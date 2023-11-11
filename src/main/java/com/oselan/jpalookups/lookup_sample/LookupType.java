package com.oselan.jpalookups.lookup_sample;

import com.oselan.commons.lookup.ILookup;
import com.oselan.commons.lookup.ILookupTypeEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
/***
 * Enum for all application lookup types
 * @author Ahmad Hamid
 *
 */
@Getter
@RequiredArgsConstructor
public  enum LookupType implements ILookupTypeEnum<LookupType> { 
	        EntityLK("Sample db lookup", EntityLk.class),
	        EnumLK("Sample enum lookup", EnumLk.class);
	          
	        private final String description;
	        private final Class<? extends ILookup> lookupClass;
 
}