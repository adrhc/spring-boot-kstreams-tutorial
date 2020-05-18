#!/bin/bash

# shopt -s expand_aliases
# source ~/.bash_aliases
# cd /home/adr/tools/kafka/kafka_2.12-2.4.0
# alias kdelete='bin/kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic'

echo "CONFLUENT_HOME: $CONFLUENT_HOME"
if [ "$CONFLUENT_HOME" == "" ]; then
	echo "set CONFLUENT_HOME OS-environment variable!"
	exit 1
fi

KDELETE="kafka-topics --delete --zookeeper 127.0.0.1:2181 --topic"

$KDELETE sbkst.commands.v2
$KDELETE sbkst.client-profiles.v2
$KDELETE sbkst.transactions.v2
