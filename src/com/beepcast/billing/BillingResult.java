package com.beepcast.billing;

import java.util.Date;

import com.beepcast.billing.profile.BillingCommand;

public class BillingResult {

  private int billingCommand;
  private int paymentResult;
  private String accountId;
  private String accountProfileId;
  private int paymentType;
  private int paymentLevel;
  private Double unit;
  private Double balanceBefore;
  private Double balanceAfter;
  private Date dateResult;

  public BillingResult() {
    dateResult = new Date();
  }

  public int getBillingCommand() {
    return billingCommand;
  }

  public void setBillingCommand( int billingCommand ) {
    this.billingCommand = billingCommand;
  }

  public int getPaymentResult() {
    return paymentResult;
  }

  public void setPaymentResult( int paymentResult ) {
    this.paymentResult = paymentResult;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId( String accountId ) {
    this.accountId = accountId;
  }

  public String getAccountProfileId() {
    return accountProfileId;
  }

  public void setAccountProfileId( String accountProfileId ) {
    this.accountProfileId = accountProfileId;
  }

  public int getPaymentType() {
    return paymentType;
  }

  public void setPaymentType( int paymentType ) {
    this.paymentType = paymentType;
  }

  public int getPaymentLevel() {
    return paymentLevel;
  }

  public void setPaymentLevel( int paymentLevel ) {
    this.paymentLevel = paymentLevel;
  }

  public Double getUnit() {
    return unit;
  }

  public void setUnit( Double unit ) {
    this.unit = unit;
  }

  public Date getDateResult() {
    return dateResult;
  }

  public void setDateResult( Date dateResult ) {
    this.dateResult = dateResult;
  }

  public Double getBalanceBefore() {
    return balanceBefore;
  }

  public void setBalanceBefore( Double balanceBefore ) {
    this.balanceBefore = balanceBefore;
  }

  public Double getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter( Double balanceAfter ) {
    this.balanceAfter = balanceAfter;
  }

  public String toString() {
    final String TAB = " ";
    String retValue = "";
    retValue = "BillingResult ( " + "billingCommand = "
        + BillingCommand.billingCommandToString( billingCommand ) + TAB
        + "paymentResult = "
        + BillingStatus.paymentResultToString( this.paymentResult ) + TAB
        + "balanceBefore = " + this.balanceBefore + TAB + "balanceAfter = "
        + this.balanceAfter + TAB + " )";
    return retValue;
  }

}
