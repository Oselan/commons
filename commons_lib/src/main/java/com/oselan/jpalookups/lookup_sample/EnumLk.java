package com.oselan.jpalookups.lookup_sample;

import com.oselan.commons.lookup.ILookupEnum;

import lombok.Getter; 
/***
 *  A sample enum based lookup 
 */
@Getter
public enum EnumLk  implements ILookupEnum<EnumLk>  {
	KEY1("Value1"), 
	KEY2("Value2"), 
	KEY3("Value3") 
	;

	private String value;

	private EnumLk(String value) {
		this.value = value;
	}
    
}