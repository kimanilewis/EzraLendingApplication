-- Drop tables if they exist
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS notification_template;
DROP TABLE IF EXISTS installment;
DROP TABLE IF EXISTS loan;
DROP TABLE IF EXISTS loan_limit;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS late_fee;
DROP TABLE IF EXISTS daily_fee;
DROP TABLE IF EXISTS service_fee;
DROP TABLE IF EXISTS product;

-- Product table
CREATE TABLE product
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                   VARCHAR(100)  NOT NULL,
    description            VARCHAR(500),
    tenure                 INT           NOT NULL,
    tenure_type            VARCHAR(20)   NOT NULL,
    interest_rate          DECIMAL(5, 2) NOT NULL,
    is_installment_loan    BOOLEAN       NOT NULL DEFAULT FALSE,
    days_after_due_for_fee INT           NOT NULL DEFAULT 0,
    created_at             TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    active                 BOOLEAN                DEFAULT TRUE
);

-- Service Fee table
CREATE TABLE service_fee
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id           BIGINT       NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    fee_amount           DECIMAL(10, 2),
    fee_percentage       DECIMAL(5, 2),
    is_percentage        BOOLEAN      NOT NULL,
    apply_at_origination BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product (id)
);

-- Daily Fee table
CREATE TABLE daily_fee
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT         NOT NULL,
    name               VARCHAR(100)   NOT NULL,
    fee_amount         DECIMAL(10, 2) NOT NULL,
    apply_when_overdue BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product (id)
);

-- Late Fee table
CREATE TABLE late_fee
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id             BIGINT       NOT NULL,
    name                   VARCHAR(100) NOT NULL,
    fee_amount             DECIMAL(10, 2),
    fee_percentage         DECIMAL(5, 2),
    is_percentage          BOOLEAN      NOT NULL,
    trigger_days_after_due INT          NOT NULL DEFAULT 1,
    created_at             TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product (id)
);

-- Customer table
CREATE TABLE customer
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    phone          VARCHAR(20)  NOT NULL,
    address        VARCHAR(255),
    date_of_birth  DATE,
    credit_score   INT,
    monthly_income DECIMAL(10, 2),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active         BOOLEAN   DEFAULT TRUE
);

-- Loan Limit table
CREATE TABLE loan_limit
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id      BIGINT         NOT NULL,
    product_id       BIGINT         NOT NULL,
    max_amount       DECIMAL(10, 2) NOT NULL,
    current_utilized DECIMAL(10, 2) DEFAULT 0,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    UNIQUE (customer_id, product_id)
);

-- Loan table
CREATE TABLE loan
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id            BIGINT         NOT NULL,
    product_id             BIGINT         NOT NULL,
    loan_amount            DECIMAL(10, 2) NOT NULL,
    outstanding_principal  DECIMAL(10, 2) NOT NULL,
    outstanding_interest   DECIMAL(10, 2) DEFAULT 0,
    outstanding_fees       DECIMAL(10, 2) DEFAULT 0,
    loan_state             VARCHAR(20)    NOT NULL,
    origination_date       DATE           NOT NULL,
    due_date               DATE           NOT NULL,
    number_of_installments INT            DEFAULT 1,
    created_at             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    FOREIGN KEY (product_id) REFERENCES product (id)
);

-- Installment table
CREATE TABLE installment
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id            BIGINT         NOT NULL,
    installment_number INT            NOT NULL,
    principal_amount   DECIMAL(10, 2) NOT NULL,
    interest_amount    DECIMAL(10, 2) NOT NULL,
    fee_amount         DECIMAL(10, 2) DEFAULT 0,
    due_date           DATE           NOT NULL,
    payment_date       DATE,
    paid_amount        DECIMAL(10, 2) DEFAULT 0,
    status             VARCHAR(20)    NOT NULL,
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loan (id)
);

-- Notification Template table
CREATE TABLE notification_template
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    event_type       VARCHAR(50)  NOT NULL,
    channel          VARCHAR(20)  NOT NULL,
    subject          VARCHAR(200),
    template_content TEXT         NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active           BOOLEAN   DEFAULT TRUE
);

-- Notification table
CREATE TABLE notification
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    loan_id     BIGINT,
    template_id BIGINT,
    event_type  VARCHAR(50) NOT NULL,
    channel     VARCHAR(20) NOT NULL,
    content     TEXT        NOT NULL,
    sent_at     TIMESTAMP,
    status      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    FOREIGN KEY (loan_id) REFERENCES loan (id),
    FOREIGN KEY (template_id) REFERENCES notification_template (id)
);