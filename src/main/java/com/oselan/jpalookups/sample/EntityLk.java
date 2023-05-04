package com.oselan.jpalookups.sample;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.oselan.jpalookups.common.lookup.BaseLookupEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***
 *  Sample database driven lookup table.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Builder
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EntityLk extends BaseLookupEntity {
 
}
