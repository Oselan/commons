package com.oselan.jpalookups.sample;


import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.oselan.jpalookups.common.lookup.BaseLookupRepository; 
/***
 * Wrapper class to group all definitions of lookup repositories.
 * @author Ahmad Hamid
 *
 */
@Component
public class LookupRepositories {
 
	@Repository
	public interface EntityLKRepository extends BaseLookupRepository<EntityLk> {
 
	}

}
