package com.oselan.jpalookups.common.lookup;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.oselan.jpalookups.common.exceptions.NotFoundException;
import com.oselan.jpalookups.common.translation.ITranslationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
  
public class BaseLookupController {
	
	
	
	@Autowired
	protected   ILookupService lookupService;
	
	@Autowired
	protected ITranslationService translationService;
 
	@Operation(summary = "Get all the lookup types available in the system")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/types")
	@Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Language of user", required = false, examples = {
			@ExampleObject(name = "English", value = "en"),
			@ExampleObject(name = "Arabic", value = "ar") })
	public ResponseEntity<Map<String, String>> getLookupTypes() throws NotFoundException {
		Map<String, String> lookupTypes = lookupService.getLookupTypes();
		//only localize description
		for (Entry<String, String> lookupType : lookupTypes.entrySet()) {
			lookupTypes.replace(lookupType.getKey(), translationService.getMessage(lookupType.getKey()+".description" , lookupType.getValue())) ;
		} 
		return new ResponseEntity<Map<String, String>>(lookupTypes, HttpStatus.OK);
	}

	@Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Language of user", required = false, examples = {
			@ExampleObject(name = "English", value = "en"),
			@ExampleObject(name = "Arabic", value = "ar") })
	@Operation(summary = "Get Lookup list by types")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping( )
	public ResponseEntity<Map<String, List<? extends LookupDTO>>> getPublicLookups(@RequestParam(name = "types") String[] lookupTypeArray)
			throws NotFoundException  {
		Map<String, List<? extends LookupDTO>> lookups = lookupService.getPublicLookups(lookupTypeArray);
		//translate the lookups
		lookups.entrySet().stream().forEach(
				t->translate(t.getKey(),t.getValue().toArray(new LookupDTO[t.getValue().size()])));
		return new ResponseEntity<>(lookups, HttpStatus.OK);
	}
	 
	/***
	 * Translates the lookupDTO based on the current context language.
	 * @param dto
	 * @return
	 */ 
	protected void translate(String typeKey, LookupDTO ... dtos)
	{ 
		Arrays.stream(dtos).filter(dto->dto!=null)
		.forEach(dto->dto.setValue(translationService.getMessage(tolocaleKey(typeKey, dto.getKey()),dto.getValue())));  
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