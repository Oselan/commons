package com.oselan.jpalookups.common.entity;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/**
 *  Audited entity injected into extension of baseEntity
 */
@MappedSuperclass 
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntityAudited  extends BaseEntity {

    
    @Embedded
    Audited audit = new Audited();

    
}
