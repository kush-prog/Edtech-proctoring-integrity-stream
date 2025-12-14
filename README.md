# EdTech Proctoring Integrity Stream

A real-time Kafka-powered proctoring system that detects suspicious student behavior during online exams.

## Architecture

- **Event Gateway**: Ingests exam events and device signals.
- **Stream Processor**: Kafka Streams app for CEP and suspicion scoring.
- **Audit Service**: Persists logs, handles DLQ, and manages replays.
- **Proctor Console**: React dashboard for real-time monitoring.
- **Infrastructure**: Kafka, Redis, Postgres, MinIO, Prometheus, Grafana.

## Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven
- Node.js 18+

## Getting Started

### 1. Start Infrastructure

```bash
docker-compose up -d
```

Wait for all services to be healthy. Kafka UI is available at http://localhost:8080.

### 2. Start Microservices

Open 3 separate terminals:

**Event Gateway:**
```bash
cd event-gateway
mvn spring-boot:run
```

**Stream Processor:**
```bash
cd stream-processor
mvn spring-boot:run
```

**Audit Service:**
```bash
cd audit-service
mvn spring-boot:run
```

### 3. Start Frontend

```bash
cd proctor-console
npm install
npm run dev
```

Access the dashboard at http://localhost:3001.

## Usage

### Simulate Events

You can use `curl` to send events to the Gateway:

```bash
# Normal Event
curl -X POST http://localhost:8081/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt-1",
    "sessionId": "session-123",
    "examId": "exam-101",
    "studentId": "student-001",
    "eventType": "TAB_SWITCH",
    "timestamp": 1672531200000,
    "metadata": "{}"
  }'

# Suspicious Burst (Run 3 times quickly)
curl -X POST http://localhost:8081/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt-2",
    "sessionId": "session-123",
    "examId": "exam-101",
    "studentId": "student-001",
    "eventType": "TAB_SWITCH",
    "timestamp": 1672531200000,
    "metadata": "{}"
  }'
```

### Check Dashboard

Go to http://localhost:3001 to see the suspicion scores appear in real-time.

### Monitoring

- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090
