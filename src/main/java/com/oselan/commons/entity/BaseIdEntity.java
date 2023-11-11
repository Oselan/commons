package com.oselan.commons.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.SneakyThrows;
 

@MappedSuperclass 
public abstract class BaseIdEntity  extends BaseEntity implements Cloneable,IdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseIdEntity that = (BaseIdEntity) o;
        return id != null && id.equals(that.getId());
    }
     
    @Override
    public int hashCode() {
    	  if (id==null)
          return super.hashCode();
    	  else 
    	  	return id.intValue();
    }

    @Override
    public String toString() {
        return "BaseIdEntity{" +
                "id=" + id +
                "} " + super.toString();
    }
    
    /***
     * Cloning an entity resets its id to null
     */
    @Override
    @SneakyThrows(CloneNotSupportedException.class)
    public Object clone()
    {
    	BaseIdEntity newObj = (BaseIdEntity) super.clone();
    	newObj.setId(null);
    	return newObj;
    }
}
