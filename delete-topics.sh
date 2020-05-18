#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

echo "KAFKA_HOME: $KAFKA_HOME"
if [ "$KAFKA_HOME" == "" ]; then
	echo "set KAFKA_HOME OS-environment variable!"
	exit 1
fi

KDELETE="$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic"

$KDELETE sbkst.commands.v2
$KDELETE sbkst.client-profiles.v2

