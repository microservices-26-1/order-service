CREATE TABLE orders.tb_order (
    id          VARCHAR(36)     PRIMARY KEY,
    id_account  VARCHAR(36)     NOT NULL,
    date        TIMESTAMP       NOT NULL,
    total       NUMERIC(10, 2)  NOT NULL
);

CREATE TABLE orders.tb_item (
    id          VARCHAR(36)     PRIMARY KEY,
    id_order    VARCHAR(36)     NOT NULL REFERENCES orders.tb_order(id) ON DELETE CASCADE,
    id_product  VARCHAR(36)     NOT NULL,
    quantity    INTEGER         NOT NULL,
    total       NUMERIC(10, 2)  NOT NULL
);