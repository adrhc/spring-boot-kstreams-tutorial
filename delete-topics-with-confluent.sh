#!/bin/bash

# shopt -s expand_aliases
# source ~/.bash_aliases

echo "CONFLUENT_HOME: $CONFLUENT_HOME"
if [ "$CONFLUENT_HOME" == "" ]; then
	echo "set CONFLUENT_HOME OS-environment variable!"
	exit 1
fi

KDELETE="kafka-topics --delete --zookeeper 127.0.0.1:2181 --topic"

$KDELETE sbkst.commands.v2
