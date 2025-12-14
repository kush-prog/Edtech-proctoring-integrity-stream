# EdTech Proctoring System - Verification Walkthrough

This guide details the steps to run the system locally and verify its functionality end-to-end.

## 1. Infrastructure Setup

First, we need to start the supporting infrastructure (Kafka, Redis, Postgres, MinIO, etc.).

1.  Open a terminal in the project root: `a:\Kafka Learning\EdTech-Proctoring-Integrity-Stream`
2.  Run Docker Compose:
    ```bash
    docker-compose up -d
    ```
3.  **Verify**:
    *   Check if containers are running: `docker-compose ps`
    *   Open Kafka UI: [http://localhost:8080](http://localhost:8080)
    *   Open MinIO Console: [http://localhost:9001](http://localhost:9001) (User: `minioadmin`, Pass: `minioadmin`)

## 2. Start Microservices

You need to run 3 Spring Boot services. Open 3 separate terminal windows.

**Terminal 1: Event Gateway**
```bash
cd event-gateway
mvn spring-boot:run
```
*Wait for "Started EventGatewayApplication in..."*

**Terminal 2: Stream Processor**
```bash
cd stream-processor
mvn spring-boot:run
```
*Wait for "Started StreamProcessorApplication in..."*

**Terminal 3: Audit Service**
```bash
cd audit-service
mvn spring-boot:run
```
*Wait for "Started AuditServiceApplication in..."*

## 3. Start Frontend

Open a 4th terminal for the React application.

**Terminal 4: Proctor Console**
```bash
cd proctor-console
npm install
npm run dev
```
*Access the dashboard at [http://localhost:3001](http://localhost:3001)*

---

## 4. Verification Scenarios

### Scenario A: Single Event (Happy Path)
Send a single "TAB_SWITCH" event. It should be logged but might not trigger a high suspicion score yet (threshold is 3).

**Action:**
Run this command in a new terminal (Git Bash or PowerShell):

```bash
curl -X POST http://localhost:8081/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "test-1",
    "sessionId": "session-A",
    "examId": "exam-101",
    "studentId": "student-bob",
    "eventType": "TAB_SWITCH",
    "timestamp": 1700000000000,
    "metadata": "{}"
  }'
```

**Expectation:**
*   **Event Gateway Logs**: "Publishing exam event..."
*   **Dashboard**: You might see the event in a raw feed if implemented, or no change if only showing high scores.

### Scenario B: Trigger Suspicion Rule (The "Burst" Test)
The rule `TabSwitchBurst` triggers if there are **3 tab switches in 10 seconds**. Let's send 3 events quickly.

**Action:**
Run this 3 times in rapid succession:

```bash
curl -X POST http://localhost:8081/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "burst-1",
    "sessionId": "session-B",
    "examId": "exam-101",
    "studentId": "student-alice",
    "eventType": "TAB_SWITCH",
    "timestamp": 1700000000000,
    "metadata": "{}"
  }'
```

**Expectation:**
1.  **Stream Processor Logs**: Should show computation.
2.  **Audit Service Logs**: "Received suspicion score..."
3.  **Dashboard (http://localhost:3001)**: A new row should appear for `session-B` with a score (likely 60.0 or similar) and reason "High frequency tab switching...".

### Scenario C: Device Signal
Send a camera disconnect signal.

**Action:**
```bash
curl -X POST http://localhost:8081/api/signals \
  -H "Content-Type: application/json" \
  -d '{
    "signalId": "sig-1",
    "sessionId": "session-C",
    "signalType": "CAMERA_DISCONNECT",
    "timestamp": 1700000000000,
    "severity": 10
  }'
```

**Expectation:**
*   Check logs to ensure it was routed to `device.signals`.

## 5. Troubleshooting

*   **Port 8081/8082/8083 already in use?**
    *   Stop other services or change `server.port` in `application.yml`.
*   **Kafka connection refused?**
    *   Ensure Docker is running.
    *   Restart services after Kafka is fully up (sometimes services start faster than Kafka).
*   **Frontend API errors?**
    *   Check if `audit-service` is running on port 8083.
    *   Check `vite.config.js` proxy settings.
