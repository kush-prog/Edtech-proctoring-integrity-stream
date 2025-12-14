#!/bin/bash

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
# In a real script we might loop checking nc -z localhost 9092

# Create topics
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic exam.events --replication-factor 1 --partitions 3
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic device.signals --replication-factor 1 --partitions 3
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic suspicion.scores --replication-factor 1 --partitions 3
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic exam.events.dlq --replication-factor 1 --partitions 3
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic quarantine.events --replication-factor 1 --partitions 3
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic replay.commands --replication-factor 1 --partitions 1
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --create --if-not-exists --topic proctor.audit --replication-factor 1 --partitions 3

echo "Topics created successfully."
docker exec kafka kafka-topics --bootstrap-server kafka:29092 --list
