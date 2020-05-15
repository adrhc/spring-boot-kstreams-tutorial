# Context
It's about a person having a bank account with 2 cards attached: his own and one for his wife. The client wants to be notified when a daily-expenses threshold is exceeded or one related to a period (e.g. 3 days, 1 month, etc).
# Setup
```bash
export CONFLUENT_HOME="/home/adr/tools/confluent/confluent-5.5.0"
export PATH="$CONFLUENT_HOME/bin:$PATH"
echo $CONFLUENT_HOME; echo $PATH
confluent local start
chmod -c +x *.sh
```
see http://localhost:9021/clusters  
(disable browser cache)
# commands
```bash
./run.sh | egrep -i "client1|command received|Notification:|Overdue:|Limit:|ERROR[^s]|totals:|Configuration:|spring profiles|app version|windowSize|windowUnit|enhancements"
bin/kafka-console-producer --broker-list 127.0.0.1:9092 --topic sbkst.commands.v2
# use {"name": "report", "parameters": ["config"]}
```
