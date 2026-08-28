# M321 Order Service

Kleine Spring-Boot-Anwendung zur Demonstration synchroner REST-Kommunikation
und asynchroner Verarbeitung mit RabbitMQ. Ein Client gibt eine Bestellung per
HTTP ab und erhält sofort `202 Accepted`. Die eigentliche Verarbeitung erfolgt
anschliessend unabhängig über RabbitMQ.

```text
POST /api/orders
       |
       v
  OrderProducer
       |
       v
orders.processing (durable Queue / Round Robin)
       |
   +---+---+
   |       |
Worker 1  Worker 2
   |       |
   +---+---+
       |
       v
orders.processed (Fanout Exchange)
       |
   +---+----------------+
   |                    |
orders.notification   orders.audit
   |                    |
NotificationConsumer  AuditConsumer
```

## Nachrichtenfluss

`OrderProducer` publiziert eine `OrderMessage` als JSON in die dauerhafte Queue
`orders.processing`. `OrderWorker1` und `OrderWorker2` sind konkurrierende
Consumer derselben Queue: RabbitMQ gibt jede Bestellung genau einem Worker
(Round Robin/Work Queue). Jeder Worker simuliert standardmässig drei Sekunden
Bearbeitungszeit.

Nach erfolgreicher Verarbeitung publiziert der Worker ein
`OrderProcessedEvent` an den Fanout Exchange `orders.processed`. Dieser legt je
eine Kopie in `orders.notification` und `orders.audit`. Deshalb erhalten der
`NotificationConsumer` und der `AuditConsumer` dasselbe Ereignis.

OpenAPI in [`openapi.yaml`](openapi.yaml) beschreibt den synchronen HTTP-
Einstieg. AsyncAPI in [`asyncapi.yaml`](asyncapi.yaml) beschreibt die
asynchronen Channels und JSON-Nachrichten. RabbitMQ entkoppelt Annahme und
Verarbeitung; wartende Bestellungen können in der Queue gespeichert werden.

## Start

Docker Desktop starten und im Projektverzeichnis ausführen:

```powershell
docker compose up -d
docker compose ps
```

- RabbitMQ AMQP: `localhost:5672`
- Management UI: http://localhost:15672
- Benutzer/Passwort: `guest` / `guest`

Anwendung starten:

```powershell
.\mvnw.cmd spring-boot:run
```

RabbitMQ stoppen beziehungsweise wieder starten:

```powershell
docker compose stop
docker compose start
```

## Bestellung senden

```powershell
$body = @{ product = "Keyboard"; quantity = 2 } | ConvertTo-Json
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8081/api/orders `
  -ContentType application/json `
  -Body $body
```

Antwort mit HTTP 202:

```json
{
  "orderId": 101,
  "product": "Keyboard",
  "quantity": 2,
  "createdAt": "2026-09-04T10:00:00Z"
}
```

Beispiel-Logs:

```text
Order 101 accepted: Keyboard x2
OrderWorker1 started processing order 101
OrderWorker1 finished processing order 101
Notification sent for order 101
Audit entry created for order 101
```

## Automatische Demo und Konfiguration

Der Demo-Scheduler erzeugt abwechslungsweise Keyboard, Mouse, Monitor, Headset
und SSD. Er ruft direkt den `OrderProducer` auf und verwendet keine internen
HTTP-Requests.

```properties
messaging.auto-producer.enabled=true
messaging.auto-producer.interval=1800
messaging.consumer.processing-time=3000
spring.rabbitmq.listener.simple.prefetch=1
```

Mit `enabled=false` lässt sich die automatische Produktion deaktivieren. Ein
kleineres Producer-Intervall oder eine grössere Bearbeitungszeit erzeugt
schneller einen Backlog. `prefetch=1` sorgt dafür, dass jeder Worker höchstens
eine noch nicht bestätigte Bestellung reserviert.

## RabbitMQ Management UI beobachten

Unter **Queues and Streams → orders.processing** bedeuten:

- **Ready:** wartet noch auf einen Worker.
- **Unacked:** wird gerade verarbeitet; bei zwei Workern normalerweise bis zu 2.
- **Total:** Ready plus Unacked.

Unter **Exchanges → orders.processed → Bindings** sind die beiden unabhängigen
Queues `orders.notification` und `orders.audit` sichtbar.

## Backlog, Stop und Restart demonstrieren

1. Anwendung mit aktiviertem Demo-Producer starten.
2. Im UI `orders.processing` öffnen und die Message Rates beobachten.
3. Die Consumer stoppen, während REST und Producer weiterlaufen:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/consumers/stop
```

4. `Consumers` fällt auf 0, `Unacked` wird 0 und `Ready` steigt. Die durable
   Queue speichert die JSON-Bestellungen.
5. Consumer wieder starten:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/consumers/start
```

6. Zwei Worker verarbeiten wieder parallel; `Unacked` liegt typischerweise bei
   2. Die Logs zeigen, welcher Worker welche Order erhalten hat.
7. Zum vollständigen Abbau den Service mit deaktiviertem Producer neu starten:

```powershell
.\mvnw.cmd spring-boot:run `
  "-Dspring-boot.run.arguments=--messaging.auto-producer.enabled=false"
```

Dann sinkt `Ready` schrittweise auf 0. Für einen stärkeren Backlog kann
`messaging.auto-producer.interval` beispielsweise auf `500` gesetzt werden.

## Verbindungswerte

Die RabbitMQ-Werte können über `RABBITMQ_HOST`, `RABBITMQ_PORT`,
`RABBITMQ_USERNAME` und `RABBITMQ_PASSWORD` überschrieben werden. Es wird keine
Datenbank benötigt; Order-IDs werden für diese Schul-Demo im Speicher erzeugt
und beginnen nach einem Anwendungsneustart wieder bei 101.
