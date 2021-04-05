CREATE SCHEMA IF NOT EXISTS db_explorer;

CREATE SEQUENCE db_explorer.sq_database START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE db_explorer.database
(
    id              BIGINT          NOT NULL DEFAULT nextval('db_explorer.sq_database'),
    name            VARCHAR(255)    NOT NULL,
    hostname        VARCHAR(255)    NOT NULL,
    port            INT             NOT NULL,
    database_name   VARCHAR(255)    NOT NULL,
    username        VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    created         TIMESTAMP       NOT NULL,
    last_modified   TIMESTAMP       NOT NULL,
    CONSTRAINT pk_database PRIMARY KEY (id)
);
