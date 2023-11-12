package com.oselan.commons.lookup;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.oselan.commons.localization.ILocalizationService;
  
public class BaseLookupController {
	
	
	
	@Autowired
	protected ILookupService lookupService;
	
	@Autowired
	protected ILocalizationService localizationService;
  
	/***
	 * Translates the lookupDTO based on the current context language.
	 * @param dto
	 * @return
	 */ 
	protected void translate(String typeKey, LookupDTO ... dtos)
	{ 
		Arrays.stream(dtos).filter(dto->dto!=null)
		.forEach(dto->dto.setValue(localizationService.getMessage(tolocaleKey(typeKey, dto.getKey()),dto.getValue())));  
	}
	
	/**
	 * helper to control local key building from lookup key
	 * @param typeKey
	 * @param lookupKey
	 */
	protected String tolocaleKey(String typeKey,String lookupKey)
	{
		return typeKey +"."+ lookupKey;
	}
}