-- CREATE DATABASE IF NOT EXISTS weave;

-- USE weave;

CREATE TABLE IF NOT EXISTS namespace
(
    id VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_owner (owner),
    INDEX inx_owner_email (owner_email)
);

CREATE TABLE IF NOT EXISTS runtime_config
(
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'who does IUD',
    PRIMARY KEY (k)
);

CREATE TABLE IF NOT EXISTS runtime_config_history
(
    id INT NOT NULL AUTO_INCREMENT,
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'who does IUD',
    PRIMARY KEY (id),
    INDEX idx_k (k)
);