### Dashboards

| System              | URL                        |
|---------------------|----------------------------|
| Importing Dashboard | http://localhost:3001/     |
| Kafka UI            | http://localhost:8081/     |
| Spark UI            | http://localhost:8082/     |
| Redis UI            | http://localhost:8011/     |
| Eureka UI           | http://localhost:8761/     |
| Grafana             | http://localhost:3000/     |
| Kibana              | http://localhost:5601/     |
| Prometheus          | http://localhost:9090/     |
| Prefect – UI        | http://localhost:4200/     |
| Prefect – Swagger   | http://localhost:4200/api  |
| Prefect – Docs      | http://localhost:4200/docs |

### Installation

Start everything at once:
```
docker compose -f docker-compose.platform-api.yml up -d
```

---

Alternatively, first start common containers:
```
docker compose -f docker-compose.commons.yml up -d
```

Then, selectively run desired Importing, Configuration, or Exporting APIs:
```
docker compose -f docker-compose.importing-api.yml up -d
docker compose -f docker-compose.configuration-api.yml up -d
docker compose -f docker-compose.exporting-api.yml up -d
```
