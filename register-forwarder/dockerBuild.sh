#!/bin/sh

TAG="register-forwarder"

if [ "$#" -ne 1 ]; then
    echo 'Building ' ${TAG} ' image'
    echo 'Version argument required'
    exit 1
fi

APP_VERSION=$1

docker build --build-arg VERSION="${APP_VERSION}" \
             --build-arg APP_NAME="${TAG}" \
            -t ${TAG} .