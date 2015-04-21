package com.harrywu.springweb.common.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harrywu.springweb.common.AuditedEntity;
import com.harrywu.springweb.common.impl.CustomEntityImpl;

@MappedSuperclass
@JsonIgnoreProperties(value={"updateBy", "allProperties", "valuedPersistentProperties", "valuedProperties", "valuedTempProperties"}, ignoreUnknown=false)
public class AuditedEntityImpl extends CustomEntityImpl implements AuditedEntity {
//    private static final long serialVersionUID = -7102263430441232518L;
////    protected Users updateBy;
//    protected String updateBy;
//    protected Date updateDate;
//
//    public AuditedEntityImpl() {
//    }
//
//    public AuditedEntityImpl(String updateBy, Date updateDate) {
//        super();
//        this.updateBy = updateBy;
//        this.updateDate = updateDate;
//    }
//
//    @Column(name = "update_by", length = 36)    
//    public String getUpdateBy() {
//        return this.updateBy;
//    }
//
//    public void setUpdateBy(String updateBy) {
//        this.updateBy = updateBy;
//    }
//
///*
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "update_by")
//    public Users getUpdateBy() {
//        return this.updateBy;
//    }
//
//*/
//    public void setUpdateBy(User updateBy) {
//        this.updateBy = updateBy.getUserUid();
//    }
//    
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "update_date", length = 19)
//    public Date getUpdateDate() {
//        return this.updateDate;
//    }
//
//    public void setUpdateDate(Date updateDate) {
//        this.updateDate = updateDate;
//    }
}
