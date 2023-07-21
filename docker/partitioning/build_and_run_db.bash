#!/bin/bash

container_name="postgres-partitioning"

docker build . -t $container_name

docker stop $container_name || true
docker rm $container_name || true

docker run -p "5555:5432" \
  -v "/tmp/postgres:/var/lib/postgresql/data" \
  --memory "2000M" \
  --cpus "2" \
  --name $container_name $container_name \
  -c log_statement=all
