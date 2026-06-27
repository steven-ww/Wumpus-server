# Wumpus Server
Backend service for prompt generation behind the `/wumpus` reverse-proxy prefix on `api.rwars.steven-webber.com`.

## Stack
- Java 25
- Maven
- Quarkus 3.36.3
- LangChain4j (`io.quarkiverse.langchain4j:quarkus-langchain4j-core`)

## API
Internal service routes (container port `8080`):
- `POST /api/prompt`
  - Request: `{"context":"..."}`
  - Response: `{"prompt":"..."}`
- `GET /q/health`
- `GET /q/openapi`
- `GET /q/swagger-ui`

Current behavior:
- Prompt generation echoes `context` via a layered design:
  - `PromptResource` → `PromptService` → `LlmGateway` (`EchoLlmGateway`)

## Build and test
```bash
mvn clean verify
```

Native build:
```bash
mvn -Dnative clean verify
```

Native container image build:
```bash
mvn -Dnative -DskipTests package
docker build -f src/main/docker/Dockerfile.native -t wumpus-server:native .
```

## Deployment topology
- Container listens on `8080` inside Docker.
- EC2 host maps `8081:8080`.
- Public endpoint is proxied by existing nginx:
  - `https://api.rwars.steven-webber.com/wumpus/...`

## One-time manual nginx runbook (SSM shell command)
Do this once on the EC2 host (not in recurring deploy workflow):

1. Configure nginx location with prefix stripping:
   - `location /wumpus/ {`
   - `    proxy_pass http://localhost:8081/;`
   - `}`
2. Validate configuration:
   - `nginx -t`
3. Reload nginx:
   - `systemctl reload nginx`

## Smoke tests
1. Local host health:
```bash
curl -sf http://localhost:8081/q/health
```

2. External health through nginx:
```bash
curl -sf https://api.rwars.steven-webber.com/wumpus/q/health
```

3. Prompt endpoint through nginx:
```bash
curl -sf -X POST https://api.rwars.steven-webber.com/wumpus/api/prompt \
  -H 'Content-Type: application/json' \
  -d '{"context":"hello"}'
```
Expected response:
```json
{"prompt":"hello"}
```
