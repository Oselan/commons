package com.oselan.commons.search;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/***
 * Simple utility class to help build the search query string with basic equality parameters. 
 * @author Ahmad Hamid
 *
 */ 
@Slf4j
public class SearchQueryBuilder { 
	private static final String CRITERIA_SEPARATOR = LogicOperator.AND.getSymbol();
	private static final String PUNCT_MARK = "\"";
	private static final String CRITERIA_FORMAT = "%1$s" + SearchOperator.EQUALITY.getSymbol() + PUNCT_MARK + "%2$s" + PUNCT_MARK;
	
	 
	/***
	 * builds a querystring from all non-null values in this DTO
	 * @return 
	 */
	public static String buildSearchQuery( Object dto)  
	{ 
		  List<String> params = new ArrayList<String>() ; 
		  Field[] fields = dto.getClass().getDeclaredFields(); 
          for (Field field : fields) { 
          	  if (Modifier.isFinal(field.getModifiers())) continue;
          	  boolean accessFlag = false;
          	  if (!field.canAccess(dto))
          	  {  
          	  	 field.setAccessible(true);
          	  	 accessFlag =true;
          	  }
          	  Object value;
    					try{
    						value = field.get(dto);
    						if (value != null && value.getClass() != String.class) {
    							value = DateTimeConverter.mapObjectToString(value);
    						}
    						if (value!= null)
    	      	     			params.add(String.format(CRITERIA_FORMAT, field.getName(),value));
    					}catch (IllegalArgumentException | IllegalAccessException e){
    						//ignore this is an internal function
    						log.error("Error occured building search query on field " + field.getName() , e);
    					}
    					if (accessFlag)
    						field.setAccessible(false);
          }
          return String.join(CRITERIA_SEPARATOR, params);
	}
	
	

}
