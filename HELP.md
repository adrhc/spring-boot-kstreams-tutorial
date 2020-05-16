# Context
It's about a person having a bank account with 2 cards attached: his own and one for his wife. The client wants to be notified when a daily-expenses threshold is exceeded or one related to a period (e.g. 3 days, 1 month, etc).
# Setup
```bash
export CONFLUENT_HOME="/home/adr/tools/confluent/confluent-5.5.0"
export PATH="$CONFLUENT_HOME/bin:$PATH"
echo $CONFLUENT_HOME; echo $PATH
confluent local start
confluent cluster describe --url http://localhost:8081
confluent cluster describe --url http://localhost:8090
```
see http://localhost:9021/clusters  
(disable browser cache)
# commands
```bash
./run.sh | egrep -i "surname|command (consumed|received)|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"
bin/kafka-console-producer --broker-list 127.0.0.1:9092 --topic sbkst.commands.v2
# {"name": "report", "parameters": ["config"]}
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
- `infrastructure` package: see [Onion Architecture](https://jeffreypalermo.com/blog/the-onion-architecture-part-1/)
# client profiles
```bash
./create-client-profile.sh | tee -a profile.log | egrep 'surname'
```
This won't work with AVRO serde:
```bash
bin/kafka-console-producer --broker-list 127.0.0.1:9092 --topic sbkst.client-profiles.v2
# {"id": "1", "name": "Gigi", "surname": "Kent", "email": "gigikent@candva.ro", "phone": "0720000000", "dailyMaxAmount": 50, "periodMaxAmount": 150}
```
