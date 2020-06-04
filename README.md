# About
This is an introduction to Kafka Streams with Spring Boot.

# Confluent setup
```bash
export CONFLUENT_HOME="/home/adr/tools/confluent/confluent-5.5.0"
export PATH="$CONFLUENT_HOME/bin:$PATH"
echo $CONFLUENT_HOME; echo $PATH
confluent local start
confluent cluster describe --url http://localhost:8081
confluent cluster describe --url http://localhost:8090
kafka-topics --list --zookeeper localhost:2181 | egrep -v "_|connect-"
curl http://localhost:8081/subjects
curl http://localhost:8081/subjects/sbkst.client-profiles.v2-value/versions
```
see http://localhost:9021/clusters  
(disable browser cache)

# feature/7/daily-exceeds
```bash
./run.sh | egrep -i "\"(id|dailyMaxAmount)\"|(consumed|Client profiles|Configuration|totals|Transaction):|ERROR[^s]|\s(profiles|version)\s=|(AmountExceeded|Client )\("
./create-client-profile.sh | egrep '"id"'
./create-command.sh config,profiles | grep 'Command('
./create-transactions.sh 3 | egrep '"time"'
./create-command.sh daily | grep 'Command('
./create-transactions.sh 33 | egrep '"time"'
```
- check {"dailyMaxAmount":...} occurrences
- mixing AVRO (check application.yml for default.value.serde) with Spring's JsonSerde
- simplified daily total spent report (see `CommandsConfig`)
- careful on KTable-KTable joins: for a client-profile update all dailyTotalSpentTable will be joined again!
    - run `./create-client-profile.sh | egrep '"id"'` again to see the issue 

### error
```
Caused by: com.fasterxml.jackson.databind.exc.MismatchedInputException: Expected array or string.
 at [Source: (byte[])"{"clientId":"1","time":{"year":2020,"month":"MAY","chronology":{"id":"ISO","calendarType":"iso8601"},"dayOfWeek":"SUNDAY","dayOfYear":138,"era":"CE","monthValue":5,"dayOfMonth":17,"leapYear":true}}"; line: 1, column: 24] (through reference chain: ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey["time"])
```
**solution:** import `<artifactId>spring-boot-starter-json</artifactId>`

# feature/6/windowing
```bash
./run.sh | egrep -i "\"id\"|(consumed|Client profiles|Configuration|totals|Transaction):|ERROR[^s]|\s(profiles|version)\s=|(AmountExceeded|Client )\("
./create-client-profile.sh | egrep '"id"'
./create-command.sh config,profiles | grep 'Command('
./create-transactions.sh 3 | egrep '"time"'
./create-command.sh daily | grep 'Command('
```

### highlights
- see that both `AmountExceededConfig` and `DailyTotalsConfig` need *transactions* `KStream`
    - see `ExceedsConfig` for *transactions* `KStream` declaration
- *txGroupedByClientId* is using a custom *peek* implementation
    - see `it.context` usage
    - one could use `it.context.offset` to implement a [fencing token](http://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html) 
- `CommandsConfig` needs only the store of *dailyTotalSpent* `KTable` but not the `KTable` itself!
- [Windowing](https://kafka.apache.org/25/documentation/streams/developer-guide/dsl-api.html#windowing)
- *windowedBy*: it's just *groupBy* some duration
    - see [Hopping time windows](https://kafka.apache.org/25/images/streams-time-windows-hopping.png)
    - see [Tumbling time windows](https://kafka.apache.org/25/images/streams-time-windows-tumbling.png)
- `aggregate`: output data to a `KTable` that's why later we need `toStream()`

# feature/5/joins
```bash
./run.sh | egrep -i "\"id\"|(consumed|Client profiles|Configuration|Transaction):|ERROR[^s]|\s(profiles|version)\s=|AmountExceeded\("
./create-client-profile.sh | egrep '"id"'
./create-command.sh config,profiles | grep 'Command('
./create-transactions.sh 1 | egrep '"time"'
```
### highlights
- check `AmountExceededConfig`
- [Joining](https://kafka.apache.org/25/documentation/streams/developer-guide/dsl-api.html#joining)
- [Schemas, Subjects, and Topics](https://docs.confluent.io/current/schema-registry/index.html#schemas-subjects-and-topics)
- [Schema Registry API Reference](https://docs.confluent.io/current/schema-registry/develop/api.html#sr-api-reference)
- `ProfilesConfig`
    - why `KTable`?
    - `Materialized.as` creates an in-memory store (used by `ReportsConfig`)
    - `Consumed.as`: using serde defaults (see application.yml)
    - check SpringBootKstreamsTutorialApplication annotations
- `ReportsConfig`
    - why not `KTable`?
    - `Consumed.as`: not using serde defaults
    - `commands` used with `filter` instead of `branch`
    - `<ClientProfile>allOf` relates to `Materialized.as` in `ProfilesConfig`
- `AmountExceededConfig`
    - `transactionsStream` using serde defaults
    - `join` with a `KTable` is similar to a sql inner-join
        - `amountExceededJoiner` is the values (`KStream`, `KTable`) joiner
            - outcome (kafka value) could be null
            - Kafka keys could contain business objects too! 
        - `Joined.as` used to configure serde for key and values

##### error
```
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 2.43 s <<< FAILURE! - in ro.go.adrhc.springbootkstreamstutorial.producers.ClientProfileProducerIT
[ERROR] sendClientProfile  Time elapsed: 0.859 s  <<< ERROR!
org.apache.kafka.common.errors.SerializationException: Error registering Avro schema: {"type":"record","name":"ClientProfile","namespace":"ro.go.adrhc.springbootkstreamstutorial.infrastru
```
**solution:** see /tmp/confluent.xomRjOEm/schema-registry/logs/schema-registry.log

# feature/4/ktable

### client profiles
```bash
./run.sh | egrep -i "\"id\"|consumed:|ERROR[^s]|Configuration:|\s(profiles|version)\s=|Client profiles:"
./create-client-profile.sh | tee -a profile.log | egrep '"id"'
./create-command.sh config,profiles | grep 'Command('
```
This won't work when using AVRO serde:
```bash
bin/kafka-console-producer --broker-list 127.0.0.1:9092 --topic sbkst.client-profiles.v2
# {"id": "1", "name": "Gigi", "surname": "Kent", "email": "gigikent@candva.ro", "phone": "0720000000", "dailyMaxAmount": 50, "periodMaxAmount": 150}
```

### highlights
- `TopicsConfig`: client-profiles is a compacted topic
- using `AVRO` for client-profiles topic
- check client-profiles topic schema and `cleanup.policy` in Confluent
- `ProfilesConfig` is using a KTable
- `ReportsConfig` is filtering on "config" parameter value
- observe the logged topology (search for "Topologies:")
- [Duality of Streams and Tables](https://kafka.apache.org/25/documentation/streams/core-concepts#streams-concepts-duality)
- [Interactive Queries](https://kafka.apache.org/10/documentation/streams/developer-guide/interactive-queries.html)
- check client profiles report (`ReportsConfig`) and implementation (`QueryAllSupp`)
- check [Kafka Streams DSL extensions](https://github.com/adrhc/kafka-streams-extensions)

##### error
```
InvalidDefinitionException: Cannot construct instance of `ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
```             
**solution:** use `@NoArgsConstructor` on `Command`

# Kafka setup
```bash
export KAFKA_HOME="/home/adr/tools/kafka/kafka_2.12-2.4.0"
export PATH="$PATH:$KAFKA_HOME/bin"
echo $KAFKA_HOME; echo $PATH
cd $KAFKA_HOME
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
#bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
#bin/kafka-topics.sh --list --zookeeper localhost:2181
#bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic test
```

# feature/3/classic-consumer
- use the same instructions as for `feature/2/commands` branch
- check `CommandConsumer` and how it deals with exceptions

# feature/2/commands
```bash
./run.sh | egrep -i "(consumed|received):|ERROR[^s]|Configuration:|spring profiles|app version"
# with Kafka:
bin/kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic sbkst.commands.v2
# with Confluent:
bin/kafka-console-producer --broker-list 127.0.0.1:9092 --topic sbkst.commands.v2
# use:
# {"names": ["config"]}
```

### highlights
- [Stream Processing Topology](https://kafka.apache.org/24/documentation/streams/core-concepts#streams_topology)
    - a graph of stream processors (nodes) that are connected by streams (edges)
- observe the logged topology (search for "Topologies:")
- *poison message*: any exception stops the foreach topology processing
    - [Kafka Streams can not recover in case of Exception while processing Messages](https://stackoverflow.com/questions/50388496/kafka-streams-can-not-recover-in-case-of-exception-while-processing-messages)

### other notes
- check pom.xml
- check application.yml
    - notice exception handler, threads, auto-startup
    - topology.optimization: all 
        - https://stackoverflow.com/questions/57164133/kafka-stream-topology-optimization
        - https://docs.confluent.io/current/streams/developer-guide/optimizing-streams.html#optimizing-kstreams-topologies
    - processing.guarantee: exactly_once
        - idempotency is promised only for a *single* producer session!
        - https://hevodata.com/blog/kafka-exactly-once/
        - https://cwiki.apache.org/confluence/display/KAFKA/Idempotent+Producer   
- notice `@EnableConfigurationProperties`, `Thread.sleep(Long.MAX_VALUE)`
- notice `Command implements Serializable`
- class `TopicsConfig`: enables topic creation
- `infrastructure`: see [Onion Architecture](https://jeffreypalermo.com/blog/the-onion-architecture-part-1/)
- `adapters`: see [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

# feature/1-init
- check Spring Initializr.png
- check application.properties
- check SpringBootKstreamsTutorialApplication
- check pom.xml
