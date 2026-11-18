# Post-contenido — Unidad 12: Despliegue y CI/CD

## Descripción

Repositorio del laboratorio de la Unidad 12 de Programación Web. Contiene la
aplicación Spring Boot del catálogo de productos (Unidad 10) dockerizada con un
Dockerfile multi-stage y perfiles dev/prod. En la Parte 2 se extenderá con un
pipeline de integración continua con GitHub Actions y monitoreo con Actuator.

## Parte 1 — Dockerización con perfiles y variables de entorno

El Dockerfile compila el proyecto con Maven en una etapa con JDK y ejecuta el
JAR en una etapa JRE Alpine con un usuario no root. La configuración sensible
se externaliza con placeholders `${VAR:default}`: sin variables de entorno la
aplicación arranca con el perfil `dev` (H2 en memoria); con
`SPRING_PROFILES_ACTIVE=prod` y las variables `DATASOURCE_URL`,
`DATASOURCE_USER` y `DATASOURCE_PASSWORD` arranca con PostgreSQL.

## Cómo construir y ejecutar

1. Construir la imagen: `docker build -t mi-spring-app:local .`
2. Perfil dev: `docker run -p 8080:8080 mi-spring-app:local`
3. Perfil prod: ver las variables de entorno indicadas arriba.