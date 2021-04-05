CREATE USER postgres WITH PASSWORD 'postgres';
CREATE SCHEMA IF NOT EXISTS integration_test;

CREATE TABLE integration_test.user (
    id              INT NOT NULL,
    username        VARCHAR(255),
    is_active       BOOLEAN NOT NULL DEFAULT FALSE,
    registered_at   DATE NOT NULL,
    modified_at     TIMESTAMP NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE integration_test.user_details (
    user_id         INT NOT NULL ,
    phone_number    VARCHAR(255) NOT NULL ,
    CONSTRAINT pk_user_details PRIMARY KEY (user_id),
    CONSTRAINT fk_user_details__user_id FOREIGN KEY (user_id) REFERENCES integration_test.user(id)
);

CREATE TABLE integration_test.login_statistics (
    user_id INT NOT NULL
);

INSERT INTO integration_test.user (id, username, is_active, registered_at, modified_at)
VALUES (1, 'big carl', true, '2021-03-05', '2021-03-15T16:54');

INSERT INTO integration_test.login_statistics (user_id)
VALUES (10), (5), (7), (8), (8), (9), (3), (10), (10), (11), (12), (15);

GRANT USAGE ON SCHEMA integration_test TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA integration_test TO postgres;
