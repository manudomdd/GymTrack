# 🏋️‍♂️ GymTrack

<div align="center">
  <p><strong>Plataforma integral para Entrenadores Personales y Clientes, diseñada para el seguimiento avanzado del rendimiento físico, análisis de métricas y planificación inteligente del entrenamiento.</strong></p>
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

## 🚀 Entorno de Desarrollo (Rama `main`)

La rama `main` está configurada específicamente para facilitar el **desarrollo local rápido, la depuración y la corrección de errores**. 

> ⚠️ **Nota de Arquitectura sobre las Credenciales:** 
> Para agilizar el levantamiento del proyecto por parte de la persona encargada de la corrección de este trabajo, en la rama `main` **las credenciales de la base de datos se encuentran *hardcodeadas***. Esto es intencional y exclusivo de este entorno de trabajo local. La rama `production` (orientada a despliegues en PaaS como Railway) utiliza un blindaje estricto mediante variables de entorno (`.env`) sin valores por defecto comprometidos.

### 🛠️ Pasos para la Ejecución del Backend (vía Docker)

He contenedorizado el backend y su base de datos para que levantar el servicio sea cuestión de un solo comando. No necesitas instalar bases de datos locales.

1. **Sitúate en la raíz del proyecto:**
   ```bash
   cd GymTrack
   ```

2. **Levanta la infraestructura completa:**
   El siguiente comando construirá la imagen del backend, resolverá dependencias y levantará el contenedor de MySQL configurado:
   ```bash
   docker-compose up -d --build
   ```

3. **Verifica los servicios:**
   - El **backend** (API) estará disponible y escuchando en `http://localhost:8080`.
   - La **base de datos** MySQL (`gymtrack_v2`) quedará expuesta en el puerto `3306`.

### 📱 Compilación del Frontend

1. Abre la carpeta `frontend/` utilizando **Android Studio**.
2. Sincroniza el proyecto con Gradle.
3. Configura las variables de red del cliente apuntando a tu `localhost` (o a la IP de tu máquina si usas un dispositivo físico).Actualmente esta configurado para apuntar a loscalhost
5. Ejecuta la aplicación.

## 🌳 Estructura de Ramas del Repositorio

Para garantizar la estabilidad y limpieza del proyecto, seguimos una estricta política de ramas (descartando "ramas zombis"):

- **`main`:** Rama base de desarrollo continuo. Contiene las últimas características funcionales. Preparada para *debugging* local rápido.
- **`production`:** Entorno de producción altamente securizado. Enlazado automáticamente para el Despliegue Continuo (CD). 
- **`test`:** Entorno de pruebas e integración (QA) para la validación de nuevas *features* antes de ser unificadas con la rama principal.

---
<div align="center">
  <i>Desarrollado con pasión para revolucionar el software deportivo.</i>
</div>
