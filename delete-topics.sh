#!/bin/bash

# shopt -s expand_aliases
# source ~/.bash_aliases

echo "CONFLUENT_HOME: $CONFLUENT_HOME"
if [ "$CONFLUENT_HOME" == "" ]; then
	echo "set CONFLUENT_HOME OS-environment variable!"
	exit 1
fi

KDELETE="kafka-topics --delete --zookeeper 127.0.0.1:2181 --topic"

# topic deletion
$KDELETE sbkst.commands.v2
$KDELETE sbkst.client-profiles.v2
$KDELETE sbkst.transactions.v2
$KDELETE sbkst.daily-exceeds.v2
$KDELETE sbkst.daily-exceeds.v2
$KDELETE sbkst.daily-total-spent.v2
$KDELETE sbkst-dailyTotalSpentGroupedByClientId-1Days-changelog

# schema deletion
# curl http://localhost:8081/subjects
# curl http://localhost:8081/subjects/sbkst.client-profiles.v2-value/versions
curl -X DELETE http://localhost:8081/subjects/sbkst.client-profiles.v2-value
curl -X DELETE http://localhost:8081/subjects/sbkst-sbkst.client-profiles.v2-changelog-value
curl -X DELETE http://localhost:8081/subjects/sbkst-sbkst.client-profiles.v2-store-changelog-value
#curl -X DELETE http://localhost:8081/subjects/sbkst-dailyTotalSpentGroupedByClientId-1Days-changelog-value
curl -X DELETE http://localhost:8081/subjects/sbkst.transactions.v2-value
