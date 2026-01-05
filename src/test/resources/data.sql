-- CREACIÓN DE TABLAS
-- ===========================

CREATE TABLE rol (
    idrol INT PRIMARY KEY,
    nomrol VARCHAR(50) NOT NULL
);

CREATE TABLE usuario (
    idusuario INT PRIMARY KEY,
    nomusuario VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    email VARCHAR(100),
    activo BOOLEAN
);

CREATE TABLE usuario_rol (
    idusuario INT NOT NULL,
    idrol INT NOT NULL,
    PRIMARY KEY (idusuario, idrol),
    FOREIGN KEY (idusuario) REFERENCES usuario(idusuario),
    FOREIGN KEY (idrol) REFERENCES rol(idrol)
);




CREATE TABLE medico (
    medicoid INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    especialidad VARCHAR(100),
    activo BOOLEAN NOT NULL
);

CREATE TABLE paciente (
    pacienteid INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    edad INT,
    telefono VARCHAR(20),
    dni VARCHAR(20)
);

CREATE TABLE citamedica (
    citaid INT PRIMARY KEY,
    fecha DATE NOT NULL,
    medicoid INT NOT NULL,
    pacienteid INT NOT NULL,
    atendida BOOLEAN NOT NULL,
    FOREIGN KEY (medicoid) REFERENCES medico(medicoid),
    FOREIGN KEY (pacienteid) REFERENCES paciente(pacienteid)
);

-- ===========================
-- INSERCIÓN DE DATOS
-- ===========================

INSERT INTO medico (medicoid, nombre, apellido, telefono, especialidad, activo)
VALUES (1, 'Juan', 'Perez', '999111222', 'Dermatologia', TRUE);

INSERT INTO paciente (pacienteid, nombre, apellido, email, edad, telefono, dni)
VALUES (1, 'Maria', 'Lopez', 'mail@mail.com', 30, '999555666', '44556677');

INSERT INTO citamedica (citaid, fecha, medicoid, pacienteid, atendida)
VALUES (1, '2024-01-01', 1, 1, FALSE);

-- Roles
INSERT INTO rol (idrol, nomrol) VALUES (1, 'USER');
INSERT INTO rol (idrol, nomrol) VALUES (2, 'ADMIN');

---
INSERT INTO usuario (idusuario, nomusuario, password, nombres, apellidos, email, activo)
VALUES (1, 'admin', '$2a$12$SvhRqPeIdeh6wGjaqtAbpOiU5fyJaaM.DktDAS4FpSAXIzTlKfFBe', 'Admin', 'Sistema', 'admin@mail.com', true);

-- Relación usuario-rol
INSERT INTO usuario_rol (idusuario, idrol) VALUES (1, 1);  -- USER
INSERT INTO usuario_rol (idusuario, idrol) VALUES (1, 2);  -- ADMIN