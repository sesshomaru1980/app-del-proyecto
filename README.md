# 💈 NeoBarber App

Aplicación móvil desarrollada en **Android (Kotlin + Jetpack Compose)** conectada a un **backend Node.js + Express** con base de datos **MongoDB (NoSQL)**.

Este proyecto permite gestionar una barbería mediante diferentes roles: **Cliente, Administrador y Barbero**.

---

## 📱 Descripción

NeoBarber es una aplicación móvil que permite:

- Registro e inicio de sesión de usuarios
- Reserva de citas
- Gestión de servicios
- Gestión de barberos
- Administración de citas
- Panel por roles (Admin, Cliente, Barbero)

La app consume una API REST conectada a MongoDB.

---

## 🧰 Tecnologías utilizadas

### 🔹 Frontend (Android)
- Kotlin
- Jetpack Compose
- Material 3
- Retrofit
- Android Studio

### 🔹 Backend
- Node.js
- Express
- MongoDB
- Mongoose

### 🔹 Base de datos
- MongoDB (NoSQL)

---

## 📁 Estructura del proyecto

```bash
neobarber/
├── neobarber-backend/      # Backend (Node.js + Express)
├── neobarber-proyecto/     # App Android (Kotlin)
├── README.md
└── .gitignore

⚙️ Requisitos previos
Backend
Node.js (v18 o superior)
npm
MongoDB (local o Atlas)
Android
Android Studio
SDK Android configurado
Emulador o dispositivo físico.

🧠 Configuración del backend
1. Entrar a la carpeta
cd neobarber-backend
2. Instalar dependencias
npm install
3. Crear archivo .env
PORT=3000
MONGODB_URI=mongodb://127.0.0.1:27017/neobarber
JWT_SECRET=clave_secreta
🗄️ Configuración de MongoDB
Opción 1: Local
MONGODB_URI=mongodb://127.0.0.1:27017/neobarber
Opción 2: MongoDB Atlas
MONGODB_URI=mongodb+srv://usuario:password@cluster.mongodb.net/neobarber
▶️ Ejecutar backend
npm run dev

Servidor disponible en:

http://localhost:3000
📲 Configuración app Android

Abrir en Android Studio:

neobarber-proyecto/
🔗 Configurar API (IMPORTANTE)

Buscar archivo:

ApiConfig.kt o RetrofitClient.kt
🔹 Para emulador:
const val BASE_URL = "http://10.0.2.2:3000/"
🔹 Para celular físico:
const val BASE_URL = "http://192.168.X.X:3000/"

(Reemplazar por tu IP local)

▶️ Ejecutar en emulador
Abrir Android Studio
Ejecutar emulador
Ejecutar proyecto
Verificar backend activo
📱 Ejecutar en celular físico
Conectar celular por USB
Activar:
Opciones de desarrollador
Depuración USB
Estar en la misma red WiFi
Ejecutar backend
Cambiar BASE_URL a IP local
Ejecutar app
🔐 Roles del sistema
👤 Cliente
Registrarse
Iniciar sesión
Reservar cita
Cerrar sesión
🧑‍💼 Administrador
Gestionar servicios
Gestionar barberos
Ver citas
Cerrar sesión
✂️ Barbero
Ver citas asignadas
Cambiar estado de citas
Cerrar sesión
🧭 Flujo de navegación
WelcomeScreen
   ↓
Login / Register
   ↓
Cliente → Reservar cita
Admin → Panel administración
Barbero → Ver citas
✅ Funcionalidades implementadas
Autenticación completa
Manejo de sesión
Consumo de API REST
Navegación por roles
CRUD de servicios
Reserva de citas
Pantalla recuperación de contraseña (simulada)
Mensajes de éxito y error
🧪 Pruebas realizadas
Login correcto/incorrecto
Registro de usuario
Reserva de citas
Cambio de estado de citas
Cierre de sesión
🧼 Buenas prácticas
Arquitectura por capas
Separación de responsabilidades
Uso de Retrofit
Manejo de errores HTTP
Código comentado
Organización por paquetes
📦 Entrega

Este repositorio incluye:

Código backend
Aplicación Android
Documentación
Estructura organizada
👨‍💻 Autor

Nombre: Pablo Prado
Proyecto: NeoBarber
Evidencia: GA8-220501096-AA2-EV02
Institución: SENA

