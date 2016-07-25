package com.beepcast.billing.persistence;

public class ClientAccountEntity {

  private int id;
  private String accountId;
  private String accountProf;
  private double balance;

  public ClientAccountEntity() {
  }

  public int getId() {
    return id;
  }

  public void setId( int id ) {
    this.id = id;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId( String accountId ) {
    this.accountId = accountId;
  }

  public String getAccountProf() {
    return accountProf;
  }

  public void setAccountProf( String accountProf ) {
    this.accountProf = accountProf;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance( double balance ) {
    this.balance = balance;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "AccountEntity ( " + "id = " + this.id + TAB + "accountId = "
        + this.accountId + TAB + "accountProf = " + this.accountProf + TAB
        + "balance = " + this.balance + TAB + " )";
    return retValue;
  }

}
