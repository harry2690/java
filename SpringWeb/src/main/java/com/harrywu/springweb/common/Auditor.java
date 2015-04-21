package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.Date;

public interface Auditor<Ent extends Entity, ID extends Serializable, ACTION extends Enum<ACTION>> extends CommonController<Ent, ID> {
    
    public CustomResult<Ent> audit(Entity data, Date performOn, Entity operator) throws Exception;
    //�auditExport撌脣��剁���誑雿輻auditLogExport
	public CustomResult<Ent> auditLogExport(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> view(Entity data, Date performOn, Entity operator) throws Exception;

    public CustomResult<Ent> auditInsert(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditUpdate(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditDelete(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditExport(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditImport(Entity data, Date performOn, Entity operator) throws Exception;

    public CustomResult<Ent> auditPrint(Entity data, Date performOn, Entity operator) throws Exception;

	public CustomResult<Ent> auditLogin(Entity data, Date performOn, Entity operator) throws Exception;
    public CustomResult<Ent> auditLoginFail(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditLogout(Entity data, Date performOn, Entity operator) throws Exception;
    
    public CustomResult<Ent> auditChangePassword(Entity data, Date performOn, Entity operator) throws Exception;
    public CustomResult<Ent> auditDeleteUser(Entity data, Date performOn, Entity operator) throws Exception;
    public CustomResult<Ent> auditUpdateUser(Entity data, Date performOn, Entity operator) throws Exception;
    public CustomResult<Ent> auditCreateUser(Entity data, Date performOn, Entity operator) throws Exception;

    public CustomResult<Ent> auditSysLog(Entity data, Date performOn, Entity operator) throws Exception;
    public CustomResult<Ent> auditSysLogExport(Entity data, Date performOn, Entity operator) throws Exception;
}
