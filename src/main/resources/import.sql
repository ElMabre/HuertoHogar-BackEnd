USE huertohogar_db;

-- 1. Borramos los productos actuales (que no tienen imagen)
DELETE FROM productos;

-- 2. Insertamos los productos con la columna 'imagen' correcta
INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad, imagen) 
VALUES 
('FR001', 'Manzanas Fuji', 1200, 'frutas', 150, 'Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule.', 'Valle del Maule', 'por kilo', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/manzana.jpg'),
('FR002', 'Naranjas Valencia', 1000, 'frutas', 200, 'Jugosas y ricas en vitamina C, ideales para zumos.', 'Región de Valparaíso', 'por kilo', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/naranja.jpg'),
('FR003', 'Plátanos Cavendish', 800, 'frutas', 250, 'Plátanos maduros y dulces, perfectos para el desayuno.', 'Región de O''Higgins', 'por kilo', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/platano.jpg'),
('VR001', 'Zanahorias Orgánicas', 900, 'verduras', 100, 'Zanahorias crujientes cultivadas sin pesticidas.', 'Región de O''Higgins', 'por kilo', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/zanahoria.jpg'),
('VR002', 'Espinacas Frescas', 700, 'verduras', 80, 'Espinacas frescas y nutritivas, perfectas para ensaladas.', 'Región Metropolitana', 'por bolsa de 500g', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/espinaca.jpg'),
('VR003', 'Pimientos Tricolores', 1500, 'verduras', 120, 'Pimientos rojos, amarillos y verdes, ideales para salteados.', 'Región de Valparaíso', 'por kilo', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/pimiento.jpg'),
('PO001', 'Miel Orgánica', 5000, 'organicos', 50, 'Miel pura y orgánica producida por apicultores locales.', 'Región del Maule', 'por frasco de 500g', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/miel.jpg'),
('PO002', 'Quinua Orgánica', 3500, 'organicos', 75, 'Quinua orgánica de alta calidad, perfecta para ensaladas.', 'Región de La Araucanía', 'por bolsa de 1kg', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/quinua.jpg'),
('PL001', 'Leche Entera', 1200, 'lacteos', 60, 'Leche entera fresca de vacas criadas en praderas naturales.', 'Región de Los Lagos', 'por litro', 'https://raw.githubusercontent.com/ElMabre/ProyectoHuertoHogar/refs/heads/main/img/leche.jpg');