-- =========================================
-- USERS
-- =========================================
INSERT INTO users (keycloak_user_id, email, username, first_name, last_name, cedula, img_url, active, phone, role) VALUES
('kc-001', 'carlos.mendez@email.com',  'carlosmendez',  'Carlos',  'Mendez',  '12345678',  'https://i.pravatar.cc/150?img=1',  TRUE,  '+573001234567', 'EMPLOYER'),
('kc-002', 'laura.gomez@email.com',    'lauragomez',    'Laura',   'Gomez',   '23456789',  'https://i.pravatar.cc/150?img=2',  TRUE,  '+573009876543', 'WORKER'),
('kc-003', 'andres.rios@email.com',    'andresrios',    'Andres',  'Rios',    '34567890',  'https://i.pravatar.cc/150?img=3',  TRUE,  '+573004567890', 'WORKER'),
('kc-004', 'sofia.ramirez@email.com',  'sofiaramirez',  'Sofia',   'Ramirez', '45678901',  'https://i.pravatar.cc/150?img=4',  TRUE,  '+573007654321', 'EMPLOYER'),
('kc-005', 'miguel.torres@email.com',  'migueltorres',  'Miguel',  'Torres',  '56789012',  'https://i.pravatar.cc/150?img=5',  TRUE,  '+573002345678', 'WORKER'),
('kc-006', 'valeria.castro@email.com', 'valeriacastro', 'Valeria', 'Castro',  '67890123',  'https://i.pravatar.cc/150?img=6',  TRUE,  '+573008765432', 'WORKER'),
('kc-007', 'jorge.pineda@email.com',   'jorgepineda',   'Jorge',   'Pineda',  '78901234',  'https://i.pravatar.cc/150?img=7',  FALSE, '+573003456789', 'WORKER'),
('kc-008', 'diana.mora@email.com',     'dianamora',     'Diana',   'Mora',    '89012345',  'https://i.pravatar.cc/150?img=8',  TRUE,  '+573005678901', 'EMPLOYER');

-- =========================================
-- PROFILES
-- =========================================
INSERT INTO profiles (skills, experience, location, visible, average_rating, total_reviews, user_id) VALUES
('Plomería, electricidad básica',    '5 años como técnico en mantenimiento de edificios',  'Bogotá, Colombia',   TRUE, 4.5, 2, 2),
('Carpintería, pintura',             '3 años en acabados de construcción',                  'Medellín, Colombia', TRUE, 4.2, 1, 3),
('Jardinería, fumigación',           '7 años en mantenimiento de zonas verdes',             'Cali, Colombia',     TRUE, 4.8, 2, 5),
('Limpieza, organización',           '2 años en servicios de aseo doméstico y empresarial', 'Bogotá, Colombia',   TRUE, 3.9, 0, 6),
('Mecánica, electricidad automotriz','4 años como mecánico independiente',                  'Barranquilla, Col.', FALSE,4.0, 0, 7),
('Cocina, repostería',               '6 años como cocinero en restaurantes y eventos',      'Bogotá, Colombia',   TRUE, 4.7, 1, 8);

-- =========================================
-- EMPLOYER PROFILES
-- =========================================
INSERT INTO employer_profiles (company_name, description, location, average_rating, total_jobs_posted, total_reviews, user_id) VALUES
('Constructora Mendez S.A.S',   'Empresa de construcción y remodelaciones con más de 10 años de experiencia en Bogotá.', 'Bogotá, Colombia', 4.5, 3, 2, 1),
('Ramirez Servicios del Hogar', 'Ofrecemos servicios integrales de mantenimiento y limpieza para hogares y empresas.',   'Cali, Colombia',   4.2, 2, 1, 4),
('Eventos & Gastronomía Mora',  'Organización de eventos corporativos y sociales con servicio de catering personalizado.','Bogotá, Colombia', 4.8, 1, 1, 8);

-- =========================================
-- OFFERS
-- =========================================
INSERT INTO offers (title, description, category, location, salary_range, status, publication_date, employer_id) VALUES
('Plomero para reparación urgente',    'Necesitamos un plomero para reparar tuberías en apartamento. Trabajo de 2 días.',       'Plomería',     'Bogotá',     120000, 'OPEN',   '2025-07-01 09:00:00', 1),
('Pintor de interiores',               'Se requiere pintor para pintar 3 habitaciones y sala-comedor. Materiales incluidos.',    'Pintura',      'Medellín',    90000, 'OPEN',   '2025-07-03 10:00:00', 1),
('Mantenimiento de jardín mensual',    'Busco jardinero para mantenimiento mensual de jardín de 200m². Pago quincenal.',         'Jardinería',   'Cali',        80000, 'CLOSED', '2025-06-15 08:00:00', 4),
('Servicio de aseo profundo',          'Limpieza profunda de casa de 3 pisos. Se requiere experiencia en aseo profesional.',    'Limpieza',     'Bogotá',      70000, 'OPEN',   '2025-07-05 11:00:00', 4),
('Electricista para instalación',      'Instalación de tomacorrientes y canaletas en oficina de 80m².',                         'Electricidad', 'Bogotá',     150000, 'OPEN',   '2025-07-06 09:30:00', 8),
('Chef para evento corporativo',       'Cocinero para evento de 50 personas. Menú buffet almuerzo y cena.',                     'Gastronomía',  'Bogotá',     250000, 'CLOSED', '2025-06-20 14:00:00', 8);

-- =========================================
-- POSTULATIONS
-- =========================================
INSERT INTO postulations (status, application_date, calification, worker_id, offer_id) VALUES
('ACCEPTED',  '2025-07-02 10:30:00', 85, 2, 1),
('PENDING',   '2025-07-04 09:00:00',  0, 3, 1),
('ACCEPTED',  '2025-06-16 08:00:00', 90, 5, 3),
('ACCEPTED',  '2025-06-21 10:00:00', 78, 8, 6),
('PENDING',   '2025-07-06 11:00:00',  0, 6, 4),
('REJECTED',  '2025-07-04 14:00:00', 45, 7, 2);

-- =========================================
-- CONTRACTS
-- =========================================
INSERT INTO contracts (start_date, end_date, agreed_amount, worker_finished, employer_finished, status, postulation_id) VALUES
('2025-07-05 08:00:00', '2025-07-06 18:00:00', 240000, TRUE,  TRUE,  'COMPLETED', 1),
('2025-06-18 08:00:00', '2025-06-18 17:00:00',  80000, TRUE,  TRUE,  'COMPLETED', 3),
('2025-06-22 10:00:00', '2025-06-22 20:00:00', 250000, TRUE,  TRUE,  'COMPLETED', 4);

-- =========================================
-- REVIEWS
-- =========================================
INSERT INTO reviews (rating, comment, author_type, image_url, review_date, visible, reviewed_user_id, reviewer_id) VALUES
(5, 'Excelente trabajo, muy puntual y profesional. Resolvió el problema rápidamente.', 'EMPLOYER', NULL,                                        '2025-07-07 10:00:00', TRUE, 2, 1),
(4, 'Buen empleador, pagó a tiempo y el espacio de trabajo estaba bien preparado.',    'WORKER',   NULL,                                        '2025-07-07 11:00:00', TRUE, 1, 2),
(5, 'El jardín quedó perfecto, superó mis expectativas. Lo contrataré de nuevo.',      'EMPLOYER', 'https://picsum.photos/seed/jardin_rev/400/300', '2025-06-19 09:00:00', TRUE, 5, 4),
(4, 'Trabajo agradable, instrucciones claras. Recomiendo a este empleador.',           'WORKER',   NULL,                                        '2025-06-19 10:00:00', TRUE, 4, 5),
(5, 'El menú estuvo espectacular. Los invitados quedaron muy satisfechos.',            'EMPLOYER', 'https://picsum.photos/seed/menu_rev/400/300',  '2025-06-23 08:00:00', TRUE, 8, 4),
(3, 'El evento fue bien pero el espacio de cocina era reducido para preparar todo.',   'WORKER',   NULL,                                        '2025-06-23 09:00:00', TRUE, 4, 8);

-- =========================================
-- NOTIFICATIONS
-- =========================================
INSERT INTO notifications (title, message, type, read, created_at, user_id) VALUES
('Nueva postulación',        'Laura Gomez se postuló a tu oferta "Plomero para reparación urgente".',     'POSTULATION', FALSE, '2025-07-02 10:31:00', 1),
('Postulación aceptada',     'Tu postulación para "Plomero para reparación urgente" fue aceptada.',       'POSTULATION', TRUE,  '2025-07-02 11:00:00', 2),
('Contrato iniciado',        'Tu contrato para la oferta "Plomero para reparación urgente" comenzó.',     'CONTRACT',    TRUE,  '2025-07-05 08:05:00', 2),
('Nueva reseña recibida',    'Carlos Mendez te dejó una reseña de 5 estrellas.',                          'REVIEW',      FALSE, '2025-07-07 10:01:00', 2),
('Postulación rechazada',    'Tu postulación para "Pintor de interiores" no fue seleccionada.',           'POSTULATION', FALSE, '2025-07-04 15:00:00', 7),
('Oferta cerrada',           'Tu oferta "Mantenimiento de jardín mensual" fue marcada como cerrada.',     'OFFER',       TRUE,  '2025-06-19 09:30:00', 4);

-- =========================================
-- MESSAGES
-- =========================================
INSERT INTO messages (content, sent_date, read, sender_id, receiver_id) VALUES
('Hola Laura, ¿puedes confirmar si tienes disponibilidad para el viernes?',       '2025-07-02 12:00:00', TRUE,  1, 2),
('Hola Carlos, sí tengo disponibilidad el viernes desde las 8am.',                '2025-07-02 12:15:00', TRUE,  2, 1),
('Perfecto. La dirección es Calle 80 #15-32, apto 401. ¿Necesitas algo?',         '2025-07-02 12:20:00', TRUE,  1, 2),
('Solo necesito saber dónde está la llave de paso principal del edificio.',        '2025-07-02 12:25:00', FALSE, 2, 1),
('Hola Miguel, ¿cuánto cobras por el mantenimiento mensual del jardín?',          '2025-06-14 09:00:00', TRUE,  4, 5),
('Buenos días Sofía, el precio es de $80.000 por visita quincenal.',              '2025-06-14 09:30:00', TRUE,  5, 4);

-- =========================================
-- VERIFICATION CODES
-- =========================================
INSERT INTO verification_code (code, expiration, used, user_id) VALUES
('493821', '2025-07-01 10:00:00', TRUE,  2),
('827465', '2025-07-03 14:00:00', TRUE,  3),
('112938', '2025-07-06 16:00:00', FALSE, 6),
('756492', '2025-07-07 09:00:00', FALSE, 5);

-- =========================================
-- JOB ASSESSMENTS
-- =========================================
INSERT INTO job_assessments (offer_id, user_id, score, level, completed, duration_seconds) VALUES
(1, 2, 85.0, 'ADVANCED',      TRUE,  480),
(1, 3, 62.5, 'INTERMEDIATE',  TRUE,  610),
(5, 2, 70.0, 'INTERMEDIATE',  TRUE,  390),
(4, 6,  0.0, 'BEGINNER',      FALSE, NULL);

-- =========================================
-- QUESTIONS
-- =========================================
INSERT INTO questions (assessment_id, question_text, type, options, correct_answer) VALUES
(1, '¿Cuál es el diámetro estándar de tubería PVC para instalaciones domésticas?',
    'MULTIPLE_CHOICE',
    '["1/2 pulgada","3/4 pulgada","1 pulgada","2 pulgadas"]',
    '1/2 pulgada'),
(1, '¿Qué material se usa para sellar uniones de tubería roscada?',
    'MULTIPLE_CHOICE',
    '["Silicona","Teflón","Pegamento PVC","Masilla de fontanería"]',
    'Teflón'),
(1, '¿Cómo se detecta una fuga de agua detrás de una pared sin abrir?',
    'OPEN',
    NULL,
    'Con un detector de humedad o sensor de infrarrojo'),
(2, '¿Cuántos litros de agua por minuto entrega una ducha estándar?',
    'MULTIPLE_CHOICE',
    '["3-5 lts","8-12 lts","15-20 lts","25-30 lts"]',
    '8-12 lts');

-- =========================================
-- ANSWERS
-- =========================================
INSERT INTO answers (question_id, user_answer, score) VALUES
(1, '1/2 pulgada',                                                          1.0),
(2, 'Teflón',                                                               1.0),
(3, 'Usando un medidor de humedad en paredes o termógrafo infrarrojo.',     0.9),
(4, '8-12 lts',                                                             1.0);

-- =========================================
-- POSTS
-- =========================================
INSERT INTO posts (description, user_id) VALUES
('¡Trabajo terminado! Reparación de tubería en conjunto residencial El Pinar. Cliente muy satisfecho. 💧',       2),
('Jardín transformado en Cali. Antes y después de nuestro trabajo de mantenimiento mensual. 🌿',                 5),
('Buscando plomero o electricista en Bogotá con buenas referencias. ¡Aquí somos el lugar indicado!',             1),
('Nuevo menú para eventos corporativos disponible. Contáctame para cotizaciones. 🍽️',                          8),
('Primer trabajo como freelance completado. Gracias a la plataforma por la oportunidad.',                        6);

-- =========================================
-- MEDIA
-- =========================================
INSERT INTO media (url, type, post_id) VALUES
('https://picsum.photos/seed/plomeria1/800/600',  'IMAGE', 1),
('https://picsum.photos/seed/plomeria2/800/600',  'IMAGE', 1),
('https://picsum.photos/seed/jardin1/800/600',    'IMAGE', 2),
('https://picsum.photos/seed/jardin2/800/600',    'IMAGE', 2),
('https://picsum.photos/seed/menu1/800/600',      'IMAGE', 4),
('https://picsum.photos/seed/aseo1/800/600',      'IMAGE', 5);

-- =========================================
-- LIKES
-- =========================================
INSERT INTO likes (user_id, post_id) VALUES
(1, 1), (4, 1), (5, 1), (8, 1),
(1, 2), (2, 2), (4, 2), (6, 2),
(2, 3), (3, 3),
(1, 4), (2, 4), (3, 4), (5, 4), (6, 4),
(3, 5), (5, 5);

-- =========================================
-- COMMENTS
-- =========================================
INSERT INTO comments (content, user_id, post_id) VALUES
('¡Qué trabajo tan limpio! ¿Cuánto tiempo tardaron?',               4, 1),
('Unos 4 horas. El problema era una tubería fisurada detrás del baño.', 2, 1),
('El jardín quedó increíble, Miguel siempre hace un trabajo excelente.', 4, 2),
('¡Gracias Sofía! Un placer trabajar contigo.',                      5, 2),
('Yo contraté a Laura y fue excelente, la recomiendo totalmente.',   4, 3),
('Gracias Carlos por la confianza 🙏',                               2, 3),
('¿Tienes menú vegetariano disponible?',                             3, 4),
('Claro, tenemos opciones vegetarianas y veganas. ¡Escríbeme!',      8, 4);