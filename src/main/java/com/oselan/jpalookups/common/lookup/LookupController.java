package com.oselan.jpalookups.common.lookup;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Lookups", description = "Lookup lists") 
@RestController
@RequestMapping("/v1/lookups")
@Validated  
public class LookupController extends BaseLookupController{
	
	
	
	 
}