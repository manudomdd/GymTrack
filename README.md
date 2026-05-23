# 🏋️‍♂️ GymTrack - [PRODUCTION]

<div align="center">
  <p><strong>Plataforma integral para Entrenadores Personales y Clientes, diseñada para el seguimiento avanzado del rendimiento físico, análisis de métricas y planificación inteligente del entrenamiento.</strong></p>
  <p><i>⚠️ ESTA ES LA RAMA DE PRODUCCIÓN. El código aquí refleja el estado estable y desplegado en vivo de la aplicación. ⚠️</i></p>
</div>

---

## 📖 Descripción del Proyecto

GymTrack es una solución Full-Stack compuesta por una aplicación móvil nativa (Frontend) y una API robusta (Backend). Su objetivo es digitalizar por completo la relación entre los entrenadores y sus clientes, proporcionando un entorno rico en datos estadísticos y facilidades de interacción.

### 🌟 Características Principales
- **Roles Diferenciados:** Dashboards y vistas independientes para Entrenadores y Clientes.
- **Registro Inteligente de Entrenamientos:** Sistema estructurado de ejercicios por categorías (grupo muscular -> ejercicio) y control preciso de pesos y volúmenes.
- **Análisis de Métricas Avanzado:** Uso de algoritmos de regresión lineal para evaluar tendencias de rendimiento y la progresión gráfica de los clientes.
- **Asistente de IA Integrado:** Un "Coach Asistente" impulsado por Inteligencia Artificial para recomendaciones y análisis de rutinas.
- **Perfiles Modernos:** Interfaz cuidada con avatares circulares y gestión avanzada de los datos biológicos del usuario.

## 🏗️ Arquitectura y Tecnologías

Este repositorio está estructurado en formato **Monorepo**, dividiendo claramente la aplicación en dos ecosistemas principales:

*   **📱 Frontend (Android):** Aplicación nativa de Android escrita en Java y construida con Gradle.
*   **⚙️ Backend (Spring Boot):** API RESTful desarrollada en Java 17 usando Spring Boot, Maven, JPA/Hibernate y MySQL como motor de persistencia de datos.
*   **🐳 Infraestructura:** Docker y Docker Compose (multi-stage builds) para asegurar despliegues consistentes y optimizados en distintos entornos.

## 🚀 Entorno de Producción y Despliegue

Esta rama está configurada de forma estricta para el **Despliegue Continuo (CD)** y garantiza el mayor nivel de seguridad y rendimiento.

> 🔒 **Seguridad y Credenciales (Zero Trust):**
> A diferencia de la rama `main` de desarrollo, en esta rama **NO hay contraseñas ni datos de conexión *hardcodeados***. El backend exige que las credenciales de la base de datos se inyecten estrictamente mediante **Variables de Entorno** (ej. `MYSQL_HOST`, `MYSQL_USER`, `MYSQL_PASSWORD`). 
> Cualquier secreto local debe configurarse en un archivo `.env` (el cual está fuertemente ignorado en Git mediante `.gitignore`).

### 🚄 Despliegue Automático en PaaS (Railway)

La arquitectura de esta rama está lista para ser desplegada en Railway (u otras plataformas Cloud) de forma 100% automatizada:
1. La plataforma detectará los cambios en esta rama al instante.
2. Analizará el archivo `backend/Dockerfile` y orquestará un *Multi-stage build* (compilando el `.jar` con Maven y luego montándolo en una imagen JRE ligera).
3. Inyectará en caliente las variables de entorno de la base de datos MySQL gestionada.
4. Expondrá el servicio a través del puerto definido en la directiva `EXPOSE 8080` utilizando su propia red interna.

### 🛠️ Simulación Local del Entorno de Producción

Aunque plataformas como Railway ignoran el archivo `docker-compose.yml` que se encuentra en la raíz (ya que despliegan desde el Dockerfile), hemos conservado este orquestador para **fines de documentación y simulación de incidencias en local**.

Si necesitas levantar una réplica exacta del servidor de producción en tu ordenador:

1. **Configura tus secretos:**
   ```bash
   cp .env.example .env
   # Edita el archivo .env con tus credenciales seguras.
   ```
2. **Levanta la simulación:**
   ```bash
   docker-compose up -d --build
   ```
   *Esto levantará el backend aislado y un contenedor MySQL local que simula el entorno gestionado.*

## 🌳 Estructura de Ramas del Repositorio

Para garantizar la estabilidad y limpieza del proyecto, seguimos una estricta política de ramas (descartando "ramas zombis"):

- **`main`:** Rama base de desarrollo continuo. Contiene las últimas características funcionales. Preparada para *debugging* local rápido.
- **`production`:** Entorno de producción altamente securizado. Enlazado automáticamente para el Despliegue Continuo (CD). 
- **`test`:** Entorno de pruebas e integración (QA) para la validación de nuevas *features* antes de ser unificadas con la rama principal.

---
<div align="center">
  <i>Desarrollado con pasión para revolucionar el software deportivo.</i>
</div>
