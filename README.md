# Aprendiendo-Microservicios
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


