package com.oselan.jpalookups.lookup_sample;

import org.springframework.context.annotation.Configuration;

import com.oselan.commons.lookup.ILookupService;

 
@Configuration
public class LookupTypeConfig     {
  
	public LookupTypeConfig(ILookupService service) { 
		service.registerLookupTypes(LookupType.values());
	}
	 
}
