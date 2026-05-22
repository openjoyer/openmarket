# OpenMarket

[![CI](https://github.com/openjoyer/openmarket/actions/workflows/ci-cd.yml/badge.svg?branch=develop)](https://github.com/openjoyer/openmarket/actions/workflows/ci-cd.yml)
![Java 21](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin_DSL-02303A?style=flat-square&logo=gradle&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=flat-square&logo=apachekafka&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

## Backend интернет-магазина на Spring-сервисах

OpenMarket - учебный backend маркетплейса с микросервисной структурой. Проект собирается вокруг Spring Boot сервисов, service discovery через Eureka и API Gateway для внешних запросов.

Проект находится в активной разработке

## Сервисы

| Модуль | Назначение |
| --- | --- |
| `api-gateway` | Входная точка для API, маршрутизация и rate limiting |
| `eureka-server` | Service discovery для backend-сервисов |
| `auth-service` | Контур аутентификации и пользовательских auth-сценариев |
| `cart-service` | Корзина и checkout-view для внутренних вызовов |
| `order-service` | Оформление заказов и интеграция с корзиной |
| `catalog-service` | Каталог товаров и поиск |
| `contracts` | Общие DTO для взаимодействия сервисов |

## Стек

- Java 21, Spring Boot 4, Spring Cloud
- Gradle Kotlin DSL
- PostgreSQL, Redis, 
- Kafka
- Docker
- GitHub Actions для PR-проверок

## Структура

```text
services/      backend-сервисы
contracts/     общие межсервисные контракты
infra/docker/  compose-файлы инфраструктуры и приложений
docs/          документация проекта
```

## Запуск

Основные команды разработки вынесены в `Makefile`:

| Команда | Что делает |
| --- | --- |
| `make` | Показывает доступные команды |
| `make up` | Поднимает инфраструктуру и app-сервисы через Docker Compose |
| `make up-infra` | Поднимает только инфраструктуру |
| `make test` | Запускает тесты всех сервисов |
| `make test-service SERVICE=cart-service` | Запускает тесты одного сервиса |
| `make logs` | Показывает логи compose-сервисов |
| `make down` | Останавливает compose-сервисы |

Прямой запуск через Compose тоже доступен; манифесты лежат в `infra/docker`:

```sh
docker compose -f infra/docker/docker-compose.yml up --build
```

Проверить отдельный сервис можно его Gradle wrapper:

```sh
cd services/cart-service
./gradlew test
```
