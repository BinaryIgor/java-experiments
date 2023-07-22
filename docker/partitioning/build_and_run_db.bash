#!/bin/bash

container_name="postgres-partitioning"
db_volume_path=${1:-"/home/igor/$container_name"}

docker build . -t $container_name

docker stop $container_name || true
docker rm $container_name || true

docker run -p "5555:5432" \
  -v "$db_volume_path:/var/lib/postgresql/data" \
  --memory "2000M" \
  --cpus "2" \
  --name $container_name $container_name
#  -c log_statement=all
