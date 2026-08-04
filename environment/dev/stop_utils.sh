#!/bin/sh

docker ps -a -f status=created -f status=running -f status=exited --format "table  {{.Names}}" \
| awk '{ print $1}' | awk 'NR>1 { print $0}' | xargs docker stop | xargs docker rm