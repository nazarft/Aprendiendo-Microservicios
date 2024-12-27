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

✅ Ventajas:

✔️ Más fácil de desarrollar, probar y depurar al inicio.

✔️ Despliegue sencillo: todo en un solo lugar.

✔️ Rendimiento eficiente al evitar llamadas a través de la red.

✔️ Menor sobrecarga operativa (menos servicios que administrar).

❌ Desventajas:

❌ Escalabilidad limitada: no puedes escalar partes individuales del sistema.

❌ Difícil de mantener a medida que crece el código.

❌ Una falla puede tumbar toda la aplicación.

❌ Tiempo de despliegue más largo conforme aumenta la complejidad.

🛠️ Casos de Uso Ideales:

Proyectos pequeños o medianos.

Equipos pequeños de desarrollo.

Aplicaciones con requisitos estables y pocos cambios.

# Arquitectura de microservicios

Con este tipo de arquitectura, tu aplicación se divide en aplicaciones más pequeñas cada una responsable de una tarea específica.

📦 Características:

Desacoplamiento: Cada servicio funciona de forma independiente.

Comunicación vía APIs: Los servicios interactúan a través de HTTP/REST, gRPC, o colas de mensajes.

Escalabilidad independiente: Puedes escalar solo los servicios que lo necesiten.

Despliegue independiente: Cada servicio se puede actualizar sin afectar a los demás.

✅ Ventajas:

✔️ Escalabilidad independiente para cada servicio.

✔️ Mayor flexibilidad tecnológica (diferentes lenguajes y bases de datos).

✔️ Mejor tolerancia a fallos: si un servicio falla, no colapsa todo el sistema.

✔️ Equipos independientes pueden trabajar en diferentes servicios simultáneamente.

❌ Desventajas:

❌ Mayor complejidad operativa y de implementación.

❌ Problemas de latencia en la comunicación entre servicios.

❌ Más difícil de depurar y hacer seguimiento de errores.

❌ Sobrecarga en el despliegue y gestión de múltiples servicios.

🛠️ Casos de Uso Ideales:

Aplicaciones grandes y complejas.

Equipos de desarrollo distribuidos.

Necesidad de escalabilidad frecuente.

Entornos con requisitos cambiantes.

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

👀 WebClient y el futuro de la programación reactiva

Tanto RestTemplate como WebClient son herramientas para consumir APIs en aplicaciones Spring Boot, pero tienen diferencias clave en su diseño, uso y casos recomendados.

✅ RestTemplate

* Usa un modelo de programación bloqueante y sincrónico.
  
* Cada llamada bloquea el hilo hasta que obtiene una respuesta.
  
* Adecuado para aplicaciones más simples o con pocas llamadas HTTP.
  
* Está marcado como @Deprecated en las últimas versiones de Spring.
  
* Ideal para aplicaciones monolíticas o heredadas.

✅ WebClient

* Soporta programación reactiva y no bloqueante.

* Permite manejar asincronía de forma eficiente.

* Optimiza el uso de recursos y escala mejor en aplicaciones con alta concurrencia.

* Compatible con Mono y Flux (paradigmas reactivos).

* Es la opción recomendada por Spring para aplicaciones modernas.

🧠 ¿Cuál elegir?
🟢 Para nuevas aplicaciones:

✅ Usa WebClient.

🟡 Para aplicaciones existentes con RestTemplate:

⚠️ No migres sin una necesidad clara.

🔄 Puedes combinar ambos en un proceso de transición gradual.

🔵 Para aplicaciones altamente concurrentes o reactivo por naturaleza:

✅ WebClient es la opción indiscutible.

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

👀 Observaciones: 
Si te fijas, mi endpoint no devuelve una lista de ratings, si no un objeto UserRatings. ¿Por qué?, muy sencillo
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
🚨 Problema:

userId y userName están repetidos en cada calificación.

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

Direcciones IP dinámicas: Los servicios suelen desplegarse en contenedores o máquinas virtuales con direcciones IP que pueden cambiar dinámicamente.

Escalabilidad: A medida que se añaden o eliminan instancias de servicios, la lista de direcciones IP cambia constantemente.

Complejidad: Los servicios necesitan saber dónde y cómo encontrar otros servicios.

✅ Solución con Eureka:

Registro de Servicios: Cada microservicio se registra en el Eureka Server cuando se inicia.

Descubrimiento de Servicios: Los microservicios pueden consultar a Eureka para obtener la dirección y el puerto de otros servicios.

Load Balancing: Eureka se integra con herramientas como Ribbon para distribuir las solicitudes entre múltiples instancias de un servicio.

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



