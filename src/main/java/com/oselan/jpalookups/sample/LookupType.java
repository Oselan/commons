package com.oselan.jpalookups.sample;

import com.oselan.jpalookups.common.lookup.ILookup;
import com.oselan.jpalookups.common.lookup.ILookupEnum;
import com.oselan.jpalookups.common.lookup.ILookupTypeEnum;

import lombok.Getter;
/***
 * Enum for all application lookup types
 * @author Ahmad Hamid
 *
 */
@Getter
public  enum LookupType implements ILookupTypeEnum<LookupType> { 
	        EntityLK("Sample db lookup", EntityLk.class),
	        EnumLK("Sample enum lookup", EnumLk.class);
	         
			private boolean isEnum = false;
	        private String description;
	        private  Class<? extends ILookup> lookupClass;

	        private LookupType(String description, Class<? extends ILookup> lookupClass) {
	        	 this.description = description; 
	        	 this.lookupClass = lookupClass;
	        	 if (lookupClass.isEnum() && ILookupEnum.class.isAssignableFrom(lookupClass) )
	        		 isEnum = true;
	        } 
}