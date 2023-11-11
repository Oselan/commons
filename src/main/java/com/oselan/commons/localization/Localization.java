package com.oselan.commons.localization;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.IdClass;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.oselan.commons.entity.BaseIdEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter 
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@IdClass(LocalizationKey.class)
public class Localization extends BaseIdEntity implements Serializable{
 
  /**
	 * 
	 */
  private static final long serialVersionUID = 8812785524623835883L;

  
   
  private String key; 
  private String locale; 
   
  private String value ; 
  
 
  
  
}
