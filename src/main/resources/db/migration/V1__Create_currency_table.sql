CREATE TABLE currency
(
    currency_code        VARCHAR(3) PRIMARY KEY,
    currency_description VARCHAR(255),
    date_time            TIMESTAMP,
    rates               DECIMAL(10, 2)
);