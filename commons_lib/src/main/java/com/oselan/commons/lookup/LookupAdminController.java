package com.oselan.commons.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.oselan.commons.exceptions.NotFoundException;
import com.oselan.commons.localization.LocalizationDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Lookups Admin API", description = "Lookup lists Management") 
@Validated  
public class LookupAdminController  extends BaseLookupController{
	  
	@Autowired
	private LookupMapper lookupMapper;
	
	@Operation(summary = "Get Lookup Object by type and id")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{type}/{id}")
	public ResponseEntity<LookupLocalizedDTO> getLookupObject(@PathVariable(name = "type") String lookupType,
			@PathVariable(name = "id") Long id) throws NotFoundException {
		ILookupTypeEnum<?> lookupTypeValue = lookupService.getLookupTypeByName(lookupType);
		LookupDTO lookupDTO = lookupService.getLookupDTOById(lookupTypeValue, id);
		LookupLocalizedDTO  localizedDTO = translate(lookupType,List.of(lookupDTO)).get(0);
		return new ResponseEntity<LookupLocalizedDTO>(localizedDTO , HttpStatus.OK);
	}
	
	
	@Operation(summary = "Get Localized Lookup list by types")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{type}/items")
	public ResponseEntity<List<LookupLocalizedDTO>>  getLookups(@PathVariable(name = "type") String lookupType)
			throws NotFoundException  {
	    Map<String, List<? extends LookupDTO>> lookups = lookupService.getAllLookups(new String[]{lookupType});  
	    //translate the returned list  
	    List<LookupLocalizedDTO> lookupsLocalized = translate(lookupType, lookups.get(lookupType));
		return new ResponseEntity<>(lookupsLocalized, HttpStatus.OK);
	}
	 
 
	@Operation(summary = "Save a Lookup list by type")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/{type}")
	public ResponseEntity<List<LookupLocalizedDTO>> saveLookups(@PathVariable(name = "type") String lookupType, 
			@RequestBody List<LookupLocalizedDTO> dtoList )
			throws NotFoundException  {
		ILookupTypeEnum<?> lookupTypeValue = lookupService.getLookupTypeByName(lookupType);
		List<LookupLocalizedDTO> lookupsLocalized = lookupService.saveLookups(lookupTypeValue, dtoList);
		Map<String,List<LocalizationDTO>> keyTransMap = new HashMap<String, List<LocalizationDTO>>();
		dtoList.forEach(ldto-> keyTransMap.put(tolocaleKey(lookupType, ldto.getKey()) , ldto.getValues()));
		localizationService.setMessages(keyTransMap);
		lookupsLocalized = translate(lookupType, lookupsLocalized) ; 
		return new ResponseEntity<List<LookupLocalizedDTO>>(lookupsLocalized, HttpStatus.OK);
	}
	
	
	
	@Operation(summary = "Deprecates a Lookup item by id")
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{type}/{id}")
	public ResponseEntity<Void> deprecateLookup (@PathVariable(name = "type") String lookupType,@PathVariable(name = "id") Long lookupId )
			throws NotFoundException  {
		ILookupTypeEnum<?> lookupTypeValue = lookupService.getLookupTypeByName(lookupType);
		lookupService.deprecateLookup(lookupTypeValue, lookupId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/***
	 * Translates the lookupDTO based on the current context language.
	 * @param dto
	 * @return
	 */ 
	protected List<LookupLocalizedDTO> translate(String typeKey, List<? extends LookupDTO> dtos)
	{ 
		//convert the dtos list to a key/defaultValue map.
		Map<String,String> localeKeyValueMap = dtos.stream() //.map(dto->tolocaleKey(typeKey,dto.getKey()))
				                       .collect(Collectors.toMap(dto->tolocaleKey(typeKey,dto.getKey()),LookupDTO::getValue));
		final Map<String, List<LocalizationDTO>> keyTranslationMap = localizationService.getMessages(localeKeyValueMap); 
		List<LookupLocalizedDTO> localdtoList = dtos.stream().map(dto->
		{   //cast to localizeddto and fetch the localization from the keytranslationMap
			LookupLocalizedDTO ldto = lookupMapper.toLocalizedDTO(dto);
			ldto.setValues(keyTranslationMap.get(tolocaleKey(typeKey,dto.getKey())));
			return ldto;
	    }).collect(Collectors.toList());
		return localdtoList;
	}
}