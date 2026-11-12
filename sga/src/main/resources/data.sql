-- Datos iniciales del SGA. Las altas se omiten cuando la fila ya existe.
INSERT INTO sga_temporada (codigo, nombre, desde, hasta, es_base)
SELECT 'BASE', 'Temporada base', DATE '2020-01-01', DATE '9999-12-31', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sga_temporada WHERE codigo = 'BASE');

INSERT INTO sga_apartamento (codigo, nombre, dormitorios, capacidad, estado_operativo, activo)
SELECT 'APT-301', 'Apartamento con dos balcones', 2, 5, 'PREPARADO', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sga_apartamento WHERE codigo = 'APT-301');

INSERT INTO sga_apartamento (codigo, nombre, dormitorios, capacidad, estado_operativo, activo)
SELECT 'APT-302', 'Apartamento privado con patio', 2, 5, 'PREPARADO', TRUE
WHERE NOT EXISTS (SELECT 1 FROM sga_apartamento WHERE codigo = 'APT-302');

INSERT INTO sga_tarifa (apartamento, temporada, valor)
SELECT a.codigo, 'BASE', 200000.00
FROM sga_apartamento a
WHERE a.codigo IN ('APT-301', 'APT-302')
  AND NOT EXISTS (SELECT 1 FROM sga_tarifa t WHERE t.apartamento = a.codigo AND t.temporada = 'BASE');

INSERT INTO sga_politica_cancelacion (numero, vigente_desde, no_show)
SELECT 1, DATE '2020-01-01', 100.00
WHERE NOT EXISTS (SELECT 1 FROM sga_politica_cancelacion WHERE numero = 1 AND vigente_desde = DATE '2020-01-01');

INSERT INTO sga_tramo_retencion (numero, vigente_desde, horas, retencion)
SELECT 1, DATE '2020-01-01', 0, 50.00
WHERE NOT EXISTS (SELECT 1 FROM sga_tramo_retencion WHERE numero = 1 AND vigente_desde = DATE '2020-01-01' AND horas = 0);

INSERT INTO sga_tramo_retencion (numero, vigente_desde, horas, retencion)
SELECT 1, DATE '2020-01-01', 72, 0.00
WHERE NOT EXISTS (SELECT 1 FROM sga_tramo_retencion WHERE numero = 1 AND vigente_desde = DATE '2020-01-01' AND horas = 72);
