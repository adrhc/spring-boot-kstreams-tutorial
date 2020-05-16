#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

# KAFKA_HOME
cd /home/adr/tools/kafka/kafka_2.12-2.4.0

# alias kdelete='bin/kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic'
kdelete sbkst.commands.v2
kdelete sbkst.commands.v2
