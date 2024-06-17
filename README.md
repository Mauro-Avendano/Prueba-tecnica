## Sobre la aplicación

Dada la simplicidad del caso de uso, se utiliza una arquitectura tradicional de 3 capas Controlador - Servicio - Repositorio 
en donde las dependencias del código fuente apuntan en la misma dirección del flujo de control. En caso del crecimiento de la aplicación se adoptaría 
rápidamente una arquitectura limpia con interfaces e inversión de la dependencia.

Se incluyen tests unitarios para la capa de servicio, tests de integración para el controlador y tests funcionales que pasan sobre todas las capas.

## Correr la aplicación 

Navegar hasta la ruta del proyecto y ejecutar en la terminal (debe tener correctamente configurada la variable de entorno JAVA_HOME).

```console
./gradlew build
```
```console
./gradlew bootRun
```

También puede ejecutarse directamente desde Intellij o Eclipse.

## Tests

```console
./gradlew test
```

## Swagger

http://localhost:8080/swagger-ui/index.html