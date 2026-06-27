# Wumpus Server
Backend service for prompt generation behind the `/wumpus` reverse-proxy prefix on `api.rwars.steven-webber.com`.

## Stack
- Java 25
- Maven (wrapper)
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
./mvnw clean verify
```

Native build:
```bash
./mvnw -Dnative -Dquarkus.native.container-build=true verify
```

Native container image build:
```bash
./mvnw -Dnative -Dquarkus.native.container-build=true -DskipTests package
docker build -f src/main/docker/Dockerfile.native -t quarkus/wumpus-server-native .
```

## CI/CD variables and secrets
Required GitHub secret:
- `AWS_ROLE_ARN`

Required GitHub variable:
- `EC2_INSTANCE_ID`

Optional GitHub variables:
- `AWS_REGION` (default `af-south-1`)
- `WUMPUS_ECR_REPOSITORY` (default `wumpus-server`)
- `WUMPUS_CONTAINER_NAME` (default `wumpus-server`)
- `WUMPUS_CONTAINER_PORT` (default `8081`)

## Deployment topology
- Container listens on `8080` inside Docker.
- EC2 host maps `8081:8080`.
- Public endpoint is proxied by existing nginx:
  - `https://api.rwars.steven-webber.com/wumpus/...`

## One-time manual nginx runbook (SSM, run once)
Run this one-time setup outside the recurring app deployment workflow. It creates a dedicated nginx include and keeps prefix stripping for `/wumpus`.

```bash
aws ssm send-command \
  --instance-ids "$EC2_INSTANCE_ID" \
  --document-name "AWS-RunShellScript" \
  --parameters 'commands=[
    "cat > /etc/nginx/conf.d/wumpus-route.conf <<\"EOF\"",
    "location /wumpus/ {",
    "  proxy_pass http://localhost:8081/;",
    "  proxy_http_version 1.1;",
    "  proxy_set_header Host $host;",
    "  proxy_set_header X-Real-IP $remote_addr;",
    "}",
    "EOF",
    "nginx -t",
    "systemctl reload nginx"
  ]'
```

Notes:
- Keep the trailing slash in `proxy_pass http://localhost:8081/;` so nginx strips `/wumpus` and forwards `/api/prompt` correctly.
- This route update is intentionally manual and one-time, not part of each deploy run.

## Smoke tests
1. Container-local health (on EC2):
```bash
curl -sf http://localhost:8081/q/health
```

2. Routed health through nginx:
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
