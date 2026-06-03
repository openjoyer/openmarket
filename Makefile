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
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml build

build-service: ## Build specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd services/$(SERVICE) && docker build --build-context contracts=../../contracts -t $(SERVICE):latest .

up: ## Start all services
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml up -d

up-infra: ## Start infrastructure only
	@cd infra/docker && docker-compose -f docker-compose.infra.yml up -d

up-service: ## Start specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.app.yml up -d $(SERVICE)

down: ## Stop all services
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml down

down-volumes: ## Stop and remove volumes
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml down -v

restart: down up ## Restart everything

restart-service: ## Restart specific service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.app.yml restart $(SERVICE)

logs: ## Show logs
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml logs -f

logs-service: ## Show logs for service (SERVICE=cart-service)
	@if [ -z "$(SERVICE)" ]; then \
		echo "Error: SERVICE not specified"; \
		exit 1; \
	fi
	@cd infra/docker && docker-compose -f docker-compose.app.yml logs -f $(SERVICE)

ps: ## Show running containers
	@cd infra/docker && docker-compose -f docker-compose.infra.yml -f docker-compose.app.yml ps

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
	@docker-compose -f infra/docker/docker-compose.infra.yml -f infra/docker/docker-compose.app.yml down -v --rmi all

prune: ## Prune Docker system
	@docker system prune -af --volumes

dev-cart: ## Run cart-service locally
	@cd services/cart-service && ./gradlew bootRun

dev-auth: ## Run auth-service locally
	@cd services/auth-service && ./gradlew bootRun

ci-test: test build ## Simulate CI/CD locally

.DEFAULT_GOAL := help