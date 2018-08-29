INSERT INTO TRANSACTION
(id, from_account, to_account, transaction_type, amount, transaction_date, comments)
VALUES
(1, NULL, 1, 'DEPOSIT', 500.5, '2018-01-19 03:14:07', 'Initial Deposit'),
(2, NULL, 2, 'DEPOSIT', 100, '2018-01-19 03:14:07', 'Initial Deposit'),
(3, NULL, 3, 'DEPOSIT', 400, '2018-01-19 03:14:07', 'Initial Deposit'),
(4, 1, NULL, 'WITHDRAWAL', 100, '2018-01-19 03:14:07', 'Withdrwal'),
(5, 3, 2, 'TRANSFER', 50.2, '2018-01-19 03:14:07', 'Nintendo Switch');
