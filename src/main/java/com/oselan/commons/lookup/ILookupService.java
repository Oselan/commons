package com.oselan.commons.lookup;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.oselan.commons.exceptions.ConflictException;
import com.oselan.commons.exceptions.NotFoundException; 
public interface ILookupService  {

 
	/**
	 * Add/Updates a lookup dto the lookupType database table
	 * @param lookupType
	 * @param lookupDTO
	 * @return
	 * @throws NotFoundException 
	 * @throws ConflictException 
	 */  
	 <T extends BaseLookupEntity> LookupDTO saveLookup(ILookupTypeEnum<?> lookupType, LookupDTO lookupDTO) throws NotFoundException, ConflictException;

	/***
	 * Returns lookup dto by list type and type key
	 * @param list
	 * @param type
	 * @return lookup dto
	 * @throws NotFoundException if list repository is not defined or type not found
	 */
    LookupDTO getLookupDTOByKey(ILookupTypeEnum<?>  lookupType, String Key) throws NotFoundException;

    /***
	 * Returns lookup dto by list type and type id
	 * @param list
	 * @param type
	 * @return lookup dto
	 * @throws NotFoundException if list repository is not defined or type not found
	 */
    LookupDTO getLookupDTOById(ILookupTypeEnum<?>  lookupType, Long id) throws NotFoundException;

    /***
     * Returns a list of lookup dtos by list type 
     * @param lookupType
     * @return
     * @throws NotFoundException
     */
    List<LookupDTO> getLookupDTOList(ILookupTypeEnum<?>  lookupType) throws NotFoundException;
    
    /***
     * Finds the lookup and returns the id of the key in the specified list.
     * @param list lookup list name
     * @param type
     * @return
     * @throws NotFoundException if list repository is not defined or type not found
     */
    Long getLookupIdByKey(ILookupTypeEnum<?>  lookupType, String key) throws NotFoundException;
    
    /***
     * Finds the ids of a set of lookup types for a specific lookup list.
     * @param list
     * @param key
     * @return Ids in same order as types
     * @throws NotFoundException if the list repository is not defined or atleast one type not found. 
     */
    List<Long> getLookupIdsByKeys(ILookupTypeEnum<?>  lookupType, String...key) throws NotFoundException;

    /***
     * Returns the lookup bean 
     * @param <T>
     * @param list
     * @param key
     * @return
     * @throws NotFoundException
     */
    <T extends ILookup> T getLookupByKey(ILookupTypeEnum<?>  lookupType, String key) throws NotFoundException;

    /***
     * Returns the lookup bean by id
     * @param <T>
     * @param lookupType
     * @param id
     * @return
     * @throws NotFoundException
     */
    <T extends ILookup> T getLookupById(ILookupTypeEnum<?>  lookupType, Long id) throws NotFoundException;
    
    /***
     * Returns the list of lookup beans 
     * @param <T>
     * @param list
     * @return
     * @throws NotFoundException
     */
    <T extends ILookup> List<T> getLookupList(ILookupTypeEnum<?>  lookupType) throws NotFoundException  ;

    
    /***
     * Returns a map of lookupkeys available in the system.
     * @return
     * @throws NotFoundException
     */
    List<LookupTypeDTO> getLookupTypes() throws NotFoundException;

    /***
     * Returns the list of lookup list except for deprecated values
     * @param keyArray
     * @return
     * @throws NotFoundException 
     */
    Map<String, List<? extends LookupDTO>> getPublicLookups(String[] lookupTypeArray, boolean sortAlphabetically) throws NotFoundException;

    /***
     * Returns the list of all lookup lists 
     * @param lookupTypeArray
     * @return
     * @throws NotFoundException
     */
    Map<String, List<? extends LookupDTO>> getAllLookups(String[] lookupTypeArray) throws NotFoundException;
    
    
    <T extends BaseLookupEntity,D extends LookupDTO> List<D> saveLookups(ILookupTypeEnum<?>  lookupType, List<D> lookupDTOs) throws NotFoundException;

  	/***
	 * Deprecates the lookup by id
	 * @param <T>
	 * @param lookupType
	 * @param lookupId
	 * @throws NotFoundException
	 */
	<T extends BaseLookupEntity> void deprecateLookup(ILookupTypeEnum<?> lookupType, Long lookupId) throws NotFoundException;

	<T extends BaseLookupEntity> void deleteLookup(ILookupTypeEnum<?>  lookupType, Long lookupId) throws NotFoundException;

  	<T extends BaseLookupEntity> void deleteAllLookup(ILookupTypeEnum<?>  lookupType ) throws NotFoundException;
	/***
	 * Finds the lookup type by name 
	 * @param name
	 * @return
	 * @throws NotFoundException 
	 */
	ILookupTypeEnum<?> getLookupTypeByName(String name) throws NotFoundException;

	/***
	 * finds the Lookup type by its class references.
	 * @param clazz
	 * @return
	 */
	Optional<ILookupTypeEnum<?>> getLookupTypeByClass(Class<? extends ILookup> clazz);

	/***
	 * Register lookup type enumerations adds the types to the list
	 * @param lookupTypes
	 */
	void registerLookupTypes(ILookupTypeEnum<?>...lookupTypes);
}