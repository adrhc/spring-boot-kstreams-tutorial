#!/bin/bash
shopt -s expand_aliases
source ~/.bash_aliases

./mvnw -DenableIT=true -DreportType=${1:-config} -Dtest=CommandProducerIT test
