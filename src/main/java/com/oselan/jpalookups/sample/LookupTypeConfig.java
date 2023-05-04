package com.oselan.jpalookups.sample;

import org.springframework.context.annotation.Configuration;

import com.oselan.jpalookups.common.lookup.ILookupService;

 
@Configuration
public class LookupTypeConfig     {
 
	
	public LookupTypeConfig(ILookupService service) { 
		service.registerLookupType(LookupType.values());
	}
	 
}
