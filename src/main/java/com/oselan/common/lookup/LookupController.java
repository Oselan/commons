package com.oselan.common.lookup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.oselan.common.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lookups", description = "Lookup list types and lookup lists") 
@RestController
@RequestMapping("/v1/lookups")
@Validated  
public class LookupController extends BaseLookupController{
	
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
	 
}