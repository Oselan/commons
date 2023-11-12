package com.oselan.commons.search;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/***
 * Helper class to handle date time conversion . should be replaced with ObjectMapper
 * @author Ahmad Hamid
 *
 */
public class DateTimeConverter {

	public final static String localDateTimeFormat[] = new String[]{"d/M/yyyy HH:mm:ss" ,"yyyy-MM-dd HH:mm:ss"};

	public final static String localDateFormat[] = new String[]{"d/M/yyyy","yyyy-MM-dd"};

	public final static String localTimeFormat[] = new String[]{"HH:mm:ss"};
	
	

	static Object mapStringToDate(Class<? extends Object> javaType, Object value) {
		Object obj = null;
		int i=0;
		if(value != null){
			if(LocalDate.class == javaType){
			    while(i<localDateFormat.length && obj==null) 
			      try {
			        obj = LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern(localDateFormat[i++]));
			      }catch(DateTimeParseException e) { 
			        //ignore
			      }
			}else if(LocalDateTime.class == javaType){
			  while(i<localDateFormat.length && obj==null) 
                try {
                  obj = LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern(localDateTimeFormat[i++]));
                }catch(DateTimeParseException e) { 
                  //ignore
                }
			}else if(LocalTime.class == javaType){
			  while(i<localDateFormat.length && obj==null) 
                try {
                    obj = LocalTime.parse(value.toString(), DateTimeFormatter.ofPattern(localTimeFormat[i++]));
    			 }catch(DateTimeParseException e) { 
                   //ignore
                 }
			}else
				obj = value;
			
		}
		return obj;
	}

	static String mapObjectToString(Object value) {
		String str = null;
		if(value != null){
			if(LocalDate.class == value.getClass()){
				str = ((LocalDate) value).format(DateTimeFormatter.ofPattern(localDateFormat[0]));
			}else if(LocalDateTime.class == value.getClass()){
				str = ((LocalDate) value).format(DateTimeFormatter.ofPattern(localDateFormat[0]));
			}else if(LocalTime.class == value.getClass()){
				str = ((LocalDate) value).format(DateTimeFormatter.ofPattern(localDateFormat[0]));
			}else
				str = value.toString();
		}
		return str;
	}
}
