#!/bin/sh

procNo=`netstat -nlp | grep :8090 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

procNo=`netstat -nlp | grep :8082 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

procNo=`netstat -nlp | grep :8091 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

procNo=`netstat -nlp | grep :8090 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

procNo=`netstat -nlp | grep :8095 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

procNo=`netstat -nlp | grep :9000 | sed -rn "s/.* ([0-9]*)\/java/\1/p"`
kill -9 $procNo

cd ..
sh stop_utils.sh

