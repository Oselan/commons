package com.oselan.common.lookup;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.oselan.common.entity.BaseIdEntity;

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
public abstract class BaseLookupEntity extends  BaseIdEntity implements ILookup {
    
    private String key;
    private String value;
    @Column(name="\"order\"")
    private Integer order;
    private boolean deprecated;
}
