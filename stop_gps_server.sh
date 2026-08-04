#!/bin/bash

cd ./environment
docker compose -f docker-compose.yml down
cd $current_directory