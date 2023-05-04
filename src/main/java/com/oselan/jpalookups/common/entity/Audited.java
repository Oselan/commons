package com.oselan.jpalookups.common.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.Setter;

	/***
	 * injects the audit fields by composition rather than inheritance
	 * @author Ahmad Hamid
	 *
	 */
@AccessType(AccessType.Type.FIELD)
@Embeddable
@Getter
@Setter
public class Audited  
{
    @CreatedBy
    @Column(updatable = false )
    private Long creationUserId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedBy 
    private Long updateUserId;

    @LastModifiedDate
    private LocalDateTime updateDate; 
}
