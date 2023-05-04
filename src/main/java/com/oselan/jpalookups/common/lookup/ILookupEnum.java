package com.oselan.jpalookups.common.lookup;

/**
 * Interface for Enumerated Lookups that provides default implementation 
 * for some of unneeded lookup properties 
 * @author Ahmad Hamid 
 */
public  interface ILookupEnum<T extends Enum<T>> extends ILookup {

	

	String getValue();
 
	/*Shadow of the enum ordinal & name method. */
	int ordinal();
	String name();

	default Integer getOrder()
	{
		return ordinal() ;
	}
	
	default boolean isDeprecated() {
		return false;
	}
	
	default Long getId() {
		return Long.valueOf(getOrder());
	}
	
	default String getKey()
	{
		return name();
	}
 
}
