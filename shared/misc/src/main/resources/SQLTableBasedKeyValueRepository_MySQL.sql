CREATE TABLE kv
(
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'trace who does the IUD',
    PRIMARY KEY (k)
);

CREATE TABLE kv_history
(
    id INT NOT NULL AUTO_INCREMENT,
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'trace who does the IUD',
    PRIMARY KEY (id),
    INDEX idx_k (k)
);