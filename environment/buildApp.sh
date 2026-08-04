#!/bin/sh

global_version="0.9"


if [ -n "$1" ]; then
    global_version="$1"
else
    global_version="0.9"
fi

cd ..
mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${global_version}"
mvn  clean install -U -Dmaven.test.skip=true spring-boot:repackage -T 4
