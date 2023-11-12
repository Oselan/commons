package com.oselan.commons.lookup;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.oselan.commons.entity.BaseIdEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/***
 * Abstract entity class for all database driven lookups
 * A lookup table should have 5 columns 
 * id,key,value(default),order(to specify special ordering),deprecated(disable use of a lookup value)
 * @author Ahmad Hamid
 *
 */
@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@ToString
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public abstract class BaseLookupEntity extends  BaseIdEntity implements ILookup {
    
    protected String key;
    protected String value;
    @Column(name="\"order\"")
    protected Integer order;
    protected boolean deprecated;
}
