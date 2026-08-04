#!/bin/sh

docker run --name active_mq -p 61616:61616 -p 8161:8161 -d apache/activemq-classic:5.19.2
current_directory=`pwd`

cd ../..
app_directory=`pwd`

cd $current_directory || exit
sh start_mongo_container.sh

cd $app_directory/register-server/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

cd $app_directory/register-forwarder/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

cd $app_directory/register-responder/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

cd $app_directory/position-server/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

cd $app_directory/position-forwarder/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

cd $app_directory/position-responder/src/main/resources || exit
cp application.properties.dev application.properties.temp
mv application.properties.temp application.properties
cd $app_directory || exit

mvn  clean install -Dmaven.test.skip=true spring-boot:repackage

cd $app_directory/register-server || exit
mvn spring-boot:run &

cd $app_directory/register-forwarder || exit
mvn spring-boot:run &

cd $app_directory/register-responder || exit
mvn spring-boot:run &

cd $app_directory/position-server || exit
mvn spring-boot:run &

cd $app_directory/position-forwarder || exit
mvn spring-boot:run &

cd $app_directory/position-responder || exit
mvn spring-boot:run