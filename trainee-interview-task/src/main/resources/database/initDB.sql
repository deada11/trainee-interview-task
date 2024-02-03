CREATE TABLE IF NOT EXISTS products
(
    id    uuid PRIMARY KEY ,
    name  VARCHAR(255) NOT NULL ,
    description VARCHAR(4096),
    price NUMERIC(10,2) DEFAULT 0,
    availability boolean DEFAULT false
);