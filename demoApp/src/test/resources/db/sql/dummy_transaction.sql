INSERT INTO TRANSACTION
(from_account, to_account, transaction_type, amount, transaction_date, comments)
VALUES
(NULL, 1, 'DEPOSIT', 500.5, '2018-01-19 03:14:07', 'Initial Deposit'),
(NULL, 2, 'DEPOSIT', 100, '2018-01-19 03:14:07', 'Initial Deposit'),
(NULL, 3, 'DEPOSIT', 400, '2018-01-19 03:14:07', 'Initial Deposit'),
(1, NULL, 'WITHDRAWAL', 100, '2018-01-19 03:14:07', 'Withdrwal'),
(3, 2, 'TRANSFER', 50.2, '2018-01-19 03:14:07', 'Nintendo Switch');
