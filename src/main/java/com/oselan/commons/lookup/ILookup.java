package com.oselan.commons.lookup;

/**
 * Interface of all lookups
 * @author Ahmad Hamid
 *
 */
public interface ILookup {
	Long getId() ; 
	
	String getKey();

	String getValue();

	Integer getOrder();

	boolean isDeprecated();  
	 
}
