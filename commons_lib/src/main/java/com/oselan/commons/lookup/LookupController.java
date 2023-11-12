package com.oselan.commons.lookup;

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

import com.oselan.commons.exceptions.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lookups API", description = "Lookup list types and lookup lists")   
@Validated  
public class LookupController extends BaseLookupController{
	
	@Operation(summary = "Get all the lookup types available in the system")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/types")
	@Parameter(name = "Accept-Language", in = ParameterIn.HEADER,  description = "Language of user", required = false, examples = {
			@ExampleObject(name = "English", value = "en"),
			@ExampleObject(name = "Arabic", value = "ar") })
	public List<LookupTypeDTO> getLookupTypes() throws NotFoundException {
	    List<LookupTypeDTO> lookupTypes = lookupService.getLookupTypes();
		//only localize description
		for (LookupTypeDTO lookupType : lookupTypes ) {
			lookupType.setDescription( localizationService.getMessage(lookupType.getName() +".description" , lookupType.getDescription())) ;
		} 
		return  lookupTypes ;
	}

	@Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Language of user", required = false, examples = {
			@ExampleObject(name = "English", value = "en"),
			@ExampleObject(name = "Arabic", value = "ar") })
	@Operation(summary = "Get Lookup list by types")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping( )
	public ResponseEntity<Map<String, List<? extends LookupDTO>>> getPublicLookups(@RequestParam(name = "types") String[] lookupTypeArray)
			throws NotFoundException  {
		Map<String, List<? extends LookupDTO>> lookups = lookupService.getPublicLookups(lookupTypeArray,true);
		//translate the lookups
		lookups.entrySet().stream().forEach(
				t->translate(t.getKey(),t.getValue().toArray(new LookupDTO[t.getValue().size()])));
		return new ResponseEntity<>(lookups, HttpStatus.OK);
	}
	 
}