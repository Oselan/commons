package com.oselan.commons.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * Audited entity injected into Id Entity
 * @author Ahmad Hamid
 *
 */
@MappedSuperclass  
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseIdEntityAudited  extends BaseIdEntity {

    @Embedded
    Audited audit = new Audited();
 
}
