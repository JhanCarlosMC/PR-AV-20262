CREATE TABLE IF NOT EXISTS sga_apartamento (
    codigo VARCHAR(40) PRIMARY KEY,
    nombre VARCHAR(160) NOT NULL,
    dormitorios INT NOT NULL CHECK (dormitorios > 0),
    capacidad INT NOT NULL CHECK (capacidad > 0),
    estado_operativo VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS sga_temporada (
    codigo VARCHAR(40) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    desde DATE NOT NULL,
    hasta DATE NOT NULL CHECK (hasta >= desde),
    es_base BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS sga_tarifa (
    apartamento VARCHAR(40) NOT NULL REFERENCES sga_apartamento(codigo),
    temporada VARCHAR(40) NOT NULL REFERENCES sga_temporada(codigo),
    valor DECIMAL(15, 2) NOT NULL CHECK (valor >= 0),
    PRIMARY KEY (apartamento, temporada)
);

CREATE TABLE IF NOT EXISTS sga_politica_cancelacion (
    numero INT NOT NULL,
    vigente_desde DATE NOT NULL,
    no_show DECIMAL(5, 2) NOT NULL CHECK (no_show BETWEEN 0 AND 100),
    PRIMARY KEY (numero, vigente_desde)
);

CREATE TABLE IF NOT EXISTS sga_tramo_retencion (
    numero INT NOT NULL,
    vigente_desde DATE NOT NULL,
    horas INT NOT NULL CHECK (horas >= 0),
    retencion DECIMAL(5, 2) NOT NULL CHECK (retencion BETWEEN 0 AND 100),
    PRIMARY KEY (numero, vigente_desde, horas),
    FOREIGN KEY (numero, vigente_desde) REFERENCES sga_politica_cancelacion(numero, vigente_desde)
);

CREATE TABLE IF NOT EXISTS sga_folio (
    reserva VARCHAR(15) PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    cerrado BOOLEAN NOT NULL,
    autorizacion_cierre VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS sga_cargo (
    reserva VARCHAR(15) NOT NULL REFERENCES sga_folio(reserva),
    posicion INT NOT NULL,
    concepto VARCHAR(30) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    fecha DATE NOT NULL,
    descripcion VARCHAR(500),
    inverso BOOLEAN NOT NULL,
    PRIMARY KEY (reserva, posicion)
);

CREATE TABLE IF NOT EXISTS sga_pago (
    reserva VARCHAR(15) NOT NULL REFERENCES sga_folio(reserva),
    posicion INT NOT NULL,
    codigo VARCHAR(80) NOT NULL,
    monto DECIMAL(15, 2) NOT NULL,
    medio VARCHAR(30) NOT NULL,
    fecha DATE NOT NULL,
    PRIMARY KEY (reserva, posicion)
);

CREATE SEQUENCE IF NOT EXISTS sga_codigo_reserva START WITH 1;
