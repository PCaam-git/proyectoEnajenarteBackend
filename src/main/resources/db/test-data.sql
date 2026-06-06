-- ============================================================
-- SCRIPT DE DATOS DE PRUEBA - enajenArte
-- Base de datos: enajenarte_db
-- Uso recomendado: HeidiSQL / MariaDB
-- ============================================================

USE enajenarte_db;

-- ------------------------------------------------------------
-- 1. Limpieza de datos
-- ------------------------------------------------------------

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM program_registrations;
DELETE FROM registrations;
DELETE FROM admin_calendar;
DELETE FROM contact_messages;
DELETE FROM events;
DELETE FROM programs;
DELETE FROM workshops;
DELETE FROM speakers;
DELETE FROM users;

ALTER TABLE program_registrations AUTO_INCREMENT = 1;
ALTER TABLE registrations AUTO_INCREMENT = 1;
ALTER TABLE admin_calendar AUTO_INCREMENT = 1;
ALTER TABLE contact_messages AUTO_INCREMENT = 1;
ALTER TABLE events AUTO_INCREMENT = 1;
ALTER TABLE programs AUTO_INCREMENT = 1;
ALTER TABLE workshops AUTO_INCREMENT = 1;
ALTER TABLE speakers AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 2. Usuarios
-- Contraseña de prueba para todos: 123456
-- ------------------------------------------------------------

INSERT INTO users
(id, username, password, email, full_name, phone, gender, age_group, is_active, role)
VALUES
    (1, 'admin', '$2a$10$8S54TgPI.VpVP9ST8pyaxeIF9GRd/rFgASGX76nHxQ2zfyScerMoi',
     'admin@enajenarte.es', 'Administradora enajenArte', 600111222, 'FEMALE', 'BETWEEN_35_44', true, 'ADMIN'),

    (2, 'usuario1', '$2a$10$8S54TgPI.VpVP9ST8pyaxeIF9GRd/rFgASGX76nHxQ2zfyScerMoi',
     'usuario1@test.com', 'Lucía Martín Pérez', 611222333, 'FEMALE', 'BETWEEN_25_34', true, 'USER'),

    (3, 'usuario2', '$2a$10$8S54TgPI.VpVP9ST8pyaxeIF9GRd/rFgASGX76nHxQ2zfyScerMoi',
     'usuario2@test.com', 'Carlos Gómez Ruiz', 622333444, 'MALE', 'BETWEEN_35_44', true, 'USER'),

    (4, 'usuario3', '$2a$10$8S54TgPI.VpVP9ST8pyaxeIF9GRd/rFgASGX76nHxQ2zfyScerMoi',
     'usuario3@test.com', 'Alex Sánchez López', 633444555, 'OTHER', 'BETWEEN_18_24', true, 'USER'),

    (5, 'usuario_inactivo', '$2a$10$8S54TgPI.VpVP9ST8pyaxeIF9GRd/rFgASGX76nHxQ2zfyScerMoi',
     'inactivo@test.com', 'Usuario Inactivo', 644555666, 'PREFER_NOT_TO_SAY', 'PREFER_NOT_TO_SAY', false, 'USER');

-- ------------------------------------------------------------
-- 3. Ponentes
-- ------------------------------------------------------------

INSERT INTO speakers
(id, first_name, last_name, email, speciality, years_experience, workshop_hours_total, is_available, join_date)
VALUES
    (1, 'Cristina', 'García', 'cristina@enajenarte.es', 'Escritura emocional y creatividad', 8, 320.5, true, '2021-09-15'),
    (2, 'Mónica', 'Calvo', 'monica@enajenarte.es', 'Acompañamiento emocional y arte terapéutico', 10, 410.0, true, '2020-03-10'),
    (3, 'Laura', 'Navarro', 'laura.nav Navarro@test.com', 'Narrativa personal y memoria vital', 5, 145.0, true, '2023-01-20'),
    (4, 'David', 'Romero', 'david.romero@test.com', 'Comunicación, lectura y dinamización grupal', 6, 180.0, false, '2022-05-05');

-- Corrección de email del ponente 3 si el cliente SQL no acepta espacios accidentales
UPDATE speakers
SET email = 'laura.navarro@test.com'
WHERE id = 3;

-- ------------------------------------------------------------
-- 4. Eventos
-- ------------------------------------------------------------

INSERT INTO events
(id, title, location, event_date, entry_fee, is_public, expected_attendance, speaker_id)
VALUES
    (1, 'Encuentro creativo abierto', 'Centro Cívico Delicias', '2026-07-10 18:00:00', 0.00, true, 40, 1),
    (2, 'Charla sobre bienestar emocional', 'Biblioteca Municipal', '2026-07-18 19:00:00', 5.00, true, 35, 2),
    (3, 'Presentación Biblioteca Viva', 'Espacio Cultural Río Ebro', '2026-08-05 18:30:00', 0.00, true, 30, 3),
    (4, 'Sesión privada para entidad colaboradora', 'Sede entidad colaboradora', '2026-08-20 17:00:00', 15.00, false, 20, 2);

-- ------------------------------------------------------------
-- 5. Talleres
-- ------------------------------------------------------------

INSERT INTO workshops
(id, name, description, start_date, hour, confirmation_deadline, duration_minutes, price,
 minimum_participants, max_capacity, is_online, status, speaker_id)
VALUES
    (1, 'Escritura emocional',
     'Taller práctico para explorar emociones mediante ejercicios de escritura creativa.',
     '2026-07-15', '18:00', NULL, 120, 25.00, 4, 12, false, 'CONFIRMED', 1),

    (2, 'Espontaneidad creativa',
     'Actividad grupal para desbloquear la creatividad mediante dinámicas expresivas.',
     '2026-07-22', '17:30', '2026-07-15', 90, 18.00, 5, 15, false, 'PENDING', 2),

    (3, 'Recetas emocionales',
     'Propuesta online para trabajar el autocuidado desde metáforas cotidianas.',
     '2026-08-01', '19:00', NULL, 100, 20.00, 3, 20, true, 'CONFIRMED', 1),

    (4, 'Caminatas narrativas',
     'Taller presencial al aire libre para construir relatos a partir del entorno.',
     '2026-08-12', '10:00', '2026-08-01', 150, 30.00, 6, 10, false, 'PENDING', 3),

    (5, 'Taller cancelado de prueba',
     'Registro de prueba para comprobar el estado cancelado en el panel de administración.',
     '2026-09-01', '18:00', NULL, 90, 10.00, 4, 10, false, 'CANCELLED', 4);

-- ------------------------------------------------------------
-- 6. Programas
-- ------------------------------------------------------------

INSERT INTO programs
(id, name, description, location, init_date, finish_date, hour, duration_minutes,
 confirmation_deadline, price, minimum_participants, max_capacity, is_online, status, speaker_id)
VALUES
    (1, 'EmoCreativos',
     'Programa de varias sesiones orientado a desarrollar la creatividad como herramienta de expresión y bienestar.',
     'Zaragoza', '2026-09-05', '2026-10-05', '18:00', 90,
     NULL, 120.00, 6, 20, false, 'CONFIRMED', 1),

    (2, 'EmoTesoros',
     'Programa centrado en memoria vital, identidad personal y construcción de relatos significativos.',
     'Zaragoza', '2026-09-12', '2026-10-24', '17:30', 120,
     '2026-09-01', 150.00, 8, 18, false, 'PENDING', 2),

    (3, 'Biblioteca Viva',
     'Programa de escucha, conversación y creación compartida a partir de historias personales.',
     'Online', '2026-10-01', '2026-11-15', '19:00', 100,
     NULL, 95.00, 5, 25, true, 'CONFIRMED', 3),

    (4, 'Programa cancelado de prueba',
     'Programa incluido para comprobar estados cancelados en la administración.',
     'Zaragoza', '2026-11-05', '2026-12-05', '18:30', 90,
     NULL, 80.00, 5, 12, false, 'CANCELLED', 4);

-- ------------------------------------------------------------
-- 7. Inscripciones a talleres
-- ------------------------------------------------------------

INSERT INTO registrations
(id, registration_date, confirmation_code, is_paid, number_of_tickets, amount_paid,
 rating, status, payment_status, user_id, workshop_id)
VALUES
    (1, '2026-06-10', 'WORK-001-AAA', true, 1, 25.00, 5, 'CONFIRMED', 'PAID', 2, 1),
    (2, '2026-06-11', 'WORK-002-BBB', false, 2, 0.00, NULL, 'PENDING', 'PENDING', 3, 2),
    (3, '2026-06-12', 'WORK-003-CCC', true, 1, 20.00, NULL, 'CONFIRMED', 'PAID', 4, 3),
    (4, '2026-06-13', 'WORK-004-DDD', false, 1, 0.00, NULL, 'CANCELLED', 'PENDING', 2, 5);

-- ------------------------------------------------------------
-- 8. Inscripciones a programas
-- ------------------------------------------------------------

INSERT INTO program_registrations
(id, registration_date, confirmation_code, is_paid, amount_paid, number_of_tickets,
 rating, status, payment_status, user_id, program_id)
VALUES
    (1, '2026-06-15 10:30:00', 'PROG-001-AAA', true, 120.00, 1, 5, 'CONFIRMED', 'PAID', 2, 1),
    (2, '2026-06-16 11:00:00', 'PROG-002-BBB', false, 0.00, 2, NULL, 'PENDING', 'PENDING', 3, 2),
    (3, '2026-06-17 12:15:00', 'PROG-003-CCC', true, 95.00, 1, NULL, 'CONFIRMED', 'PAID', 4, 3),
    (4, '2026-06-18 17:20:00', 'PROG-004-DDD', false, 0.00, 1, NULL, 'CANCELLED', 'PENDING', 2, 4);

-- ------------------------------------------------------------
-- 9. Mensajes de contacto
-- ------------------------------------------------------------

INSERT INTO contact_messages
(id, full_name, email, category, reference_id, message, created_at)
VALUES
    (1, 'Ana Torres', 'ana.torres@test.com', 'WORKSHOP', 1,
     'Me gustaría recibir más información sobre el taller de escritura emocional.',
     '2026-06-20 09:15:00'),

    (2, 'Entidad Social Norte', 'contacto@entidadnorte.org', 'PROGRAM', 2,
     'Estamos interesadas en adaptar el programa EmoTesoros para un grupo de personas adultas.',
     '2026-06-21 13:45:00'),

    (3, 'María López', 'maria.lopez@test.com', 'BIBLIOTECA', NULL,
     'Quisiera saber cómo participar en una actividad relacionada con Biblioteca Viva.',
     '2026-06-22 18:10:00'),

    (4, 'Javier Martín', 'javier.martin@test.com', 'SALUD_L_MENTAL', NULL,
     'Me interesa conocer próximas propuestas relacionadas con salud mental y creatividad.',
     '2026-06-23 11:30:00');

-- ------------------------------------------------------------
-- 10. Calendario administrativo
-- Estas entradas se crean desde los services al crear talleres/programas.
-- Aquí se insertan para facilitar la revisión visual del panel admin.
-- ------------------------------------------------------------

INSERT INTO admin_calendar
(id, title, start_date, end_date, hour, duration_minutes, category, description, speaker_name)
VALUES
    (1, 'Taller: Escritura emocional', '2026-07-15', '2026-07-15', '18:00', 120, 'WORKSHOP',
     'Entrada de calendario asociada al taller Escritura emocional.',
     'Cristina García'),

    (2, 'Taller: Espontaneidad creativa', '2026-07-22', '2026-07-22', '17:30', 90, 'WORKSHOP',
     'Entrada de calendario asociada al taller Espontaneidad creativa.',
     'Mónica Calvo'),

    (3, 'Programa: EmoCreativos', '2026-09-05', '2026-10-05', '18:00', 90, 'PROGRAM',
     'Entrada de calendario asociada al programa EmoCreativos.',
     'Cristina García'),

    (4, 'Programa: EmoTesoros', '2026-09-12', '2026-10-24', '17:30', 120, 'PROGRAM',
     'Entrada de calendario asociada al programa EmoTesoros.',
     'Mónica Calvo'),

    (5, 'Evento: Encuentro creativo abierto', '2026-07-10', '2026-07-10', '18:00', 90, 'EVENT',
     'Entrada de calendario para evento público de prueba.',
     'Cristina García');

-- ------------------------------------------------------------
-- 11. Comprobaciones rápidas
-- ------------------------------------------------------------

SELECT 'Usuarios' AS tabla, COUNT(*) AS total FROM users
UNION ALL
SELECT 'Ponentes', COUNT(*) FROM speakers
UNION ALL
SELECT 'Eventos', COUNT(*) FROM events
UNION ALL
SELECT 'Talleres', COUNT(*) FROM workshops
UNION ALL
SELECT 'Programas', COUNT(*) FROM programs
UNION ALL
SELECT 'Inscripciones talleres', COUNT(*) FROM registrations
UNION ALL
SELECT 'Inscripciones programas', COUNT(*) FROM program_registrations
UNION ALL
SELECT 'Mensajes contacto', COUNT(*) FROM contact_messages
UNION ALL
SELECT 'Calendario admin', COUNT(*) FROM admin_calendar;