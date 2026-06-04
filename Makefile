.PHONY: help test build up down clean logs

help:
	@echo "Marketplace Development Commands"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-20s %s\n", $$1, $$2}'

install: ## Install dependencies
	@for service in services/*/; do \
		if [ -f "$$service/build.gradle.kts" ]; then \
			cd "$$service" && ./gradlew dependencies --no-daemon && cd ../..; \
		fi \
	done

test: ## Run tests
	@for service in services/*/; do \
		if [ -f "$$service/build.gradle.kts" ]; then \
			echo "Testing $$service"; \
			cd "$$service" && ./gradlew test --no-daemon && cd ../..; \
		fi \
	done

test-service: ## Test specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		echo "Usage: make test-service SERVICE=cart-service"; \
		exit 1; \
	fi
	@cd services/$(SERVICE) && ./gradlew test --no-daemon

coverage: ## Generate coverage reports
	@for service in services/*/; do \
		if [ -f "$$service/build.gradle.kts" ]; then \
			cd "$$service" && ./gradlew test jacocoTestReport && cd ../..; \
		fi \
	done

build: ## Build all services
	@cd infra/docker && docker-compose -f docker-compose.yml build

build-service: ## Build specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd services/$(SERVICE) && docker build -t $(SERVICE):latest .

up: ## Start all services
	@cd infra/docker && docker-compose -f docker-compose.yml up -d

up-infra: ## Start infrastructure only
	@cd infra/docker && docker-compose -f docker-compose.yml up -d postgres redis kafka opensearch

up-service: ## Start specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.yml up -d $(SERVICE)

down: ## Stop all services
	@cd infra/docker && docker-compose -f docker-compose.yml down

down-volumes: ## Stop and remove volumes
	@cd infra/docker && docker-compose -f docker-compose.yml down -v

restart: down up ## Restart everything

restart-service: ## Restart specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.yml restart $(SERVICE)

logs: ## Show logs
	@cd infra/docker && docker-compose -f docker-compose.yml logs -f

logs-service: ## Show logs for service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.yml logs -f $(SERVICE)

ps: ## Show running containers
	@cd infra/docker && docker-compose -f docker-compose.yml ps

health: ## Check service health
	@curl -s http://localhost:8761/actuator/health | jq . || echo "Eureka: DOWN"
	@curl -s http://localhost:8081/actuator/health | jq . || echo "API Gateway: DOWN"

clean: ## Clean build artifacts
	@for service in services/*/; do \
		if [ -f "$$service/build.gradle.kts" ]; then \
			cd "$$service" && ./gradlew clean && cd ../..; \
		fi \
	done

clean-docker: ## Remove Docker images
	@docker-compose -f infra/docker/docker-compose.yml down -v --rmi all

prune: ## Prune Docker system
	@docker system prune -af --volumes

dev-cart: ## Run cart-service locally
	@cd services/cart-service && ./gradlew bootRun

dev-auth: ## Run auth-service locally
	@cd services/auth-service && ./gradlew bootRun

ci-test: test build ## Simulate CI/CD locally

GW_PORT ?= 8000

k8s-gw: ## Port-forward gateway via ingress with auto-restart (http://localhost:$(GW_PORT))
	@echo "Gateway → http://localhost:$(GW_PORT)   (Ctrl+C to stop)"
	@while true; do \
		kubectl port-forward -n ingress-nginx svc/ingress-nginx-controller $(GW_PORT):80; \
		echo "port-forward dropped — restarting in 2s..."; \
		sleep 2; \
	done

K8S_NODE   ?= desktop-control-plane
REGISTRY   ?= ghcr.io/openjoyer/openmarket
K8S_SERVICES = eureka-server api-gateway auth-service cart-service order-service

k8s-load: ## Build images, retag to registry name, load into the cluster (SERVICE=all|<svc>)
	@if [ -z "$(SERVICE)" ]; then echo "Usage: make k8s-load SERVICE=all   (or SERVICE=order-service)"; exit 1; fi
	@svcs="$(if $(filter all,$(SERVICE)),$(K8S_SERVICES),$(SERVICE))"; \
	for s in $$svcs; do \
		echo ">>> $$s: build → retag → load"; \
		docker compose -f infra/docker/docker-compose.yml build $$s || exit 1; \
		docker tag openmarket-$$s:latest $(REGISTRY)/$$s:latest; \
		docker save $(REGISTRY)/$$s:latest | docker exec -i $(K8S_NODE) ctr -n k8s.io images import - || exit 1; \
		kubectl rollout restart deploy -n openmarket $$s 2>/dev/null || true; \
	done; \
	echo "Done. Pods restarting with fresh images."

.DEFAULT_GOAL := help