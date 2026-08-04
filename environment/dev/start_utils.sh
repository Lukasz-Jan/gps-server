#!/bin/bash

container_name=mongodbsrv

if docker inspect "$container_name" > /dev/null 2>&1; then
    echo "The container $container_name exists."
    # Check if the container is running
    if $(docker inspect -f '{{.State.Status}}' "$container_name" | grep -q "running"); then
        echo "The container $container_name is running."
    else
        echo "Starting mongodb."
        docker start mongodb
    fi
else
    echo "The container $container_name does not exist - creating."
    # Create and start the container if it does not exist
    echo "Starting mongodb."
    docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
fi

docker run --name active-mq -p 61616:61616 -p 8161:8161 -d apache/activemq-classic:5.19.2


