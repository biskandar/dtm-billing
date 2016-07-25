# dtm-billing
Directtomobile Billing Module

v1.1.09

- There is a change inside file ./conf/billing.xml

      <accountProfile id="client_prepaid"
        level="client"
        paymentType="prepaid"
        mapKeyPrefix="CPE"
        storageId="mem1"
        lowestUnit="-1"
        roundDecimalPlaces="2" />

- Add configuration to round up the decimal places

- Add new library :
  . 

v1.1.08

- This library is used also for other application like p2p , that allow to 
  deduct balance until negatif value 
  
- There is a change inside ./conf/billing.xml

    add attribute minUnit in the account profile
  
- Add new library :
  . ...

v1.1.07

- Use buffer to insert "client_trans_unit" for every transaction for performance wise

- Add new library :
  . beepcast_dbmanager-v1.1.31.jar

v1.1.06

- Exclude the event or client library , 
  so that this library can be used for other third party .
  
- Add new library :
  . beepcast_database-v1.1.05.jar

v1.1.05

- Add function to clear account postpaid account
  command named RESET

- Add debug property in the configuration billing.xml
  
- Add new library :
  . beepcast_dbmanager-v1.1.26.jar

v1.1.04

- need to be able to customize the following credit alert, 
  so that for partner customers, the partners details are provided, and not BeepCast?'s.
  NOTES: For starters we should change "$10.0" to "10 credits"

- add new library :
  . beepcast_dbmanager-v1.1.22.jar
  
v1.1.03

- Support to add credit with negatif value .

v1.1.01

- Put billing history in the payment result .

- Remove presistent log info , just put the direct informational content .

- Only doCredit of prepaid and doDebit for postpaid ,
  will perform automatically persist .
  
- Reorganized client tracking system to support :
  . daily_client_track 
  . monthly_client_track 

- Create logic to execute management task (s) : sweep and synch

- Create persistence api to support persistence account

- Add persistence table for every account

  CREATE TABLE `client_account` (
    `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    `account_id` VARCHAR(20) NOT NULL,
    `account_prof` VARCHAR(45) NOT NULL,
    `balance` DOUBLE NOT NULL DEFAULT 0,
    `date_inserted` DATETIME NOT NULL,
    `date_updated` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `account_id_account_prof`(`account_id`, `account_prof`)
  )
  ENGINE = InnoDB;

- Add account clonenable

- Add account in the BillingResult object

- Alter table transaction ( client_trans_unit ) , add field :
  . account_id
  . account_prof
  . balance
  
  Execute sql below :
  
  ALTER TABLE `client_trans_unit` 
    ADD COLUMN `account_id` VARCHAR(20) AFTER `id` ,
    ADD COLUMN `account_prof` VARCHAR(45) AFTER `account_id` ,
    ADD COLUMN `balance` DOUBLE NOT NULL DEFAULT 0 AFTER `unit` ;

- Add balance threshold to trigger NOT_ENOUGH_BALANCE code
- Extend the BasicBillingEngine into PersistenceBillingEngine

v1.1.00

- Redesign billing API to support tracking management , must implement plug and play the components
- Create a simple management for client / event billing mechanism .
- Inside BillingApp there is BasicBillingEngine
- Inside BillingEngine must have rwLock mechanism for queryAccount and storeAccount

v1.0.03

- failed to resolve return when perform ing debit unit for unbalanced client 

    20.05.2009 15.18.11:935 194 DEBUG   {main}ClientsAccount [1] Found first client holder in the memory , try to synch with db
    20.05.2009 15.18.11:935 195 DEBUG   {main}ClientsAccount Found client with accountType value = 1
    20.05.2009 15.18.11:935 196 DEBUG   {main}AccountsBasic Added to the map for new AccountHolder ( id = 1 accountType = 1 debitUnit = null balanceUnit = 0.0  )
    20.05.2009 15.18.11:935 197 WARNING {main}ClientsAccount [1] Failed to do debit , found low balance , current balanceUnit = 0.0
    20.05.2009 15.18.11:935 198 DEBUG   {main}Transaction [INT56000] Successfully perform debit for clientId = 1 , with debitUnit = 1.0

  . the solution are :
    * fixed the update unit at client / event level  
    
v1.0.02

- fixed bugs :

    java.lang.NoSuchMethodError: com.beepcast.dbmanager.Fetch.execute(Lcom/beepcast/dbmanager/Record;Z)Lcom/beepcast/dbmanager/Record;
    	at com.beepcast.billing.acc.ClientsAccount.getAccountType(ClientsAccount.java:147)
    	at com.beepcast.billing.acc.ClientsAccount.addNewClientAccount(ClientsAccount.java:333)
    	at com.beepcast.billing.acc.ClientsAccount.getClientHolder(ClientsAccount.java:169)
    	at com.beepcast.billing.acc.ClientsAccount.getClientUnit(ClientsAccount.java:185)
    	at com.beepcast.billing.engine.BillingApiImpl.getClientUnit(BillingApiImpl.java:80)
    	at com.beepcast.billing.BillingApp.getPostpaidClientDebitUnit(BillingApp.java:135)

    java.lang.NoSuchMethodError: com.beepcast.dbmanager.Fetch.execute(Lcom/beepcast/dbmanager/RecordResult;Z)Lcom/beepcast/dbmanager/RecordResult;
    	at com.beepcast.billing.acc.ClientsAccount.getAccountType(ClientsAccount.java:148)
    	at com.beepcast.billing.engine.BillingApiImpl.doClientDebit(BillingApiImpl.java:153)
    	at com.beepcast.billing.BillingApp.doPrepaidClientDebit(BillingApp.java:182)
    	at com.beepcast.model.event.EventSupport.doDebitPayment(EventSupport.java:1533)
    	at com.beepcast.model.transaction.Transaction.getResponse(Transaction.java:477)
    	at com.beepcast.router.RouterMOWorker.processMoRecord(RouterMOWorker.java:308)
    	at com.beepcast.router.RouterMOWorker.access$10(RouterMOWorker.java:263)
    	at com.beepcast.router.RouterMOWorker$RouterMOWorkerThread.run(RouterMOWorker.java:453)
    	
  . fixed in the class ClientsAccount.getAccountType

v1.0.01


- add feature to clean the accountHolder in the map after expiry period , for memory efficient
- fixed the concurrent issue , rebuild all the framework and start from the beginning again

v1.0.00

- add feature as billing system , with list of api :
  . doPrepaidClientCredit = add a unit credit to particular prepaid client
  . doPrepaidEventCredit  = add a unit credit to particular prepaid event
  . getPrepaidClientBalance = get current balance from prepaid client
  . getPostpaidClientDebitUnit = get current debit unit from postpaid client
  . doPrepaidClientDebit = sub a unit from particular prepaid client
  . doPrepaidEventDebit = sub a unit from particular prepaid event
  . doPrepaidReservedBalance = client balance move to event balance
  . doPrepaidUnreservedBalance = event balance move to client balance
  
- create configuration for thread to synch the balance
  with example property below :
    
    billing.config.file=${beepadmin.conf}/billing.xml
