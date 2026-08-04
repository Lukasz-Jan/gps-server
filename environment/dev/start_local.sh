#!/bin/sh

cd ../..
app_directory=`pwd`

mvn  clean install -Dmaven.test.skip=true spring-boot:repackage -T 4

cd $app_directory/register-server || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

cd $app_directory/register-forwarder || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

cd $app_directory/register-responder || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

cd $app_directory/position-server || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

cd $app_directory/position-forwarder || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

cd $app_directory/position-responder || exit
mvn spring-boot:run -Dspring-boot.run.profiles=dev