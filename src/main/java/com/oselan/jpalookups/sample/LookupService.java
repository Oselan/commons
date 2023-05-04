package com.oselan.jpalookups.sample;

import com.oselan.jpalookups.common.lookup.BaseLookupService;

 
//@Service 
//@Primary
public class LookupService extends BaseLookupService   {

	 
	public LookupService() {
		super(); 
		registerLookupType(LookupType.values());
	}
	 
}
