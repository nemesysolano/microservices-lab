# Internal Transfer #

## Kafka ##
Use https://hub.docker.com/r/apache/kafka-native and then follow this process

That is fantastic news! You successfully navigated some of the most notorious networking and configuration traps in the Kafka ecosystem.

Here is a complete, step-by-step briefing of your final architecture. Save this for the next time you need to spin up a single-node Kafka environment for testing or development.

---

# Architecture Briefing: Single-Node KRaft Kafka via Docker

**Scenario:** Running a Spring Boot application on a local machine, connecting to a standalone Apache Kafka broker running inside a Docker container on a remote network host (e.g., `nas`).

### Phase 1: Deploying the Kafka Broker

Because we are using the modern `apache/kafka-native` image, it relies on **KRaft** (Kafka Raft) instead of Zookeeper. When overriding defaults to expose the broker to a remote machine, we must explicitly define the KRaft controllers, listener protocol maps, and single-node replication rules.

**1. Clean the Slate**
Always ensure no orphaned data volumes contain conflicting `localhost` metadata from previous attempts.

```bash
docker rm -f kafka
docker volume prune -f

```

**2. The Deployment Command**
Run this on the host machine (`nas`).

```bash
docker run -d \
  --name kafka \
  -p 9092:9092 \
  -e CLUSTER_ID=MkU3OEVBNTcwNTJENDM2Qk \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS="PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093" \
  -e KAFKA_ADVERTISED_LISTENERS="PLAINTEXT://nas:9092" \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP="CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT" \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  apache/kafka-native

```

**Why this configuration is required:**

* **The Network Bridge (`KAFKA_ADVERTISED_LISTENERS`):** Forces the broker to hand out `nas:9092` in its metadata, allowing remote clients to find it instead of trapping them inside the Docker container's internal IP.
* **The Protocol Map (`KAFKA_LISTENER_SECURITY_PROTOCOL_MAP`):** Explicitly tells the Kafka startup script how to parse our custom listeners, preventing it from silently falling back to defaults.
* **The Single-Node Survival (`KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1`):** Prevents the broker from crashing when creating internal tracking topics (`__consumer_offsets`). By default, Kafka demands these exist on 3 separate brokers; this lowers the requirement to 1.

### Phase 2: Native Validation

Before writing any Java code, always prove the broker is healthy using native CLI tools. **Crucial:** Always use the hostname you defined in the advertised listeners (`nas`).

**1. Open a Consumer Terminal:**

```bash
kafka-console-consumer.sh --bootstrap-server nas:9092 --topic test-topic --from-beginning

```

**2. Open a Producer Terminal:**

```bash
kafka-console-producer.sh --bootstrap-server nas:9092 --topic test-topic

```

Type a message and hit enter. If it appears in the consumer terminal, the infrastructure is 100% sound.

### Phase 3: Spring Boot Integration

Configure your `application.yml` to target the advertised listener.

```yaml
spring:
  kafka:
    # Target the advertised listener network address
    bootstrap-servers: ${KAFKA_SERVER:nas:9092}
    
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all # Ensures no data loss during transmission
        
    consumer:
      group-id: internal-transfer-group
      auto-offset-reset: earliest # Reads from the beginning if no history exists
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer

```

**Developer Tip for Spring Boot:**
Always attach a callback when using `kafkaTemplate.send()` to prevent Spring from swallowing asynchronous network or serialization errors:

```java
kafkaTemplate.send("internal-transfer", "payload").whenComplete((result, ex) -> {
    if (ex != null) {
        log.error("Failed to send message to Kafka", ex);
    }
});

```

---

You conquered the classic Kafka networking trial by fire today. Keep this briefing handy, and your future deployments will take seconds instead of hours!

## Mongo DB ##
Look in `applicaiton.yaml`