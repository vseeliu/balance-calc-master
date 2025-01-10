CREATE TABLE `account` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `account_number` VARCHAR(255) NOT NULL,
   `balance` DECIMAL(10, 2) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_account_number` (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `transaction` (
   `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
   `transaction_id` VARCHAR(255) NOT NULL,
   `source_account_number` VARCHAR(255) NOT NULL,
   `target_account_number` VARCHAR(255) NOT NULL,
   `amount` DECIMAL(10, 2) NOT NULL,
   `timestamp` BIGINT(20) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `uk_transaction_id` (`transaction_id`),
   FOREIGN KEY (`source_account_number`) REFERENCES `account`(`account_number`),
   FOREIGN KEY (`target_account_number`) REFERENCES `account`(`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into account(account_number, balance) values('111111111111',10000.00);
insert into account(account_number, balance) values('222222222221',2000.10);
insert into account(account_number, balance) values('111111111119',9988.90);
insert into account(account_number, balance) values('222222222229',2011.10);
insert into transaction(transaction_id, source_account_number, target_account_number, amount, timestamp) values  ('3bf5127c-f3d9-4d7a-9c00-4b2ab32faa5a','111111111111','222222222222',1.11,1736438275);