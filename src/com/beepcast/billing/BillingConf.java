package com.beepcast.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class BillingConf {

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constanta
  //
  // ////////////////////////////////////////////////////////////////////////////

  static final DLogContext lctx = new SimpleContext( "BillingConf" );

  public static final String FIELD_SEPARATOR = "_";

  public static final String ACCOUNT_STORAGE_ID = "id";
  public static final String ACCOUNT_STORAGE_MEDIA = "media";

  public static final String ACCOUNT_PROFILE_ID = "id";
  public static final String ACCOUNT_PROFILE_LEVEL = "level";
  public static final String ACCOUNT_PROFILE_PAYMENTTYPE = "paymentType";
  public static final String ACCOUNT_PROFILE_MAPKEYPREFIX = "mapKeyPrefix";
  public static final String ACCOUNT_PROFILE_STORAGEID = "storageId";
  public static final String ACCOUNT_PROFILE_LOWESTUNIT = "lowestUnit";
  public static final String ACCOUNT_PROFILE_ROUNDDECIMALPLACES = "roundDecimalPlaces";

  public static final String MANAGEMENT_TASK_ID = "id";
  public static final String MANAGEMENT_TASK_SYNCH = "synch";
  public static final String MANAGEMENT_TASK_SYNCH_INTERVAL = "interval";
  public static final String MANAGEMENT_TASK_SWEEP = "sweep";
  public static final String MANAGEMENT_TASK_SWEEP_INTERVAL = "interval";
  public static final String MANAGEMENT_TASK_SWEEP_EXPIRY = "expiry";

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Data Member
  //
  // ////////////////////////////////////////////////////////////////////////////

  private boolean debug;

  private Map accountStorages;
  private List accountStorageIds;
  private Map accountProfiles;
  private List accountProfileIds;
  private Map managementTasks;
  private List managementTaskIds;
  private BillingLogWorkerConf billingLogWorkerConf;

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  // ////////////////////////////////////////////////////////////////////////////

  public BillingConf() {
    init();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Support Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void init() {
    accountStorages = new HashMap();
    accountStorageIds = new ArrayList();
    accountProfiles = new HashMap();
    accountProfileIds = new ArrayList();
    managementTasks = new HashMap();
    managementTaskIds = new ArrayList();
    billingLogWorkerConf = new BillingLogWorkerConf();
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Get Function
  //
  // ////////////////////////////////////////////////////////////////////////////

  public void setDebug( boolean debug ) {
    this.debug = debug;
  }

  public boolean isDebug() {
    return debug;
  }

  public Map getAccountStorages() {
    return accountStorages;
  }

  public List getAccountStorageIds() {
    return accountStorageIds;
  }

  public Map getAccountProfiles() {
    return accountProfiles;
  }

  public List getAccountProfileIds() {
    return accountProfileIds;
  }

  public Map getManagementTasks() {
    return managementTasks;
  }

  public List getManagementTaskIds() {
    return managementTaskIds;
  }

  public BillingLogWorkerConf getBillingLogWorkerConf() {
    return billingLogWorkerConf;
  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Inner Class
  //
  // ////////////////////////////////////////////////////////////////////////////

  public class InternalModuleConf {

    private int queueSize;
    private long queueTimeout;
    private int workerSize;
    private long workerLatency;

    public InternalModuleConf() {
    }

    public int getQueueSize() {
      return queueSize;
    }

    public void setQueueSize( int queueSize ) {
      this.queueSize = queueSize;
    }

    public long getQueueTimeout() {
      return queueTimeout;
    }

    public void setQueueTimeout( long queueTimeout ) {
      this.queueTimeout = queueTimeout;
    }

    public int getWorkerSize() {
      return workerSize;
    }

    public void setWorkerSize( int workerSize ) {
      this.workerSize = workerSize;
    }

    public long getWorkerLatency() {
      return workerLatency;
    }

    public void setWorkerLatency( long workerLatency ) {
      this.workerLatency = workerLatency;
    }

  }

  public class BillingLogWorkerConf extends InternalModuleConf {

    public BillingLogWorkerConf() {
      super();
    }

  }

  // ////////////////////////////////////////////////////////////////////////////
  //
  // Helper
  //
  // ////////////////////////////////////////////////////////////////////////////

  public String toString() {
    final String TAB = "\n";
    String retValue = "";
    retValue = "BillingConf (" + TAB + "accountStorages = "
        + this.accountStorages + TAB + "accountStorageIds = "
        + this.accountStorageIds + TAB + "accountProfiles = "
        + this.accountProfiles + TAB + "accountProfileIds = "
        + this.accountProfileIds + TAB + "managementTasks = "
        + this.managementTasks + TAB + "managementTaskIds = "
        + this.managementTaskIds + TAB + ")";
    return retValue;
  }

}
