package com.oselan.commons.translation;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.oselan.commons.entity.BaseEntity;

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
@IdClass(TranslationKey.class)
public class Translation extends BaseEntity implements Serializable{
 
  /**
	 * 
	 */
  private static final long serialVersionUID = 8812785524623835883L;

  
  @Id 
  private String key;
   @Id 
  private String locale; 
   
  private String value ; 
  
 
  
  
}
