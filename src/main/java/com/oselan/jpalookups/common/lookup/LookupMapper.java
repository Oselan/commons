package com.oselan.jpalookups.common.lookup;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

import com.oselan.jpalookups.common.exceptions.NotFoundException;

import lombok.SneakyThrows;

 
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class LookupMapper {
 
 
	@PersistenceContext
	private EntityManager entityManager;
	  
	public <T extends BaseLookupEntity> T lookupProxyTolookupEntity(HibernateProxy sourceProxy, @TargetType Class<T> entityClass)   {
		return null;
	}
	
	public abstract List<LookupDTO> toDTOList(List<? extends ILookup> lookupList);
  
	@Mapping(target = "values", ignore = true)
	public abstract LookupLocalizedDTO toLocalizedDTO( LookupDTO lookup );
	
	
	/***
	 * Maps a Lookup enum or a database driven lookup to LookupDTO 
	 * with auto translation of the value
	 * If the lookup is an uninitialized hibernate proxy, it will load from the cache 
	 * @param lookup
	 * @return
	 * @throws NotFoundException 
	 */ 
    public LookupDTO toDTO(ILookup lookup)  
    {
		   if ( lookup == null ) return null;
		   LookupDTO lookupDTO =null; 
		   if (lookup instanceof ILookupEnum )
		   {
			   lookupDTO = LookupDTO.builder().key(lookup.getKey())
					   .value(lookup.getValue()).build(); 
		   }
		   else if (lookup != null && lookup instanceof HibernateProxy  && ((HibernateProxy)lookup).getHibernateLazyInitializer().isUninitialized())
		   {
				   //load the lookupdto from the service
				   try {
					   LazyInitializer initializer = ((HibernateProxy)lookup).getHibernateLazyInitializer() ;  
					   @SuppressWarnings("unchecked")
					   Class<? extends ILookup> entityClass = initializer.getPersistentClass();
					   lookup =  entityManager.find( entityClass, lookup.getId() );
//					   ILookupTypeEnum<?> lookupType = getLookupTypeByClass(  entityClass).orElseThrow(()-> new NotFoundException("Unexpected class for a lookup type."));
//					   lookupDTO = getLookupDTOById(lookupType, lookup.getId());//this will get from cache
				   }catch(Exception e)
				   {   //this shouldn't occur because we are loading an proxy based entity
					   throw new IllegalArgumentException("Unexpected parameter for loading uninitialized id",e);
				   }
		   }
		   
		   if (lookupDTO==null && lookup !=null) 
		   {    //its a fully loaded lookup no need to worry about it 
			    lookupDTO = new LookupDTO();
			    lookupDTO.setId( lookup.getId() );
		        lookupDTO.setKey( lookup.getKey() );
		        lookupDTO.setDeprecated( lookup.isDeprecated() );
		        lookupDTO.setOrder( lookup.getOrder() ); 
		        lookupDTO.setValue( lookup.getValue()  );
		   }
	       return lookupDTO; 
    }
    
	  
	/***
	 * Maps a DTO to an enum of type ILookupEnum
	 * @param <X>
	 * @param <T>
	 * @param sourceDto
	 * @param enumClass
	 * @return
	 */
	public <X extends LookupSimpleDTO,T extends Enum<T> & ILookupEnum<T>> T toEnum(X sourceDto,@TargetType Class<T> enumClass)
	{
		return  sourceDto !=null && sourceDto.getKey()!=null ?  Enum.valueOf(enumClass, sourceDto.getKey()) : null;
	}
  
   
	@SneakyThrows(IllegalAccessException.class)
    public <X extends LookupSimpleDTO,T extends BaseLookupEntity> T toEntity(X sourceDto, @TargetType Class<T> entityClass)   { 
    	if (sourceDto == null) return null;
    	T entity;
    	if (sourceDto.getId()!=null)
    	{
    		entity =  entityManager.find( entityClass, sourceDto.getId() );
    		if (sourceDto instanceof LookupDTO)
    			dtoToEntity((LookupDTO)sourceDto,entity);
    	}
    	else 
    	{
    		try {//used when creating new dto entities
			    entity  = entityClass.getDeclaredConstructor().newInstance();
			    dtoToEntity((LookupDTO)sourceDto,entity);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IllegalAccessException("Failed to instantiate entity {" + entityClass.getName() + "}") ; 
			}
    	} 
    	return entity;
    } 
    
    @Mapping(target = "id",ignore =true)
	public abstract void dtoToEntity(LookupDTO lookupDTO, @MappingTarget BaseLookupEntity entity);
}
