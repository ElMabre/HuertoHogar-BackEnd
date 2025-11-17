
-- Inserta usuarios con contraseñas encriptadas (BCrypt)
-- La contraseña para 'admin@huerto.hogar' es: admin123
INSERT INTO usuarios (nombre, apellido, run, email, password, region, comuna, direccion, rol) 
VALUES (
    'Admin', 
    'HuertoHogar', 
    '11111111-1', 
    'admin@huerto.hogar', 
    '$2a$10$yS.8a0.V.K.G.l.S.A.p.N.O.R.S.W.O.R.D.P.S.S.W.D', -- admin123
    'Metropolitana', 
    'Santiago', 
    'Av. Principal 123', 
    'ADMIN'
);

-- La contraseña para 'cliente@gmail.com' es: cliente123
INSERT INTO usuarios (nombre, apellido, run, email, password, region, comuna, direccion, rol) 
VALUES (
    'Cliente', 
    'Prueba', 
    '22222222-2', 
    'cliente@gmail.com', 
    '$2a$10$a.B.C.D.E.F.G.H.I.J.K.L.M.N.O.P.Q.R.S.T.U.V.W', -- cliente123
    'Valparaíso', 
    'Viña del Mar', 
    'Calle Falsa 123', 
    'CLIENTE'
);

-- Inserta los productos de la pauta
INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'FR001', 
    'Manzanas Fuji', 
    1200, 
    'frutas', 
    150, 
    'Manzanas Fuji crujientes y dulces, cultivadas en el Valle del Maule. Perfectas para meriendas saludables o como ingrediente en postres. Estas manzanas son conocidas por su textura firme y su sabor equilibrado entre dulce y ácido.', 
    'Valle del Maule', 
    'por kilo'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'FR002', 
    'Naranjas Valencia', 
    1000, 
    'frutas', 
    200, 
    'Jugosas y ricas en vitamina C, estas naranjas Valencia son ideales para zumos frescos y refrescantes. Cultivadas en condiciones climáticas óptimas que aseguran su dulzura y jugosidad.', 
    'Región de Valparaíso', 
    'por kilo'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'FR003', 
    'Plátanos Cavendish', 
    800, 
    'frutas', 
    250, 
    'Plátanos maduros y dulces, perfectos para el desayuno o como snack energético. Estos plátanos son ricos en potasio y vitaminas, ideales para mantener una dieta equilibrada.', 
    'Región de O\'Higgins', 
    'por kilo'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'VR001', 
    'Zanahorias Orgánicas', 
    900, 
    'verduras', 
    100, 
    'Zanahorias crujientes cultivadas sin pesticidas en la Región de O\'Higgins. Excelente fuente de vitamina A y fibra, ideales para ensaladas, jugos o como snack saludable.', 
    'Región de O\'Higgins', 
    'por kilo'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'VR002', 
    'Espinacas Frescas', 
    700, 
    'verduras', 
    80, 
    'Espinacas frescas y nutritivas, perfectas para ensaladas y batidos verdes. Estas espinacas son cultivadas bajo prácticas orgánicas que garantizan su calidad y valor nutricional.', 
    'Región Metropolitana', 
    'por bolsa de 500g'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'VR003', 
    'Pimientos Tricolores', 
    1500, 
    'verduras', 
    120, 
    'Pimientos rojos, amarillos y verdes, ideales para salteados y platos coloridos. Ricos en antioxidantes y vitaminas, estos pimientos añaden un toque vibrante y saludable a cualquier receta.', 
    'Región de Valparaíso', 
    'por kilo'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'PO001', 
    'Miel Orgánica', 
    5000, 
    'organicos', 
    50, 
    'Miel pura y orgánica producida por apicultores locales. Rica en antioxidantes y con un sabor inigualable, perfecta para endulzar de manera natural tus comidas y bebidas.', 
    'Región del Maule', 
    'por frasco de 500g'
);

-- (Añado los productos que vi en tu código de React pero no en la pauta PDF)
INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'PO002', 
    'Quinua Orgánica', 
    3500, 
    'organicos', 
    75, 
    'Quinua orgánica de alta calidad, perfecta para ensaladas y platos saludables. Es una excelente fuente de proteína vegetal y fibra.', 
    'Región de La Araucanía', 
    'por bolsa de 1kg'
);

INSERT INTO productos (sku, nombre, precio, categoria, stock, descripcion, origen, unidad) 
VALUES (
    'PL001', 
    'Leche Entera', 
    1200, 
    'lacteos', 
    60, 
    'Leche entera fresca de vacas criadas en praderas naturales. Rica en calcio y vitaminas, ideal para toda la familia.', 
    'Región de Los Lagos', 
    'por litro'
);