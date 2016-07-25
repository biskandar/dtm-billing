package com.beepcast.billing.persistence;

import com.firsthop.common.log.DLog;
import com.firsthop.common.log.DLogContext;
import com.firsthop.common.log.SimpleContext;

public class ClientAccountService {

  static final DLogContext lctx = new SimpleContext( "ClientAccountService" );

  private ClientAccountDAO dao;

  public ClientAccountService() {
    dao = new ClientAccountDAO();
  }

  public ClientAccountEntity queryEntity( String accountId ) {
    ClientAccountEntity entity = null;

    if ( ( accountId == null ) || ( accountId.equals( "" ) ) ) {
      DLog.warning( lctx , "Failed to perform query entity "
          + ", found null accountId" );
      return entity;
    }

    entity = dao.queryEntity( accountId );

    return entity;
  }

  public boolean insertNewEntity( ClientAccountEntity entity ) {
    boolean result = false;

    if ( entity == null ) {
      DLog.warning( lctx , "Failed to perform insert new entity "
          + ", found null entity" );
      return result;
    }

    if ( entity.getAccountId() == null ) {
      DLog.warning( lctx , "Failed to perform insert new entity "
          + ", found null account id" );
      return result;
    }

    if ( entity.getAccountProf() == null ) {
      DLog.warning( lctx , "Failed to perform insert new entity "
          + ", found null account profile" );
      return result;
    }

    result = dao.insertNewEntity( entity );

    return result;
  }

  public boolean updateEntityBalance( int clientAccountId , double balance ) {
    boolean result = false;

    if ( clientAccountId < 1 ) {
      DLog.warning( lctx , "Failed to perform update entity balance"
          + ", found null clientAccountId" );
      return result;
    }

    result = dao.updateEntityBalance( clientAccountId , balance );

    return result;
  }

}
