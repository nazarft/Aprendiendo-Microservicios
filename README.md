# Aprendiendo-Microservicios

## Antes de empezar

*La finalidad de este repositorio es entender como funcionan los microservicios. El código intenta ser lo más simple posible y evitar usar conceptos que no son necesarios para entender el funcionamiento
de los microservicios. He intentado evitar el uso de Lombok,Jpa,BBDDs, etc y centrarme únicamente en lo importante.*

## Introducción
Cuando diseñamos una aplicación, se nos viene a la mente dos enfoques principales: **Monolítica y Microservicios**.

# Arquitectura monolítica
## 📝 ¿Qué es?
Una arquitectura monolítica es aquella en la que toda la aplicación está construida como una única unidad de software. La interfaz de usuario, la lógica de negocio y la base de datos están juntas en una única aplicación.

📦 Características:

* Un solo código base: Toda la aplicación se desarrolla y despliega como un único proyecto.
* Base de datos centralizada: Una única base de datos para toda la aplicación.
* Despliegue conjunto: Si hay un cambio en una parte de la aplicación, es necesario desplegar todo el sistema nuevamente.

| **Ventajas** | **Desventajas** | **Casos de Uso Ideales** |
|--------------|-----------------|--------------------------|
| ✔️ Más fácil de desarrollar, probar y depurar al inicio. | ❌ Escalabilidad limitada: no puedes escalar partes individuales del sistema. | Proyectos pequeños o medianos. |
| ✔️ Despliegue sencillo: todo en un solo lugar. | ❌ Difícil de mantener a medida que crece el código. | Equipos pequeños de desarrollo. |
| ✔️ Rendimiento eficiente al evitar llamadas a través de la red. | ❌ Una falla puede tumbar toda la aplicación. | Aplicaciones con requisitos estables y pocos cambios. |
| ✔️ Menor sobrecarga operativa (menos servicios que administrar). | ❌ Tiempo de despliegue más largo conforme aumenta la complejidad. |  |


# Arquitectura de microservicios

Con este tipo de arquitectura, tu aplicación se divide en aplicaciones más pequeñas cada una responsable de una tarea específica.

📦 Características:

Desacoplamiento: Cada servicio funciona de forma independiente.

Comunicación vía APIs: Los servicios interactúan a través de HTTP/REST, gRPC, o colas de mensajes.

Escalabilidad independiente: Puedes escalar solo los servicios que lo necesiten.

Despliegue independiente: Cada servicio se puede actualizar sin afectar a los demás.

| **Ventajas** | **Desventajas** | **Casos de Uso Ideales** |
|--------------|-----------------|--------------------------|
| ✔️ Escalabilidad independiente para cada servicio. | ❌ Mayor complejidad operativa y de implementación. | Aplicaciones grandes y complejas. |
| ✔️ Mayor flexibilidad tecnológica (diferentes lenguajes y bases de datos). | ❌ Problemas de latencia en la comunicación entre servicios. | Equipos de desarrollo distribuidos. |
| ✔️ Mejor tolerancia a fallos: si un servicio falla, no colapsa todo el sistema. | ❌ Más difícil de depurar y hacer seguimiento de errores. | Necesidad de escalabilidad frecuente. |
| ✔️ Equipos independientes pueden trabajar en diferentes servicios simultáneamente. | ❌ Sobrecarga en el despliegue y gestión de múltiples servicios. | Entornos con requisitos cambiantes. |


<img width="1191" alt="image" src="https://github.com/user-attachments/assets/59604fe0-bc89-47a2-80c9-829f20a23f4f" />

## ¿Cómo manejamos esta complejidad?

<img width="972" alt="image" src="https://github.com/user-attachments/assets/d7abca4a-5a1f-4d19-802d-475ee1a78da0" />

## Inicio: la aplicación a crear

La idea de este repositorio será construir un catálogo de valoraciones de películas:

<img width="1293" alt="image" src="https://github.com/user-attachments/assets/39f54f52-4e3d-4ac2-be32-70c0b4386fc7" />

Para ello crearemos los siguientes servicios:

<img width="1420" alt="image" src="https://github.com/user-attachments/assets/cdaed95f-ab85-44d2-9a06-1a48bcf04d33" />

## Primer paso

En primer lugar, queremos obtener los detalles de las películas, así que empezaremos con que nuestro MovieCatalogService llame a MovieInfoService:

Para ello, haremos uso de RestTemplate:

```java
@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final RestTemplate restTemplate;

    @Autowired
    public MovieCatalogController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );
        return ratings.stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        }).toList();
    }

}
```

Si te fijas, también será necesario tener las clases Movie y Rating, asi que copiaremos y pegaremos tal cual los modelos que hemos creado en los otros dos servicios:

<img width="243" alt="image" src="https://github.com/user-attachments/assets/00a68ce1-a9e4-436d-8faa-4ab919aa6f5f" />

🧠 Consejo:

Haremos uso de los @Bean de Spring para poder hacer uso de la misma instancia.

```java
@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

✅ ¿Por qué crear un Bean de RestTemplate?

* Reutilización: Puedes reutilizar la misma instancia en diferentes partes de la aplicación.

* Configuración personalizada: Puedes agregar interceptores, administradores de errores o configuraciones adicionales.

* Inyección de dependencias: Facilita la inyección de la instancia usando @Autowired.

### WebClient y el futuro de la programación reactiva

Tanto RestTemplate como WebClient son herramientas para consumir APIs en aplicaciones Spring Boot, pero tienen diferencias clave en su diseño, uso y casos recomendados.

| **RestTemplate** | **WebClient** |
|------------------|---------------|
| ✅ Usa un modelo de programación bloqueante y sincrónico. | ✅ Soporta programación reactiva y no bloqueante. |
| ✅ Cada llamada bloquea el hilo hasta que obtiene una respuesta. | ✅ Permite manejar asincronía de forma eficiente. |
| ✅ Adecuado para aplicaciones más simples o con pocas llamadas HTTP. | ✅ Optimiza el uso de recursos y escala mejor en aplicaciones con alta concurrencia. |
| ✅ Está marcado como @Deprecated en las últimas versiones de Spring. | ✅ Compatible con Mono y Flux (paradigmas reactivos). |
| ✅ Ideal para aplicaciones monolíticas o heredadas. | ✅ Es la opción recomendada por Spring para aplicaciones modernas. |

### 🧠 ¿Cuál elegir?
| **Tipo de Aplicación** | **Recomendación** |
|------------------------|-------------------|
| 🟢 Para nuevas aplicaciones: | ✅ Usa WebClient. |
| 🟡 Para aplicaciones existentes con RestTemplate: | ⚠️ No migres sin una necesidad clara. |
|                        | 🔄 Puedes combinar ambos en un proceso de transición gradual. |
| 🔵 Para aplicaciones altamente concurrentes o reactivo por naturaleza: | ✅ WebClient es la opción indiscutible. |

### Código con WebClient

```java

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogController( WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );
        return ratings.stream().map(rating -> {
            /*Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());*/
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        }).toList();
    }

}
```

Y la configuracion sería:

```java

@Configuration
public class WebClientConfiguration {
     @Bean
     public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
     }
}
```

## Segundo paso: Recuperar la información de Ratings

En nuestro RatingDataService lo que haremos será crear un endpoint donde devolveremos una lista de **Ratings**:
```java

@RestController
@RequestMapping("/ratingsdata")
public class RatingDataServiceController {
    @RequestMapping("/{movieId}")
    public Rating getRating(@PathVariable("movieId") String movieId) {
        return new Rating(movieId, 4);
    }

    @RequestMapping("/user/{userId}")
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        List<Rating> ratings = List.of(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );
        UserRating userRating = new UserRating();
        userRating.setUserRatings(ratings);
        return userRating;
    }
}
```

### 👀 Observaciones: 
Si te fijas, mi endpoint no devuelve una lista de ratings, si no un objeto **UserRatings**. ¿Por qué?, muy sencillo
queremos evitar devolver una lista para asi no tener que parametizar los datos:

```java
public class UserRating {
    private List<Rating> userRatings;

    public List<Rating> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(List<Rating> userRatings) {
        this.userRatings = userRatings;
    }
}
```
📝 Escenario con Rating sin UserRating:

Si decides añadir datos del usuario (userId, userName) directamente en cada objeto Rating, tendrás que repetirlos en cada instancia.

```java

public class Rating {
    private String movieId;
    private int rating;
    private String userId;
    private String userName;
}

```

```
[
    { "movieId": "1234", "rating": 4, "userId": "u123", "userName": "Juan Pérez" },
    { "movieId": "5678", "rating": 3, "userId": "u123", "userName": "Juan Pérez" }
]
```
### 🚨 Problema:

**userId y userName están repetidos en cada calificación.**

Si el usuario tiene 100 calificaciones, ¡estás repitiendo la misma información 100 veces!

Esto no es eficiente ni escalable. Además, si el nombre del usuario cambia, tendrás que actualizar las 100 instancias.


El resultado: 
```java
@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogController(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://localhost:8082/ratingsdata/user/" + userId,
                UserRating.class);

        return ratings
                .getUserRatings()
                .stream()
                .map(rating -> {
                   // For each movie ID, call movie info service and get details
                    Movie movie = restTemplate.getForObject("http://localhost:8081/movies/" + rating.getMovieId(),
                            Movie.class);
                    // Put them all together
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());

                   /* Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8081/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());*/

        }).toList();
    }

}
```
## ¿Qué tenemos hasta ahora?

<img width="933" alt="image" src="https://github.com/user-attachments/assets/3441dc3b-9aee-4bbb-a9f7-9e381c8fe4ea" />

🤦🏻‍♂️ Sin embargo, hay una cosa muy importante que estamos haciendo mal y es la práctica del **"hard-coding URLs"** o dicho de otro modo, escribir directamente las URLs en el código fuente de una aplicación, 
en lugar de obtenerlas de una configuración externa o de variables de entorno. Esto puede dificultar el mantenimiento y la flexibilidad del código, ya 
que cualquier cambio en las URLs requerirá modificar el código fuente y volver a desplegar la aplicación. En general tenemos estos problemas:

* Los cambios requieren cambiar el código fuente
  
* Cuando despliegas algo en la nube, obtienes URLs dinámicas (no sabes la URL que vas a obtener)
  
* Balance de carga
  
* Múltiples entornos

### ¿Cuál es la solución?
Para solucionar este problema usaremos algo llamado **"Server discovery"**.

<img width="1149" alt="image" src="https://github.com/user-attachments/assets/d35ee22c-a07c-4c10-b849-855b66e8bc0e" />

Esto es lo que llamamos Server discovery del lado **cliente**.

## Netflix Eureka Server

Netflix Eureka es un servicio de descubrimiento (Service Discovery) de aplicaciones diseñado para entornos de microservicios. Forma parte del conjunto de herramientas de Netflix OSS (Open Source Software) y es ampliamente utilizado en arquitecturas distribuidas para simplificar la comunicación entre microservicios.

📚 **¿Por qué es necesario un servicio de descubrimiento?**

En una arquitectura de microservicios, las aplicaciones están divididas en múltiples servicios pequeños e independientes que se comunican entre sí a través de la red.

🛑 Problema sin Eureka:

* Direcciones IP dinámicas: Los servicios suelen desplegarse en contenedores o máquinas virtuales con direcciones IP que pueden cambiar dinámicamente.

* Escalabilidad: A medida que se añaden o eliminan instancias de servicios, la lista de direcciones IP cambia constantemente.

* Complejidad: Los servicios necesitan saber dónde y cómo encontrar otros servicios.

✅ Solución con Eureka:

* Registro de Servicios: Cada microservicio se registra en el Eureka Server cuando se inicia.

* Descubrimiento de Servicios: Los microservicios pueden consultar a Eureka para obtener la dirección y el puerto de otros servicios.

* Load Balancing: Eureka se integra con herramientas como Ribbon para distribuir las solicitudes entre múltiples instancias de un servicio.

**🖥️ Ejemplo básico de Eureka Server en Spring Boot**

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```
Es importante tambien añadir en el application properties:

```
eureka.client.registerWithEureka=false
eureka.client.fetchRegistry=false
server.port=8761
```
## Convertir nuestros servicios en clientes 

Tenemos que convertir tanto MovieInfoService, como RatingInfoService y MovieCatalogService en **clientes**:

```java
<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
Y si ejecutamos por ejemplo MovieInfoService, podemos ver en nuestra pagina de Eureka-Server lo siguiente:
<img width="1680" alt="image" src="https://github.com/user-attachments/assets/a23733e7-f07d-4dba-bc10-3ff363cdc70a" />

### ¿Cómo consumimos nuestros servicios?

Nuestra "aplicación central", es decir, MovieCatalog, necesita consumir los otros servicios de forma dinámica. Para ello:
```java
@Configuration
public class RestTemplateConfiguration {
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```
En nuestro caso, como tenemos una configuración específica del LoadTemplate ( si usamos WebClient también usaremos la misma anotación @LoadBalanced.

### ¿Qué hace @LoadBalanced?
La anotación @LoadBalanced le indica a Spring que el RestTemplate debe tener habilitado el balanceo de carga de las instancias de servicio registradas en Eureka o cualquier otro registro de servicios que estés utilizando.

Esto permite que, cuando tu aplicación haga una solicitud a otro servicio (por ejemplo, a través de RestTemplate), Spring Cloud se encargue de redirigir la solicitud a una de las instancias del servicio disponible en lugar de realizar una solicitud directa a una URL estática.

**Ejemplo**: *Si tienes un servicio llamado movie-catalog-service registrado en Eureka, en lugar de hacer una solicitud directa a http://localhost:8080/movies, podrías hacer una solicitud como http://movie-catalog-service/movies (sin especificar el puerto ni la IP). Spring Cloud se encargará de resolver la URL y dirigir la solicitud a una de las instancias disponibles, incluso si hay múltiples instancias de movie-catalog-service funcionando en diferentes puertos o máquinas.*

Nuestro controlador de MovieCatalog quedaría así:

```java
@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public MovieCatalogController(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId,
                UserRating.class);

        /*List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );*/
        return ratings
                .getUserRatings()
                .stream()
                .map(rating -> {
                   // For each movie ID, call movie info service and get details
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(),
                            Movie.class);
                    // Put them all together
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());

                   /* Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8081/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();
                    return new CatalogItem(movie.getName(), "Desc", rating.getRating());*/

        }).toList();
    }
}
```
Fíjate!!. Ya no hacemos llamadas a URLs estáticas si no que este enfoque facilita la escalabilidad y el balanceo de carga sin necesidad de gestionar direcciones de IP o puertos manualmente.

<img width="1329" alt="image" src="https://github.com/user-attachments/assets/c4837d3d-852c-4631-b483-d161337d76ea" />

**¿Cómo gestiona Spring Cloud el balanceo de carga?:**
Cuando tienes varias instancias de un servicio (en este caso, movie-info-service), y haces una llamada usando un RestTemplate con @LoadBalanced, Spring Cloud, 
a través de un cliente de balanceo de carga como *Ribbon* (integrado en Spring Cloud), selecciona dinámicamente una instancia disponible para realizar la solicitud. Esto ocurre en segundo plano ¡sin que el desarrollador tenga que preocuparse por cómo se distribuyen las solicitudes entre las instancias!.

## Flujo de nuestro proyecto por ahora

Tenemos tres servicios principales:

**1. Movie Catalog Service (/catalog)**
   
**2. Ratings Data Service (/ratingsdata)**
   
**3.Movie Info Service (/movies)**

### Flujo de llamadas entre microservicios:

1. El usuario llama al servicio Movie Catalog Service:

* Aquí, el usuario solicita un catálogo de películas para un ID de usuario específico.
  
* El MovieCatalogController obtiene las calificaciones de las películas para ese usuario llamando al Ratings Data Service.
  
2. Movie Catalog Service llama a Ratings Data Service:

* Se obtiene una lista de películas y sus calificaciones para el usuario dado.
  
* El RatingDataServiceController devuelve una lista de calificaciones.

```
{
  "userRatings": [
    { "movieId": "MOV101", "rating": 5 },
    { "movieId": "MOV102", "rating": 4 }
  ]
}
```

3. Movie Catalog Service llama a Movie Info Service:

Por cada movieId en la lista anterior, se hace una llamada al servicio de información de películas:

El MovieController devuelve la información de cada película.
```
{
  "movieId": "MOV101",
  "name": "El Señor de los Anillos",
  "descrption": "Un anillo para gobernarlos a todos"
}
```

4. Movie Catalog Service construye la respuesta final:

Con las calificaciones del Ratings Data Service y los detalles del Movie Info Service, el catálogo final se compone.

```
[
  {
    "name": "El Señor de los Anillos",
    "description": "Desc",
    "rating": 5
  },
  {
    "name": "Interestelar",
    "description": "Desc",
    "rating": 4
  }
]
```

**🔑 Puntos clave para entender el flujo**

1.Movie Catalog Service es el intermediario principal: Llama a los otros dos servicios (Ratings Data y Movie Info) para obtener los datos necesarios.

2.Ratings Data Service: Proporciona una lista de calificaciones para un usuario.

3.Movie Info Service: Proporciona detalles para cada movieId.

## Tolerancia a fallos y resilencia

La tolerancia a fallos es la capacidad que tiene nuestra aplicación de "sobrevivir" o "soportar" un fallo.

La resilencia es como nuestra aplicación puede adaptarse a los fallos para poder seguir funcionando.

**¿Cómo haces nuestra aplicación mas resiliente?**

Imagíate un escenario en el que nuestro servicio de RatingsDataService se cae:

<img width="941" alt="image" src="https://github.com/user-attachments/assets/30f83b90-ff27-4b91-a4e4-c3afb30be43a" />

👀 **¿La solución?:** Crea varias instancias del mismo servicio!

<img width="941" alt="image" src="https://github.com/user-attachments/assets/5caac7e3-a4bc-484e-a00a-765242bf3582" />

## Más problemas: lentitud en los servicios

Que un servicio vaya lento puede ser un quebradero de cabeza incluso mayor al que un servicio se caiga. Si un servicio va lento, nuestra aplicación puede verse ralentizada en otros ámbitos. 
Pongamos este ejemplo:
<img width="1309" alt="image" src="https://github.com/user-attachments/assets/f78c8cb0-6137-4923-888a-0bf21972a188" />
Imagina que ahora nuestro servicio que trae la información de las películas hace una llamada a una base de datos (que es como se haría en realidad pero en nuestra aplicación no se ha hecho
para buscar sencillez). Si hay problemas que hace que se vuelva lento la obtención de información. Pero, ¿por qué?.

Aquí entra en concepto los "hilos" (se explica en el siguiente apartado):
* Si un servicio externo es lento, puede generar muchos hilos bloqueados (threads que están esperando una respuesta) en tu sistema. Estos hilos siguen consumiendo recursos del sistema mientras esperan que el servicio externo responda.
Cuando intentas llamar a un servicio interno, este también necesitará recursos del sistema (como hilos disponibles) para procesar la solicitud. Sin embargo, si la mayoría de los recursos están ocupados esperando respuestas de ese servicio externo lento, el servicio interno no tendrá suficientes recursos para funcionar de manera eficiente.
Como resultado, todo el sistema se vuelve más lento, ya que los recursos están saturados con solicitudes pendientes que aún no han sido completadas.
En resumen: un servicio externo lento puede crear un efecto dominó que afecta negativamente a otros servicios en tu sistema, ralentizando su desempeño general.

### ¿Cómo funcionan los hilos?

De normal, el flujo correcto sería el siguiente:
<img width="1309" alt="image" src="https://github.com/user-attachments/assets/b43b348c-8daf-4804-8c40-7b6a3d77a095" />
**Pero no siempre se dan situacione idóneas y hay veces que el flujo no es así!**:
<img width="1218" alt="image" src="https://github.com/user-attachments/assets/a59f5a39-39b5-4154-942d-41d23c07319b" />

**¿La solución?**: crear *"timeouts"*

## Circuit breaker

Para solucionar los problemas que encontramos con servicios lentos entra lo que denominamos como **"circuit breaker"**.
Un Circuit Breaker (Interruptor de Circuito) es un patrón de diseño utilizado en arquitecturas de microservicios para evitar fallos en cascada cuando un servicio dependiente falla o está sobrecargado. Su principal objetivo es detectar fallos rápidamente y evitar que el sistema se degrade aún más al seguir intentando operaciones que probablemente fallarán.

### ¿Cuándo se dispara el circuito?

Para lanzarlo es fundamental tener en cuenta los siguientes matices:

* Últimas n peticiones para ser considerado como decisión

* ¿Cuántas de esas deben fallar?

* Duración del "timeout"

### ¿Cuándo se resetea el circuito?

* ¿Cuánto tiempo esperamos desde que se disparó el circuito por última vez?

### Ejemplo

Para poder ver estas explicaciones es mejor verlo en un dibujo:

<img width="1218" alt="image" src="https://github.com/user-attachments/assets/fb35ffc2-39c4-4869-80b5-87fb7aa47a13" />

Poniendo de ejemplo las condiciones del dibujo, podemos visualizar una idea de como se ejecutaría un **circuit  breaker**, lógicamente esto en producción no sería así y averiguar las variables exactas es mucho más complicado.

### Ejemplo

*Una vez entendido esto, imaginemos que nuestro MovieInfoService va lento, ¿qué haríamos?*

Necesitamos **retroceder**. Para ellos podemos tener varias opciones:

1. Lanzar un error: no es la más aconsejable

2. Devolver una repuesta predeterminada

3. Guardar respuestas anterior en caché y utilizarlas: esta es la mejor opción!

### 🤔 Curiosidades

**¿Cómo averiguamos el número de hilos de nuestro servidor web?**

Antes de implementar un circuit breaker seguro que puedes pensar, ¿cómo podemos saber el número de hilos de los que dispone nuestro servidor?

Si trabajamos con SpringBoot, de normal es montar un servidor Tomcat, así que podemos acceder al archivo **server.xml**:

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443"
           maxThreads="200"
           minSpareThreads="10"/>
```

**¿Cómo podemos saber los valores que tenemos que usar para nuestro circuit breaker?**

En muchas páginas, libros, teoría dan muchas explicaciones de como poder obtener los valores para poder crear el circuito.

¿La verdad?. La verdad es que los parámetros se consiguen a base de prueba y error, ver como tu aplicación reacciona a un número concreto de valores y modificiarlos
en caso de que no tenga el rendimiento esperado.

## Implementación del circuit breaker: Resilience4j

Reilience4j es una biblioteca de Java diseñada para implementar patrones de resiliencia en aplicaciones distribuidas o microservicios. Está inspirada en Hystrix, pero a diferencia de Hystrix, está construida específicamente para Java 8 y versiones posteriores, aprovechando sus características modernas como Lambdas y CompletableFuture.

Tenemos muchas opciones que nos aporta Resilience4j, pero en nuestro caso haremos uso en concreto de la herramienta **Circuit breaker**.

En primer lugar pondremos la dependencia:

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

Lo segundo, indicar donde queremos que tenga lugar el circuit breaker:

```java
 @RequestMapping("/{userId}")
    @CircuitBreaker(name = "movie-catalog-service", fallbackMethod = "fallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) throws Exception {
            UserRating userRatings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/user/" + userId, UserRating.class);
            return userRatings.getRatings().stream()
                    .map(rating -> {
                        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                        return new CatalogItem(
                                movie.getName(),
                                movie.getDescription(),
                                rating.getRating()
                        );
                    }).toList();
    }
```

En "name" indicamos el nombre del circuit breaker, y en el "fallbackMethod" indicamos el nombre del método que actuará en el caso de que falle la llamada en este caso a "MovieInfoService":

```java
private List<CatalogItem> fallbackCatalog(Exception e) {
        return List.of(new CatalogItem("Película no disponible", "", 0));
    }
```

Y luego en nuestro application.properties o en nuestro application.yml indicamos como parametros lo siguiente:
```java
spring:
  application:
    name: movie-catalog-service
resilience4j:
circuitbreaker:
  instances:
    movie-catalog-service:
      slidingWindowSize: 10
      slidingWindowType: COUNT_BASED
      minimumNumberOfCalls: 5
      permittedNumberOfCallsInHalfOpenState: 10
      failureRateThreshold: 50
server:
  port: 8081
```
* slidingWindowSize:

Define el tamaño de la ventana deslizante para medir el rendimiento del Circuit Breaker.
10: Se tendrán en cuenta las últimas 10 llamadas al servicio.

* slidingWindowType:

Define el tipo de ventana:
COUNT_BASED: Se basa en un número fijo de llamadas (en este caso, 10).
TIME_BASED: Se basa en un período de tiempo fijo.

* minimumNumberOfCalls:

Número mínimo de llamadas necesarias para que el Circuit Breaker empiece a evaluar el estado.
5: Hasta que no haya al menos 5 llamadas, el estado no se evaluará.

* permittedNumberOfCallsInHalfOpenState:

Número de llamadas permitidas cuando el Circuit Breaker está en estado "Half-Open" (medio abierto).
10: Se permitirán hasta 10 llamadas antes de decidir si el Circuit Breaker vuelve a estar "Closed" (cerrado) o se mantiene "Open" (abierto).

* failureRateThreshold:

Umbral de tasa de fallos para abrir el Circuit Breaker.
50: Si el 50% de las llamadas fallan, el Circuit Breaker cambiará a estado "Open".
eventConsumerBufferSize:

# Fuentes
https://resilience4j.readme.io/docs/getting-started
https://samnewman.io/books/building_microservices/
https://www.youtube.com/c/amigoscode
https://www.arquitecturajava.com/spring-cloud-eureka-server-y-discovery/
