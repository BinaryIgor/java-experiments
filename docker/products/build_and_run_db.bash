#!/bin/bash

docker build . -t postgres-products

docker stop postgres-products
docker rm postgres-products
docker run -p "5555:5432" --name postgres-products postgres-products
