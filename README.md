<p align="center">
<img src="https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/huertohogarlogoconfondo.png" width="300" alt="HuertoHogar Logo"/>
</p>

<h1 align="center">HuertoHogar – Backend Microservicios</h1>

<p align="center">
<b>Asignatura:</b> Desarrollo Fullstack II (DSY1104) · <b>Duoc UC</b>





<b>Autores:</b> Matias Guzman, Felipe Quezada y Danilo Celis
</p>

<p align="center">
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Java-21-ED8B00%3Flogo%3Dopenjdk%26logoColor%3Dwhite" alt="Java Version"/>
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F%3Flogo%3Dspring-boot%26logoColor%3Dwhite" alt="Spring Boot Version"/>
<img src="https://www.google.com/search?q=https://img.shields.io/badge/MySQL-8.0%2B-4479A1%3Flogo%3Dmysql%26logoColor%3Dwhite" alt="MySQL Version"/>
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Seguridad-JWT-000000%3Flogo%3Djson-web-tokens%26logoColor%3Dwhite" alt="JWT Security"/>
<img src="https://www.google.com/search?q=https://img.shields.io/badge/Arquitectura-Microservicios-FF9800" alt="Arquitectura Microservicios"/>
<img src="https://img.shields.io/badge/Licencia-MIT-00C853?logo=open-source-initiative&logoColor=white" alt="Licencia MIT"/>
</p>

Descripción General

Este repositorio contiene el Backend de la plataforma E-commerce HuertoHogar. Inicialmente concebido como un monolito, el sistema fue refactorizado hacia una arquitectura de Microservicios para mejorar la escalabilidad, el mantenimiento y la separación de responsabilidades.

El sistema está construido con Java 21 y Spring Boot, utilizando MySQL como base de datos relacional compartida. Implementa seguridad robusta mediante Spring Security y JWT (JSON Web Tokens) para la autenticación y autorización stateless entre servicios. Cada microservicio cuenta con su propia documentación interactiva mediante Swagger (OpenAPI).

Arquitectura de Microservicios

El backend se divide en 3 servicios independientes que se comunican a través de API REST:

1. Microservicio de Usuarios (ms-usuarios)

Puerto: 8081

Responsabilidad: Gestión de identidades y acceso.

Funcionalidades:

Registro de usuarios (Clientes, Vendedores, Admin).

Autenticación (Login) y generación de tokens JWT.

Validación de roles y permisos.

CRUD de usuarios para administradores.

2. Microservicio de Catálogo (ms-catalogo)

Puerto: 8082

Responsabilidad: Gestión del inventario de productos.

Funcionalidades:

Listado público de productos con filtros.

CRUD completo de productos (Crear, Editar, Eliminar) protegido para administradores.

Validación de JWT para operaciones protegidas.

3. Microservicio de Pedidos (ms-pedidos)

Puerto: 8083

Responsabilidad: Procesamiento de transacciones.

Funcionalidades:

Creación de pedidos (Checkout) para clientes autenticados.

Gestión de estados de pedido (Pendiente, En camino, Completado).

Historial de ventas para administración.

Control de stock (descuenta unidades al comprar).

Stack Tecnológico

Lenguaje: Java 21 (LTS)

Framework Principal: Spring Boot 3.5.7

Base de Datos: MySQL 8.0+

Seguridad: Spring Security 6 + JWT (Auth0 Library)

Persistencia: Spring Data JPA (Hibernate)

Documentación: SpringDoc OpenAPI (Swagger UI)

Herramienta de Construcción: Maven

Utilidades: Lombok

Estructura del Proyecto

HuertoHogar-BackEnd/
├── ms-usuarios/       # Microservicio de Autenticación y Usuarios
├── ms-catalogo/       # Microservicio de Productos
├── ms-pedidos/        # Microservicio de Pedidos y Ventas
└── README.md          # Documentación general


Guía de Inicio Rápido

Requisitos Previos

Java JDK 21 instalado.

Maven instalado (o usar el wrapper mvnw).

MySQL Server corriendo.