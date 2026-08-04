#!/bin/sh

global_version="0.9"

appDirectory=`pwd`

cd ./environment
sh buildApp.sh $global_version
cd ${appDirectory}


cd ${appDirectory}/register-forwarder || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}/register-server || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}/register-responder || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}/position-forwarder || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}/position-server || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}/position-responder || exit
sh dockerBuild.sh ${global_version}

cd ${appDirectory}

docker compose -f docker-compose.yml up --remove-orphans -d
