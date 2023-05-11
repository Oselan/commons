package com.oselan.commons.lookup;

 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.oselan.commons.exceptions.ConflictException;
import com.oselan.commons.exceptions.NotFoundException;

import lombok.extern.slf4j.Slf4j; 

/***
 * A generic implementation  that handles both Enum(Readonly lookups) and Database lookups 
 * LookupDTOs are translated before returned
 * Lookup lists are cached on startup at ApplicationReadyEvent
 * A db Lookup entity should extend BaseLookup, 
 * A db Lookup repository should extend LookupRepository
 * Override with @Service Anotation and register lookupType enums in constructor. 
 * @author Ahmad Hamid
 *
 */ 
@Slf4j
@Service
public class LookupService implements ILookupService {

	/**
	 * Autoinject all jpa lookup repositories
	 * 
	 */
	@Autowired
	protected Map<String, BaseLookupRepository<? extends BaseLookupEntity>> lkRepositories;
	 
	 
	@Autowired
	protected LookupMapper lookupMapper;

	@Value( "${app.lookups.auto-create}" )
	private Boolean autoCreate;
  
	protected ConcurrentHashMap<ILookupTypeEnum<?>, BaseLookupRepository<? extends BaseLookupEntity>> repoMap = new ConcurrentHashMap<>();
	
	protected List<ILookupTypeEnum<?>> lookupEnums = new ArrayList<ILookupTypeEnum<?>>(); 
	
	
	public LookupService() {
		super();  
	}
	
	/***
	 * Register lookup type enumerations adds the types to the list
	 * @param lookupTypes
	 */ 
	@Override
	public void registerLookupTypes(ILookupTypeEnum<?>...lookupTypes)
	{
		lookupEnums.addAll(List.of(lookupTypes));
	} 
	
	 
	/***
	 * returns the lookup types values registered
	 * @return
	 */
	protected List<ILookupTypeEnum<?>> getLookupTypeValues() {
		return lookupEnums; 
	}
	
	/***
	 * finds the Lookup type by its class references.
	 * @param clazz
	 * @return
	 */ 
	@Override
	public Optional<ILookupTypeEnum<?>> getLookupTypeByClass(Class<? extends ILookup> clazz){
        try{
            return lookupEnums.stream().filter(v->v.getLookupClass().equals(clazz)).findAny()  ;
        }
        catch(IllegalArgumentException e){
            return Optional.empty();
        }
    }
	
	/***
	 * Finds the lookup type by name 
	 * @param name
	 * @return
	 * @throws NotFoundException 
	 */ 
	@Override
	public ILookupTypeEnum<?> getLookupTypeByName(String name) throws NotFoundException   { 
		Optional<ILookupTypeEnum<?>> lookup = lookupEnums.stream().filter(lkEnum->lkEnum.name().equals(name)).findAny();
		return lookup.orElseThrow(()->new NotFoundException("Lookup Type not found: " + name)) ;
	}
	
	
	/***
	 * Initialize repository map and cache lookups on app startup
	 */
	@EventListener(ApplicationReadyEvent.class)
	protected void initializeLookupsOnSystemStart() {
 
		log.info("Caching lookups ...");
		for (Map.Entry<String, BaseLookupRepository<? extends BaseLookupEntity>> lklistEntry : lkRepositories.entrySet()) {
			String repoName = lklistEntry.getKey();
			String entityName = repoName.replaceAll(".*\\.(.*?)Repository", "$1");
			try { 
     			ILookupTypeEnum<?>  lookupListKey = getLookupTypeByName(entityName);
				if (!repoMap.containsKey(lookupListKey)) {
					log.info("Adding repository for entity {} to repository map: ", lookupListKey.name());
					repoMap.put(lookupListKey, lklistEntry.getValue());
					//table creation doesn't work because native queries #{#entityName} doesn't get mapped to physical name.
					if (Boolean.TRUE.compareTo(autoCreate)==0 )
						lklistEntry.getValue().createTable(); 
					else 
			        	log.info("Define property app.lookups.auto-create=true to auto create " + entityName + " table.");
				}
				 // do first initial call to fill the cache
				lklistEntry.getValue().findAll();
			}catch (NotFoundException e) {
				log.error("Repository {} entity type not defined in lookup list enumeration and will not be preloaded",
						repoName);
				continue;
			} 
		}
		log.info("Cached lookups ...");
	}

	private Integer lookupOrderIncrement(ILookupTypeEnum<?> lookupType) throws NotFoundException {
		Integer maxOrder = 0;
		var repo = repoMap.get(lookupType);
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
		
		maxOrder = repo.findAll().stream().filter(lkp->lkp.getOrder()!=null).mapToInt(lkp -> lkp.getOrder()).max().orElse(0);
		return ++maxOrder;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional 
	public  <T extends BaseLookupEntity,D extends LookupDTO> List<D> saveLookups(ILookupTypeEnum<?> lookupType, List<D> lookupDTOs)
			throws NotFoundException {
		log.info("Saving list of {} lookups for {}",lookupDTOs.size() ,lookupType);
		if (lookupType.isEnum()) return lookupDTOs;
		BaseLookupRepository<T> repo = (BaseLookupRepository<T>) repoMap.get(lookupType); 
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found");
		 
		List<T> lookupList = lookupDTOs.stream().map(dto->{ 
		   T blk = lookupMapper.toEntity(dto,(Class<T>) lookupType.getLookupClass()); 
		   return blk;
		}).collect(Collectors.toList()); 
	    lookupList  =  (List<T>) repo.saveAll( lookupList); 
	    lookupDTOs = (List<D>) lookupList.stream().map(lk->lookupMapper.toDTO(lk)).collect(Collectors.toList()); 
		log.info("Saved list of {} lookups for {}",lookupDTOs.size() ,lookupType);
		return lookupDTOs;
	}
	 
	@Override
	@Transactional
	public <T extends BaseLookupEntity> void deprecateLookup(ILookupTypeEnum<?> lookupType, Long lookupId) throws NotFoundException {
		log.info("Delete lookup Id {} for type {}", lookupId ,lookupType);
		@SuppressWarnings("unchecked")
		BaseLookupRepository<T> repo =  (BaseLookupRepository<T>) repoMap.get(lookupType);
		
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found ");
		T  lookup = repo.findById(lookupId).orElseThrow(()->new NotFoundException("Lookup item not found for id {1}" + lookupId));
		lookup.setDeprecated(true);
		repo.save(lookup);
		log.info("Deleted lookup Id {} for type {}", lookupId ,lookupType);
	}

	@Override
	@Transactional
	public <T extends BaseLookupEntity> void deleteLookup(ILookupTypeEnum<?> lookupType, Long lookupId) throws NotFoundException {
		log.info("Delete lookup Id {} for type {}", lookupId ,lookupType);
		@SuppressWarnings("unchecked")
		BaseLookupRepository<T> repo =  (BaseLookupRepository<T>) repoMap.get(lookupType);
		
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found ");
		T  lookup = repo.findById(lookupId).orElseThrow(()->new NotFoundException("Lookup item not found for id {1}" + lookupId)); 
		repo.delete(lookup);
		log.info("Deleted lookup Id {} for type {}", lookupId ,lookupType);
	}
	
	@Override
	@Transactional
	public <T extends BaseLookupEntity> void deleteAllLookup(ILookupTypeEnum<?> lookupType ) throws NotFoundException {
		log.info("Delete all lookup values for type {}", lookupType);
		@SuppressWarnings("unchecked")
		BaseLookupRepository<T> repo =  (BaseLookupRepository<T>) repoMap.get(lookupType);
		
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found "); 
		repo.deleteAll();
		log.info("Deleted all lookup values for type {}", lookupType);
	}


	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public  <T extends BaseLookupEntity> LookupDTO saveLookup(ILookupTypeEnum<?> lookupType, LookupDTO lookupDTO)
			throws NotFoundException, ConflictException {
		if (lookupType.isEnum()) 
			throw new ConflictException("Enum lookups are read only");
		BaseLookupRepository<T> repo = (BaseLookupRepository<T>) repoMap.get(lookupType);
		if (!lookupType.isEnum() && repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found");
		else if (!lookupType.isEnum())
		{
			T lookup = null;
			if (lookupDTO.getId() == null) {
				if (lookupDTO.getOrder() == null || lookupDTO.getOrder() <= 0) {
					lookupDTO.setOrder(lookupOrderIncrement(lookupType));
				}
				lookup = lookupMapper.toEntity(lookupDTO, (Class<T>) lookupType.getLookupClass());
			} else {
				lookup = getLookupById(lookupType, lookupDTO.getId());
				lookup.setKey(lookupDTO.getKey());
				lookup.setValue(lookupDTO.getValue());
				lookup.setDeprecated(lookupDTO.getDeprecated());
			} 
			lookup = repo.save(lookup);
			lookupDTO = lookupMapper.toDTO(lookup);
		}
//		translationUpdate(lookupType,lookupDTO ); 
		return lookupDTO;
	}

	@Override
	public Map<String, String> getLookupTypes() {
		Map<String, String> keyMap = new HashMap<>();
		for (ILookupTypeEnum<?> lookupType : getLookupTypeValues()) {
			keyMap.put(lookupType.name(),lookupType.getDescription());
		}
		return keyMap;
	}

	@Override
	public Map<String, List<? extends LookupDTO>> getPublicLookups(String[] lookupTypeArray) throws NotFoundException {
		Map<String, List<? extends LookupDTO>> lookupListMap = new HashMap<>();
		for (String lookupTypeName : lookupTypeArray) {
			ILookupTypeEnum<?> lookupType = getLookupTypeByName(lookupTypeName) ;
			if (!lookupType.isEnum()) {
				BaseLookupRepository<? extends BaseLookupEntity> repo = repoMap.get(lookupType);
				if (repo != null) {
					List<LookupDTO> lookupDTOS = lookupMapper.toDTOList(
							repo.findAll().stream().filter(lkp -> !lkp.isDeprecated()).collect(Collectors.toList()));
					lookupListMap.put(lookupTypeName, lookupDTOS);
				}
			} else if (lookupType.getLookupClass() != null) {
				List<LookupDTO> dtoList = lookupMapper
						.toDTOList(List.of(lookupType.getLookupClass().getEnumConstants()));
				lookupListMap.put(lookupTypeName, dtoList);
			}
		}
		return lookupListMap;
	}

	@Override
	public Map<String, List<? extends LookupDTO>> getAllLookups(String[] lookupTypeArray) throws NotFoundException {
		Map<String, List<? extends LookupDTO>> lookupListMap = new HashMap<>(); 
		for (String lookupTypeName : lookupTypeArray) {
			ILookupTypeEnum<?> lookupType =  getLookupTypeByName(lookupTypeName) ;
			if (!lookupType.isEnum()) {
				BaseLookupRepository<? extends BaseLookupEntity> repo = repoMap.get(lookupType);
				if (repo != null) {
					List<LookupDTO> lookupDTOS = lookupMapper.toDTOList(repo.findAll()); 
					lookupListMap.put(lookupTypeName, lookupDTOS);
				}
			} else if (lookupType.getLookupClass() != null) {
				List<LookupDTO> dtoList = lookupMapper
						.toDTOList(List.of(lookupType.getLookupClass().getEnumConstants()));
				lookupListMap.put(lookupTypeName, dtoList);
			}
		}
		return lookupListMap;
	}

	@Override
	public LookupDTO getLookupDTOByKey(ILookupTypeEnum<?> lookupType, String key) throws NotFoundException {
		LookupDTO dto = null;
		if (!lookupType.isEnum()) {
			var repo = repoMap.get(lookupType);
			if (repo == null)
				throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
			dto = repo.findAll().stream().filter(lk -> lk.getKey().equals(key)).findAny().map(lookupMapper::toDTO)
					.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + " key " + key + " not defined"));
		} else if (lookupType.getLookupClass() != null) {
			dto = Stream.of(lookupType.getLookupClass().getEnumConstants()).filter(enm -> enm.getKey().equals(key))
					.findAny().map(lookupMapper::toDTO)
					.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + " key " + key + " not defined"));

		} else
			throw new NotFoundException("Lookup " + lookupType + " enum not defined");
		return dto;
	}

	@Override
	public LookupDTO getLookupDTOById(ILookupTypeEnum<?> lookupType, Long id) throws NotFoundException {
		LookupDTO dto = null;
		if (lookupType.isEnum())
			throw new NotFoundException("Enum lookups not supported " + lookupType + " is of type enum");

		var repo = repoMap.get(lookupType);
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
		dto = repo.findAll().stream().filter(lk -> lk.getId().equals(id)).findAny().map(lookupMapper::toDTO)
				.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + id + " not found "));
		return dto;
	}

	@Override
	public List<LookupDTO> getLookupDTOList(ILookupTypeEnum<?> lookupType) throws NotFoundException {
		List<LookupDTO> dtoList = null;
		if (!lookupType.isEnum()) {
			var repo = repoMap.get(lookupType);
			if (repo == null)
				throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
			dtoList = lookupMapper.toDTOList(repo.findAll());
		} else if (lookupType.getLookupClass() != null) {
			dtoList = lookupMapper.toDTOList(List.of(lookupType.getLookupClass().getEnumConstants()));
		} else
			throw new NotFoundException("Lookup " + lookupType + " enum not defined");
		return dtoList;
	}

	@Override
	public Long getLookupIdByKey(ILookupTypeEnum<?> lookupType, String key) throws NotFoundException {
		if (lookupType.isEnum())
			throw new NotFoundException("Enum lookups not supported " + lookupType + " is of type enum");

		var repo = repoMap.get(lookupType);
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
		return repo.findAll().stream().filter(lk -> lk.getKey().equals(key)).findAny()
				.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + " key " + key + " not defined"))
				.getId();

	}

	@Override
	public List<Long> getLookupIdsByKeys(ILookupTypeEnum<?> lookupType, String... keys) throws NotFoundException {
		if (lookupType.isEnum())
			throw new NotFoundException("Enum lookups not supported " + lookupType + " is of type enum");

		var repo = repoMap.get(lookupType);
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
		List<? extends BaseLookupEntity> listValues = repo.findAll();
		List<Long> listIds = new ArrayList<Long>(keys.length);

		for (String key : keys) {
			listIds.add(listValues.stream().filter(lk -> lk.getKey().equals(key)).findAny()
					.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + " key " + key + " not found"))
					.getId());
		}
		return listIds;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILookup> T getLookupByKey(ILookupTypeEnum<?> lookupType, String key) throws NotFoundException {
		if (!lookupType.isEnum()) {
			var repo = repoMap.get(lookupType);
			if (repo == null)
				throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
			return (T) repo.findAll().stream().filter(lk -> lk.getKey().equals(key)).findAny()
					.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + " key " + key + " not found"));
		} else if (lookupType.getLookupClass() != null) {
			//enum lookup 
			return (T) Stream.of(lookupType.getLookupClass().getEnumConstants()).filter(enm -> enm.getKey().equals(key))
					.findAny().orElseThrow(
							() -> new NotFoundException("Lookup " + lookupType + " enum key " + key + " not defined"));
		} else
			throw new NotFoundException("Lookup " + lookupType + " enum not defined");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILookup> T getLookupById(ILookupTypeEnum<?> lookupType, Long id) throws NotFoundException {
		if (lookupType.isEnum())
			throw new NotFoundException("Enum lookups not supported " + lookupType + " is of type enum");

		var repo = repoMap.get(lookupType);
		if (repo == null)
			throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
		return (T) repo.findAll().stream().filter(lk -> lk.getId().equals(id)).findAny()
				.orElseThrow(() -> new NotFoundException("Lookup " + lookupType + id + " not found "));

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILookup> List<T> getLookupList(ILookupTypeEnum<?> lookupType) throws NotFoundException {
		if (!lookupType.isEnum()) {
			var repo = repoMap.get(lookupType);
			if (repo == null)
				throw new NotFoundException("Lookup list " + lookupType + " repository not found.");
			return (List<T>) repo.findAll();
		} else if (lookupType.getLookupClass() != null) {
			return (List<T>) Stream.of(lookupType.getLookupClass().getEnumConstants()).collect(Collectors.toList());
		} else
			throw new NotFoundException("Lookup " + lookupType + " enum not defined");

	}
	
	
//	private LookupDTO toDTO(ILookup lookup)
//	{
//		 LookupDTO lookupDTO =null; 
//		 if (lookup != null && lookup instanceof HibernateProxy  && ((HibernateProxy)lookup).getHibernateLazyInitializer().isUninitialized())
//		 {
//			   //load the lookupdto from the service
//			   try {
//				   LazyInitializer initializer = ((HibernateProxy)lookup).getHibernateLazyInitializer() ;  
//				   @SuppressWarnings("unchecked")
//				   Class<? extends ILookup> clazz = initializer.getPersistentClass();
//				   ILookupTypeEnum<?> lookupType = getLookupTypeByClass(  clazz).orElseThrow(()-> new NotFoundException("Unexpected class for a lookup type."));
//				   lookupDTO = getLookupDTOById(lookupType, lookup.getId());//this will get from cache
//			   }catch(Exception e)
//			   {   //this shouldn't occur because we are loading an proxy based entity
//				   throw new IllegalArgumentException("Unexpected parameter for loading uninitialized id",e);
//			   }
//		 }
//		 else 
//			 lookupDTO= lookupMapper.toDTO(lookup);
//		 return lookupDTO;
//	}
}
