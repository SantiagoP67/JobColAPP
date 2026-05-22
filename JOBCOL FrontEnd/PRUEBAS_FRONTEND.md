# PRUEBAS DE FRONTEND — JobCol

## 1. Propósito

Este documento presenta los resultados del control de calidad implementado para el frontend del Sistema JobCol, detallando las **155 pruebas unitarias y de integración** realizadas con sus respectivos resultados.

## 2. Alcance

El alcance cubre pruebas de componentes, servicios, formularios CRUD y flujos del sistema desarrollado en **React 19.2.4** con **Vite 6.4.1**.

## 3. Tecnologías

| Tecnología/Herramienta | Propósito y Uso |
|---|---|
| React 19.2.4 | Framework principal para el desarrollo de componentes y aplicación frontend |
| Vitest 4.1.6 | Framework de testing para pruebas unitarias y de integración |
| @testing-library/react | Renderizado y consultas de componentes React en pruebas |
| @testing-library/jest-dom | Matchers extendidos para aserciones de DOM |
| happy-dom | Entorno DOM simulado para ejecución de pruebas |
| JavaScript (ES Modules) | Lenguaje de programación del frontend |

## 4. Resultados Globales

| Métrica | Valor |
|---|---|
| Total de Especificaciones (Specs) | **155** |
| Pruebas Exitosas | **155 (100%)** |
| Pruebas Fallidas | **0 (0%)** |
| Tiempo de Ejecución | **~5.27 segundos** |
| Archivos de Test | **10** |

## 5. Módulos

---

### 5.1 Módulo de Autenticación (AuthModal) — 23 pruebas

Este módulo gestiona el formulario completo de inicio de sesión y registro de usuarios, incluyendo validaciones de campos, selección de rol (candidato/empleador) y envío de credenciales al backend. La evaluación se realizó mediante el componente **AuthModal**, ejecutándose un total de **23 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| AUTH-001 | Autenticación | Unitaria | Creación del componente | El componente se instancia correctamente | Sí |
| AUTH-002 | Autenticación | Unitaria | Tab login activo por defecto | El tab login tiene clase 'active' cuando initialTab='login' | Sí |
| AUTH-003 | Autenticación | Unitaria | Tab register activo | El tab register tiene clase 'active' cuando initialTab='register' | Sí |
| AUTH-004 | Autenticación | Interfaz | Cambio a tab de registro | Se puede cambiar al tab de registro correctamente | Sí |
| AUTH-005 | Autenticación | Interfaz | Cambio a tab de login | Se puede cambiar al tab de login correctamente | Sí |
| AUTH-006 | Autenticación | Interfaz | Campos de registro visibles | Se muestran los 6 campos del formulario de registro | Sí |
| AUTH-007 | Autenticación | Unitaria | Rol por defecto (candidato) | El radio de candidato está seleccionado por defecto | Sí |
| AUTH-008 | Autenticación | Interfaz | Cambio de rol a empleador | Se puede seleccionar el rol de empleador | Sí |
| AUTH-009 | Autenticación | Unitaria | Validación teléfono (solo números) | Retorna error si el valor contiene letras | Sí |
| AUTH-010 | Autenticación | Unitaria | Validación teléfono (longitud) | Retorna error si no tiene exactamente 10 dígitos | Sí |
| AUTH-011 | Autenticación | Unitaria | Validación cédula (solo números) | Retorna error si contiene letras | Sí |
| AUTH-012 | Autenticación | Unitaria | Validación cédula (rango) | Retorna error si no tiene entre 6 y 12 dígitos | Sí |
| AUTH-013 | Autenticación | Unitaria | Validación email | Retorna error si el formato es inválido | Sí |
| AUTH-014 | Autenticación | Unitaria | Validación nombre (mínimo) | Retorna error si tiene menos de 2 caracteres | Sí |
| AUTH-015 | Autenticación | Unitaria | Validación nombre (solo letras) | Retorna error si contiene números | Sí |
| AUTH-016 | Autenticación | Unitaria | Validación username (mínimo) | Retorna error si tiene menos de 3 caracteres | Sí |
| AUTH-017 | Autenticación | Unitaria | Validación contraseña (mínimo 8) | Retorna error si tiene menos de 8 caracteres | Sí |
| AUTH-018 | Autenticación | Unitaria | Validación contraseña (mayúscula) | Retorna error si no tiene al menos una mayúscula | Sí |
| AUTH-019 | Autenticación | Unitaria | Validación contraseña (número) | Retorna error si no tiene al menos un número | Sí |
| AUTH-020 | Autenticación | Interfaz | Checks de contraseña visibles | Los 4 requisitos de contraseña se muestran en registro | Sí |
| AUTH-021 | Autenticación | Integración | Login exitoso | Se llama al servicio de login y se invoca onRequireVerifyCode | Sí |
| AUTH-022 | Autenticación | Integración | Registro exitoso | Se llama al servicio de registro y se invoca onRegisterSuccess | Sí |
| AUTH-023 | Autenticación | Integración | Error en login | Se muestra mensaje de error cuando el servicio falla | Sí |

---

### 5.2 Módulo de Test IA (TestModal) — 20 pruebas

Este módulo gestiona la evaluación con IA de candidatos, con preguntas adaptativas de selección múltiple y respuesta abierta, generadas por OpenAI. La evaluación se realizó mediante el componente **TestModal**, ejecutándose **20 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| TST-001 | Test IA | Unitaria | Creación del componente | El componente se instancia correctamente | Sí |
| TST-002 | Test IA | Unitaria | Estado de carga inicial | Se muestra "Generando pregunta con IA" | Sí |
| TST-003 | Test IA | Unitaria | Sin feedback inicial | No hay feedback visible al inicio | Sí |
| TST-004 | Test IA | Interfaz | Botón habilitado tras selección | El botón se habilita al seleccionar una opción | Sí |
| TST-005 | Test IA | Unitaria | MAX_QUESTIONS = 3 | Se muestra "de 3" en el contador | Sí |
| TST-006 | Test IA | Interfaz | Título visible | Se muestra "Test de Aptitud con IA" | Sí |
| TST-007 | Test IA | Interfaz | Categoría visible | Se muestra la categoría del empleo | Sí |
| TST-008 | Test IA | Interfaz | Barra de progreso | El elemento .test-progress-bar existe | Sí |
| TST-009 | Test IA | Integración | Carga primera pregunta | Se llama a generateQuestion al abrir el modal | Sí |
| TST-010 | Test IA | Interfaz | 4 opciones renderizadas | Las 4 opciones de selección múltiple son visibles | Sí |
| TST-011 | Test IA | Interfaz | Seleccionar opción | La clase 'selected' se aplica a la opción elegida | Sí |
| TST-012 | Test IA | Interfaz | Textarea para pregunta abierta | Se muestra textarea para preguntas tipo open | Sí |
| TST-013 | Test IA | Unitaria | canSubmit sin selección | El botón está deshabilitado sin selección | Sí |
| TST-014 | Test IA | Unitaria | Respuesta abierta < 10 chars | El botón está deshabilitado con menos de 10 caracteres | Sí |
| TST-015 | Test IA | Unitaria | Respuesta abierta ≥ 10 chars | El botón se habilita con 10 o más caracteres | Sí |
| TST-016 | Test IA | Integración | Respuesta correcta | Se calcula score=100 y se muestra feedback positivo | Sí |
| TST-017 | Test IA | Integración | Respuesta incorrecta | Se calcula score=0 y se muestra feedback negativo | Sí |
| TST-018 | Test IA | Integración | Evaluación respuesta abierta | Se llama a evaluateOpenAnswer del servicio IA | Sí |
| TST-019 | Test IA | Integración | Finalización del test | Se llama onComplete con score promedio y totalQuestions | Sí |
| TST-020 | Test IA | Unitaria | Reset al cerrar | Se reinicia el estado y se invoca onClose | Sí |

---

### 5.3 Módulo de Tarjetas de Empleo (JobCard) — 15 pruebas

Este módulo renderiza las tarjetas de ofertas de empleo con información del puesto, acciones de guardar y postularse. La evaluación se realizó mediante el componente **JobCard**, ejecutándose **15 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| JOB-001 | Empleos | Unitaria | Creación del componente | El componente se instancia correctamente | Sí |
| JOB-002 | Empleos | Unitaria | Muestra título del empleo | Se renderiza el título del empleo | Sí |
| JOB-003 | Empleos | Unitaria | Muestra empresa | Se renderiza el nombre de la empresa | Sí |
| JOB-004 | Empleos | Unitaria | Muestra ubicación | Se renderiza la ubicación | Sí |
| JOB-005 | Empleos | Unitaria | Muestra salario | Se renderiza el salario | Sí |
| JOB-006 | Empleos | Unitaria | Muestra tiempo publicado | Se renderiza timePosted | Sí |
| JOB-007 | Empleos | Interfaz | Botón postularme | Se muestra botón "Postularme" | Sí |
| JOB-008 | Empleos | Interfaz | Botón postulado | Se muestra "Postulado ✓" si isApplied=true | Sí |
| JOB-009 | Empleos | Interfaz | Botón oferta cerrada | Se muestra "Oferta Cerrada" si isClosed=true | Sí |
| JOB-010 | Empleos | Interfaz | Click en guardar empleo | Se invoca onSave al hacer click en bookmark | Sí |
| JOB-011 | Empleos | Interfaz | Icono guardado (filled) | El título cambia a "Quitar de guardados" si isSaved | Sí |
| JOB-012 | Empleos | Interfaz | Click en tarjeta | Se invoca onClick al hacer click en la tarjeta | Sí |
| JOB-013 | Empleos | Interfaz | Click en postular | Se invoca onApply al hacer click en postularme | Sí |
| JOB-014 | Empleos | Unitaria | Badge de tipo visible | Se muestra el tipo de empleo como badge | Sí |
| JOB-015 | Empleos | Interfaz | Badge cerrada visible | Se muestra badge "Cerrada" si isClosed=true | Sí |

---

### 5.4 Módulo de Detalles del Empleo (JobDetailsModal) — 14 pruebas

Este módulo muestra la información detallada de una oferta de empleo, incluyendo requisitos, beneficios y acciones de postulación. Ejecutándose **14 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| JDM-001 | Detalles Empleo | Unitaria | Retorna null sin job | No renderiza nada si job es null | Sí |
| JDM-002 | Detalles Empleo | Unitaria | Muestra título | Se renderiza job.title | Sí |
| JDM-003 | Detalles Empleo | Unitaria | Muestra empresa | Se renderiza job.company | Sí |
| JDM-004 | Detalles Empleo | Unitaria | Muestra ubicación | Se renderiza job.location | Sí |
| JDM-005 | Detalles Empleo | Unitaria | Muestra salario | Se renderiza job.salary | Sí |
| JDM-006 | Detalles Empleo | Unitaria | Muestra descripción | Se renderiza job.description | Sí |
| JDM-007 | Detalles Empleo | Interfaz | Botón postularme ahora | Se muestra "Postularme Ahora" | Sí |
| JDM-008 | Detalles Empleo | Interfaz | Botón ya postulado | Se muestra "Ya Postulado ✓" si isApplied | Sí |
| JDM-009 | Detalles Empleo | Interfaz | Banner oferta cerrada | Se muestra banner de oferta cerrada | Sí |
| JDM-010 | Detalles Empleo | Interfaz | Banner ya postulado | Se muestra banner de ya postulado | Sí |
| JDM-011 | Detalles Empleo | Interfaz | Click en guardar | Se invoca onSave | Sí |
| JDM-012 | Detalles Empleo | Interfaz | Click en postularme | Se invoca onApply y onClose | Sí |
| JDM-013 | Detalles Empleo | Interfaz | Click en cerrar | Se invoca onClose | Sí |
| JDM-014 | Detalles Empleo | Unitaria | Secciones estáticas | Se renderizan Requisitos y Beneficios | Sí |

---

### 5.5 Módulo de Empleos Guardados (SavedJobsModal) — 10 pruebas

Este módulo muestra los empleos que el usuario ha marcado como favoritos. Ejecutándose **10 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| SAV-001 | Guardados | Unitaria | No renderiza si cerrado | innerHTML vacío si isOpen=false | Sí |
| SAV-002 | Guardados | Unitaria | Muestra título | "Mis Empleos Guardados" visible | Sí |
| SAV-003 | Guardados | Unitaria | Estado vacío | Mensaje "No tienes empleos guardados" | Sí |
| SAV-004 | Guardados | Unitaria | Contador de empleos | "Tienes N empleo(s) guardado(s)" | Sí |
| SAV-005 | Guardados | Unitaria | Lista de empleos | Empleos guardados renderizados | Sí |
| SAV-006 | Guardados | Interfaz | Botón ver detalles | Se invoca onViewDetails | Sí |
| SAV-007 | Guardados | Interfaz | Botón eliminar guardado | Se invoca onRemove con el ID | Sí |
| SAV-008 | Guardados | Unitaria | Título del empleo | Se muestra el título de cada empleo | Sí |
| SAV-009 | Guardados | Unitaria | Empresa del empleo | Se muestra la empresa de cada empleo | Sí |
| SAV-010 | Guardados | Interfaz | Botón cerrar modal | Se invoca onClose | Sí |

---

### 5.6 Módulo de Contratos (ContractDetailsModal) — 15 pruebas

Este módulo muestra los detalles de un contrato incluyendo timeline dinámico, estado, monto acordado y fechas. Ejecutándose **15 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| CON-001 | Contratos | Unitaria | Retorna null sin contrato | No renderiza si contract es null | Sí |
| CON-002 | Contratos | Unitaria | Muestra título | Se renderiza título de la oferta | Sí |
| CON-003 | Contratos | Unitaria | Badge PENDIENTE | Badge "PENDIENTE" visible para status=PENDING | Sí |
| CON-004 | Contratos | Unitaria | Badge ACTIVO | Badge "ACTIVO" visible para status=ACTIVE | Sí |
| CON-005 | Contratos | Unitaria | Badge FINALIZANDO | Badge "FINALIZANDO" para status=PENDING_FINISH | Sí |
| CON-006 | Contratos | Unitaria | Badge FINALIZADO | Badge "FINALIZADO" para status=FINISHED | Sí |
| CON-007 | Contratos | Unitaria | Badge RECHAZADO | Badge "RECHAZADO" para status=REJECTED | Sí |
| CON-008 | Contratos | Unitaria | Formato de fecha | Fecha formateada en dd/mm/yyyy es-CO | Sí |
| CON-009 | Contratos | Unitaria | Fecha nula | Muestra "Sin fecha" cuando date es null | Sí |
| CON-010 | Contratos | Unitaria | Formato de moneda | Monto formateado en COP | Sí |
| CON-011 | Contratos | Unitaria | Monto nulo | Muestra "$0" cuando amount es null | Sí |
| CON-012 | Contratos | Unitaria | Timeline 5 pasos | Se renderizan 5 items en el timeline | Sí |
| CON-013 | Contratos | Unitaria | Timeline rechazado | Mensaje de rechazo en lugar de timeline | Sí |
| CON-014 | Contratos | Unitaria | Muestra categoría | Categoría de la oferta visible | Sí |
| CON-015 | Contratos | Interfaz | Botón cerrar | Se invoca onClose al hacer click | Sí |

---

### 5.7 Módulo de Reseñas (ReviewModal) — 15 pruebas

Este módulo gestiona la calificación de servicios con estrellas, comentarios y evidencia fotográfica obligatoria para trabajadores. Ejecutándose **15 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| REV-001 | Reseñas | Unitaria | Creación del componente | Se instancia correctamente | Sí |
| REV-002 | Reseñas | Unitaria | Rating por defecto (5) | Se muestra "Excelente" | Sí |
| REV-003 | Reseñas | Interfaz | Selección de estrellas | Se puede cambiar el rating | Sí |
| REV-004 | Reseñas | Interfaz | Texto Excelente (rating=5) | "🤩 Excelente" visible | Sí |
| REV-005 | Reseñas | Interfaz | Texto Malo (rating=2) | "😕 Malo" visible | Sí |
| REV-006 | Reseñas | Interfaz | Campo de comentario | Se puede escribir en el textarea | Sí |
| REV-007 | Reseñas | Interfaz | Upload imagen (trabajador) | Sección de evidencia visible | Sí |
| REV-008 | Reseñas | Interfaz | Sin upload (empleador) | Sección de evidencia oculta | Sí |
| REV-009 | Reseñas | Unitaria | Imagen obligatoria trabajador | Se muestra error si no sube foto | Sí |
| REV-010 | Reseñas | Interfaz | Vista previa de imagen | Preview visible al seleccionar | Sí |
| REV-011 | Reseñas | Interfaz | Eliminar imagen | Preview removido al eliminar | Sí |
| REV-012 | Reseñas | Integración | Envío exitoso | Se llama onSubmit con rating, comment, image | Sí |
| REV-013 | Reseñas | Integración | Estado de carga | Overlay "Enviando calificación" visible | Sí |
| REV-014 | Reseñas | Unitaria | Nombre del usuario objetivo | Se muestra targetName en subtítulo | Sí |
| REV-015 | Reseñas | Interfaz | Botón cancelar | Se invoca onClose al cancelar | Sí |

---

### 5.8 Módulo de Resultados del Test (TestResultModal) — 10 pruebas

Este módulo muestra los resultados finales del test de aptitud con IA, incluyendo score circular animado y desglose por pregunta. Ejecutándose **10 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| TRM-001 | Resultados | Unitaria | Retorna null sin resultado | No renderiza si result es null | Sí |
| TRM-002 | Resultados | Unitaria | Score como porcentaje | Se renderiza el score numérico | Sí |
| TRM-003 | Resultados | Unitaria | Título aprobado (≥50%) | Se muestra "¡Excelente!" | Sí |
| TRM-004 | Resultados | Unitaria | Título reprobado (<50%) | Se muestra "Sigue intentando" | Sí |
| TRM-005 | Resultados | Unitaria | Mensaje aprobado | Texto de postulación registrada | Sí |
| TRM-006 | Resultados | Unitaria | Mensaje reprobado | Texto de puntaje no alcanzado | Sí |
| TRM-007 | Resultados | Unitaria | Desglose por pregunta | Se muestran las 3 preguntas | Sí |
| TRM-008 | Resultados | Unitaria | Nivel básico | "(básico)" visible en pregunta 1 | Sí |
| TRM-009 | Resultados | Unitaria | Promedio final | "Promedio Final" visible | Sí |
| TRM-010 | Resultados | Interfaz | Botón continuar | Se invoca onClose al hacer click | Sí |

---

### 5.9 Componentes UI Base (Modal, Button, Input) — 13 pruebas

Estos módulos proveen los componentes reutilizables base de la interfaz. Ejecutándose **13 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| UI-001 | Modal | Unitaria | No renderiza si cerrado | Retorna null si isOpen=false | Sí |
| UI-002 | Modal | Unitaria | Renderiza children | Muestra contenido si isOpen=true | Sí |
| UI-003 | Modal | Interfaz | Click en overlay cierra | Se invoca onClose | Sí |
| UI-004 | Modal | Interfaz | Click en contenido no cierra | onClose NO se invoca | Sí |
| UI-005 | Modal | Unitaria | Bloqueo de scroll | document.body overflow='hidden' | Sí |
| UI-006 | Button | Unitaria | Renderiza children | El botón muestra su contenido | Sí |
| UI-007 | Button | Unitaria | Clase btn-primary | Aplica clase por defecto | Sí |
| UI-008 | Button | Unitaria | Clase btn-secondary | Aplica clase correcta | Sí |
| UI-009 | Button | Unitaria | Clase btn-ghost | Aplica clase correcta | Sí |
| UI-010 | Button | Unitaria | Prop disabled | Se deshabilita correctamente | Sí |
| UI-011 | Input | Unitaria | Renderiza label | Label visible | Sí |
| UI-012 | Input | Unitaria | Clase de error | Aplica clase 'input-error' | Sí |
| UI-013 | Input | Unitaria | Mensaje de error | Muestra el mensaje de error | Sí |

---

### 5.10 Servicios (authService, offerService, contractService, etc.) — 20 pruebas

Estos módulos cubren la capa de servicios que conecta el frontend con el backend mediante Axios. Ejecutándose **20 pruebas** completadas exitosamente.

| ID | Módulo | Tipo de Prueba | Contenido | Resultados Esperados | Éxito |
|---|---|---|---|---|---|
| SRV-001 | authService | Unitaria | getUserFromToken válido | Retorna email, username y roles del JWT | Sí |
| SRV-002 | authService | Unitaria | getUserFromToken sin token | Retorna null | Sí |
| SRV-003 | authService | Unitaria | getAppRole ADMIN | Retorna "ADMIN" | Sí |
| SRV-004 | authService | Unitaria | getAppRole EMPLEADOR | Retorna "EMPLEADOR" | Sí |
| SRV-005 | authService | Unitaria | getAppRole TRABAJADOR | Retorna "TRABAJADOR" | Sí |
| SRV-006 | authService | Unitaria | getAppRole null user | Retorna null | Sí |
| SRV-007 | authService | Unitaria | getAppRole sin roles | Retorna "TRABAJADOR" por defecto | Sí |
| SRV-008 | authService | Integración | login guarda token | Se guarda token en localStorage | Sí |
| SRV-009 | authService | Integración | register guarda token | Se guarda token en localStorage | Sí |
| SRV-010 | offerService | Integración | getAllOffers | Se llama a GET /offers con token | Sí |
| SRV-011 | offerService | Integración | createOffer | Se llama a POST /offers con datos | Sí |
| SRV-012 | offerService | Integración | deleteOffer | Se llama a DELETE /offers/:id | Sí |
| SRV-013 | offerService | Integración | closeOffer | Se llama a PATCH /offers/:id/close | Sí |
| SRV-014 | contractService | Integración | getContractsByUser | Se llama a GET /contracts/user/:id | Sí |
| SRV-015 | contractService | Integración | createContract | Se llama a POST /contracts | Sí |
| SRV-016 | contractService | Integración | acceptContract | Se llama a PUT /contracts/:id/accept | Sí |
| SRV-017 | contractService | Integración | rejectContract | Se llama a PUT /contracts/:id/reject | Sí |
| SRV-018 | postulationService | Integración | createPostulation payload | Payload contiene offerId, workerId, status PENDING | Sí |
| SRV-019 | aiTestService | Unitaria | getDifficulty pregunta 1 | Retorna "basico" | Sí |
| SRV-020 | aiTestService | Unitaria | getDifficulty pregunta 2 | Retorna "intermedio" si anteriores correctas | Sí |

---

## 6. Comando de Ejecución

```bash
npm run test
# o directamente
npx vitest run
```

## 7. Estructura de Archivos de Test

```
src/test/
├── setup.js                              # Configuración global y mocks
├── components/
│   ├── AuthModal.test.jsx                # 23 pruebas
│   ├── TestModal.test.jsx                # 20 pruebas
│   ├── JobCard.test.jsx                  # 15 pruebas
│   ├── JobDetailsModal.test.jsx          # 14 pruebas
│   ├── SavedJobsModal.test.jsx           # 10 pruebas
│   ├── ContractDetailsModal.test.jsx     # 15 pruebas
│   ├── ReviewModal.test.jsx              # 15 pruebas
│   ├── TestResultModal.test.jsx          # 10 pruebas
│   └── UIComponents.test.jsx            # 13 pruebas
└── services/
    └── services.test.js                  # 20 pruebas
```
