package com.oselan.commons.lookup;

/**
 * Interface of enums of all lookup lists    
 * @author Ahmad Hamid
 *
 */
public  interface ILookupTypeEnum<T extends Enum<T>>    {
   
	public String getDescription()  ;
	
	public String name();
	
	
	/**
	 * Whether this lookup type is to be built from an enum extending ILookupEnum
	 * or false if it is database driven lookup
	 * @return
	 */
	public default boolean isEnum()
	{
		 return ILookupEnum.class.isAssignableFrom(getLookupClass());
	}
	
	/***
	 * The lookup enumeration class which is an enum that extends IlookupEnum
	 * @return
	 */ 
	public  Class<? extends ILookup> getLookupClass();
}
