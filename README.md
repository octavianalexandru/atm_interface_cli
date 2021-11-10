ATM CLI interface

In order to run:
1. make sure you have gradle installed
2. open terminal and navigate into root app folder
3. run 
    gradle build
4. run 
   gradle --console plain run


The app simulates a simple ATM interface with basic account interactions:

1. login [username]
    - will simulate a user session and permit using the other operations
2. deposit [amount]
    - will add an amount of money on top of your balance
    - if you already are in debt to other users, de command will substract first 
    as much as possible from the deposited amount and try a transfer to as much 
      users as possible in order to cover the debt until the balance it exceeded,
      or all de debt to all users has been paid
3. withdraw [amount]
    - will substract an amount of money from the current balance, or will
    print error message if withdrawn amount exceeds current balance
4. transfer [userToTransfer] [amount]
    - will transfer an amount to a specified user
    - if the amount exceeds the current balance, all balance will be transfered,
    and the rest will be marked as debt to the specified user
5. logout
    - will log out of the current session, blocking all other commands to be used
    until the next login.
      
