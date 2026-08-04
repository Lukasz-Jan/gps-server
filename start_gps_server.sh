#!/bin/sh

appDirectory=`pwd`

cd ./register-server/src/main/resources
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties

cd $appDirectory/register-forwarder/src/main/resources || exit
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties

cd $appDirectory/register-responder/src/main/resources || exit
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties

cd $appDirectory/position-forwarder/src/main/resources || exit
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties

cd $appDirectory/position-server/src/main/resources || exit
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties

cd $appDirectory/position-responder/src/main/resources || exit
cp application.properties.prod application.properties.temp
mv application.properties.temp application.properties



cd $appDirectory
mvn  clean install -U -Dmaven.test.skip=true spring-boot:repackage -T 4

cd ./register-server || exit
sh dockerBuild.sh

cd $appDirectory/position-forwarder || exit
sh dockerBuild.sh

cd $appDirectory/register-forwarder || exit
sh dockerBuild.sh

cd $appDirectory/position-forwarder || exit
sh dockerBuild.sh

cd $appDirectory/position-server || exit
sh dockerBuild.sh

cd $appDirectory/position-responder || exit
sh dockerBuild.sh


cd $appDirectory/environment
docker compose -f docker-compose.yml up --remove-orphans -d

cd $appDirectory
