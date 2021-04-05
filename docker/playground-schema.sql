CREATE SCHEMA IF NOT EXISTS playground;

CREATE TABLE playground.customer (
    id             INT NOT NULL,
    first_name     VARCHAR(255),
    last_name      VARCHAR(255),
    registered_at  DATE NOT NULL,
    created        TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE playground.customer_details (
    customer_id         INT NOT NULL,
    email               VARCHAR(255) NOT NULL,
    secondary_email     VARCHAR(255),
    phone_number        VARCHAR(255) NOT NULL,
    is_premium          BOOLEAN NOT NULL,
    CONSTRAINT pk_customer_details PRIMARY KEY (customer_id),
    CONSTRAINT fk_customer_details__customer_id FOREIGN KEY (customer_id) REFERENCES playground.customer (id)
);

CREATE TABLE playground.statistics (
    sold_premium_count INT NOT NULL
);
