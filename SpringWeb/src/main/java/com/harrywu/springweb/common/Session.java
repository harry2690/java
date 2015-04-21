package com.harrywu.springweb.common;

import java.util.Calendar;
import java.util.Date;

import com.harrywu.springweb.common.impl.EntityImpl;

public class Session extends EntityImpl {
    private static final long serialVersionUID = -343776311775514238L;
    
    public static final String OPERATOR_ID = "usrOid";
    public static final String USER_NAME = "usrName";
    public static final String USER_ID = "usrId";
    public static final String LOGIN_IP = "loginIp";
    public static final String LOGIN_DATE_TIME = "loginDateTime";
    public static final String LOGIN_FAIL_DATE_TIME = "loginFailDateTime";
    public static final String LOGIN_FAIL_COUNT = "loginFailedCount";
    
    private String sessionId;
    
    private String performerIp;
    
    private Entity operator;
    
    private Date createOn;
    
    public Session(Entity operator) {
//        this.sessionId = (String) UUIDHexGenerator.getOID(null);
        this.operator = operator;
        this.createOn = Calendar.getInstance().getTime();
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setOperator(Entity operator) {
        this.operator = operator;
    }

    public Entity getOperator() {
        return operator;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setPerformerIp(String ip) {
        this.performerIp = ip;
    }

    public String getPerformerIp() {
        return performerIp;
    }
}
