.PHONY: up down build run test all
all: build run
up:
	docker-compose up -d
	@echo "PostgreSQL container started"

down:
	docker-compose down
	@echo "PostgreSQL container stopped"

build:
	./mvnw clean package

run: up
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

test: up
	./mvnw test

clean:
	./mvnw clean
	docker-compose down -v
	@echo "Cleaned all resources"