Feature: Transaction allowed per account type

  The rules for successful transactions are as follow:
  - TRANSFER of money can be made only from an OPEN account to either another OPEN account or LIMITED account
  - DEPOSIT can be made only to OPEN or LIMITED account
  - WITHDRAWAL can be made only from an OPEN account
  - LIMITED account cannot withdraw nor transfer to any account
  - BLOCKED/CLOSED account cannot be part of any transaction

  Scenario Outline: Successfully transfer money between 2 accounts

    Given an origin account with <O_STATUS> status and <O_AMOUNT> in its balance
    And a destination account with <D_STATUS> status and <D_AMOUNT> in its balance
    When a transaction of type TRANSFER is made with the amount of <TRANSFER>
    Then the transaction must be saved
    And the origin account should have <O_BALANCE> in its balance
    And the destination account should have <D_BALANCE> in its balance

    Examples:
      | O_STATUS | O_AMOUNT | D_STATUS | D_AMOUNT | TRANSFER | O_BALANCE | D_BALANCE |
      | OPEN     |      500 | OPEN     |     1000 |      250 |       250 |      1250 |
      | OPEN     |      550 | LIMITED  |      400 |      120 |       430 |       520 |

  Scenario Outline: Successfully deposit money to an account

    Given a destination account with <D_STATUS> status and <D_AMOUNT> in its balance
    When a transaction of type DEPOSIT is made with the amount of <DEPOSIT>
    Then the transaction must be saved
    And the destination account should have <D_BALANCE> in its balance

    Examples:
      | D_STATUS | D_AMOUNT | DEPOSIT | D_BALANCE |
      | OPEN     |      345 |     125 |       470 |
      | LIMITED  |      225 |     125 |       350 |

  Scenario: Successfully withdraw money from an account

    Given an origin account with OPEN status and 300 in its balance
    When a transaction of type WITHDRAWAL is made with the amount of 235
    Then the transaction must be saved
    And the origin account should have 65 in its balance

  Scenario Outline: Failure transferring from account with non OPEN status or to BLOCKED/CLOSED account

    Given an origin account with <O_STATUS> status
    And a destination account with <D_STATUS> status
    When a transaction of type TRANSFER is made with the amount of 1
    Then the transaction mustn't be saved
    And an exception of type ValidationException should be thrown

    Examples:
      | O_STATUS | D_STATUS |
      | OPEN     | CLOSED   |
      | OPEN     | BLOCKED  |
      | LIMITED  | OPEN     |
      | LIMITED  | LIMITED  |
      | LIMITED  | CLOSED   |
      | LIMITED  | BLOCKED  |
      | CLOSED   | OPEN     |
      | CLOSED   | LIMITED  |
      | CLOSED   | CLOSED   |
      | CLOSED   | BLOCKED  |
      | BLOCKED  | OPEN     |
      | BLOCKED  | LIMITED  |
      | BLOCKED  | CLOSED   |
      | BLOCKED  | BLOCKED  |

  Scenario Outline: Failure deposit to BLOCKED/CLOSED account

    Given a destination account with <D_STATUS> status
    When a transaction of type DEPOSIT is made with the amount of 1
    Then the transaction mustn't be saved
    And an exception of type ValidationException should be thrown

    Examples:
      | D_STATUS |
      | CLOSED   |
      | BLOCKED  |

  Scenario Outline: Failure withdraw from non OPEN account

    Given an origin account with <O_STATUS> status
    When a transaction of type WITHDRAWAL is made with the amount of 1
    Then the transaction mustn't be saved
    And an exception of type ValidationException should be thrown

    Examples:
      | O_STATUS |
      | LIMITED  |
      | CLOSED   |
      | BLOCKED  |