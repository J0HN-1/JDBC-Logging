Feature: Limit account with insufficient funds

  when a transfer transaction is added and the origin account lack sufficient funds,
  the transaction should be canceled and the account status should change to LIMITED.

    Scenario: An account get LIMITED status when it doesn't have enough funds to perform a transfer

  Given an origin account with OPEN status and 1000 in its balance
  And a destination account with OPEN status
  When a transaction of type TRANSFER is made between them with an amount bigger than the origin balance
  Then the transaction mustn't be saved
  And an InsufficientFunds error should be raised
  And the origin account status should be LIMITED