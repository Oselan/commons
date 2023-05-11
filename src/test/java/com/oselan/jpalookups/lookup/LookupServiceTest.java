package com.oselan.jpalookups.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.oselan.common.exceptions.ConflictException;
import com.oselan.common.exceptions.NotFoundException;
import com.oselan.common.lookup.ILookup;
import com.oselan.common.lookup.ILookupService;
import com.oselan.common.lookup.LookupDTO;
import com.oselan.common.lookup.LookupMapper;
import com.oselan.common.lookup.LookupSimpleDTO;
import com.oselan.jpalookups.lookup_sample.EntityLk;
import com.oselan.jpalookups.lookup_sample.EnumLk;
import com.oselan.jpalookups.lookup_sample.LookupType;

import lombok.SneakyThrows;

//@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
@SpringBootTest
@ActiveProfiles("test") 
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class  LookupServiceTest {

	private final static String DUMMY_KEY ="DUMMY_KEY";
	private final static String DUMMY_KEY2 ="DUMMY_KEY2"; 
	
	@Autowired
	private ILookupService lookupService;
	
	
	@Autowired
	private  LookupMapper lookupMapper; 
	 
	@BeforeAll
	private static void setup()
	{   
		LocaleContextHolder.setLocale(new Locale("ar")); 
	}
	
	@AfterAll
	@SneakyThrows(InterruptedException.class)
	private static void destroy()
	{
		//sleep so any translations get to be saved 
		TimeUnit.SECONDS.sleep(5); 
	}
	  
	 
	@Order(1)
	@Test 
	void testCreateNewLookupDTO() throws NotFoundException, ConflictException
	{ 
		lookupService.deleteAllLookup(LookupType.EntityLK);
		//create a new lookup value
		LookupDTO dto = LookupDTO.builder().key(DUMMY_KEY).value("dummy value").build();
		LookupDTO dto2 = lookupService.saveLookup(LookupType.EntityLK, dto);
		assertNotNull(dto2.getId());
		assertEquals(dto2.getKey(),dto.getKey());
		assertEquals(dto2.getValue(),dto.getValue()); 
		assertEquals(dto2.getDeprecated(),false);
		
		LookupDTO dto3 = LookupDTO.builder().key(DUMMY_KEY2).value("dummy value2").deprecated(true).build();
	    dto3 = lookupService.saveLookup(LookupType.EntityLK, dto3); 
	    assertEquals(dto3.getOrder(), dto2.getOrder()+1);
	    
	    //recreating existing dummy fails
	    assertThrows(DataIntegrityViolationException.class,()-> lookupService.saveLookup(LookupType.EntityLK, LookupDTO.builder().key(DUMMY_KEY2).value("dummy value2").build())); 
	    
		//Can not create enum 
		assertThrows(ConflictException.class, ()->lookupService.saveLookup(LookupType.EnumLK , LookupDTO.builder().key(DUMMY_KEY).value("dummy value").build()));
	}
	
	@Test
	void testGetLookupDTOByKey() throws NotFoundException {
		LookupDTO dto = lookupService.getLookupDTOByKey(LookupType.EntityLK, DUMMY_KEY);
		assertNotNull(dto);
		assertEquals(dto.getKey(),DUMMY_KEY);
	}
	
	@Test
	void testGetLookupDTOById() throws NotFoundException {
		ILookup  lk = lookupService.getLookupByKey(LookupType.EntityLK, DUMMY_KEY);
		LookupDTO dto2 = lookupService.getLookupDTOById(LookupType.EntityLK, lk.getId());
		assertNotNull(dto2);
		assertEquals(dto2.getKey(),DUMMY_KEY);
	}
	
	
	@Test
	void testGetLookupDTOByKeyforEnum() throws NotFoundException { 
		LookupDTO dto = lookupService.getLookupDTOByKey(LookupType.EnumLK, EnumLk.KEY1.getKey());
		assertNotNull(dto);
		assertEquals(dto.getKey(), EnumLk.KEY1.getKey());
	}

	@Test
	void testGetLookupIdByKey() throws NotFoundException {
		Long id = lookupService.getLookupIdByKey(LookupType.EntityLK, DUMMY_KEY);
		assertNotNull(id); 
	}

	@Test
	void testGetLookupIdsByKeyforEnum() throws NotFoundException {
		List<Long> ids = lookupService.getLookupIdsByKeys(LookupType.EntityLK, DUMMY_KEY,DUMMY_KEY2);
		assertNotNull(ids);
		assertTrue(ids.size()>0);  
	}

	@Test
	void testGetLookupByKeyforEnum()  throws NotFoundException {
		EnumLk enumLK = lookupService.getLookupByKey(LookupType.EnumLK,EnumLk.KEY1.getKey());
		assertNotNull(enumLK);
		assertEquals(enumLK.getKey(),EnumLk.KEY1.getKey());  
	}

	@Test
	void testGetLookupList()  throws NotFoundException {
		 List<EntityLk> entityLKs = lookupService.getLookupList(LookupType.EntityLK);
		assertNotNull(entityLKs);
		assertTrue(entityLKs.size()>0);  
	}
	
	@Test
	void testGetLookupListforEnum()  throws NotFoundException {
		 List<EnumLk> enumLKs = lookupService.getLookupList(LookupType.EnumLK);
		assertNotNull(enumLKs);
		assertTrue(enumLKs.size()>0);  
	}
	
	 
	@Test
	void testMapper()  throws NotFoundException {
		EntityLk entityLK = lookupService.getLookupByKey(LookupType.EntityLK, DUMMY_KEY);
		assertNotNull(entityLK);
		assertEquals(entityLK.getKey(),DUMMY_KEY);   
		LookupDTO dto  = lookupMapper.toDTO(entityLK); 
		assertNotNull(dto); 
		EntityLk entityLK2 = lookupMapper.toEntity(dto, EntityLk.class);
		assertNotNull(entityLK2); 
		assertEquals(entityLK.getId(), entityLK2.getId()); 
	}
	
	@Test
	void testMapperEnum()  throws NotFoundException {
		EnumLk enumLK = lookupService.getLookupByKey(LookupType.EnumLK, EnumLk.KEY1.getKey());
		assertNotNull(enumLK);
		assertEquals(enumLK.getKey(),EnumLk.KEY1.getKey());   
		LookupDTO dto  = lookupMapper.toDTO(enumLK); 
		assertNotNull(dto);
		
		enumLK  =lookupMapper.toEnum(LookupSimpleDTO.builder().key(EnumLk.KEY1.getKey()).build(),EnumLk.class );
		assertNotNull(enumLK);
		assertEquals(enumLK,EnumLk.KEY1); 
	}
	
	@Test
	void testMapperList()  throws NotFoundException {
		List<EntityLk> entityLKs = lookupService.getLookupList(LookupType.EntityLK );
		assertNotNull(entityLKs);    
		List<LookupDTO> dtoList  = lookupMapper.toDTOList(entityLKs); 
		assertNotNull(dtoList);
		assertEquals(entityLKs.size(), dtoList.size());
	}
	
	@Test
	void testMapperListEnum()  throws NotFoundException {
		List<EnumLk> enumLKs = lookupService.getLookupList(LookupType.EnumLK);
		assertNotNull(enumLKs);    
		List<LookupDTO> dtoList  = lookupMapper.toDTOList(enumLKs); 
		assertNotNull(dtoList);
		assertEquals(enumLKs.size(), dtoList.size());
	}
	
	
	@Test
	void testGetLookupTypes() throws NotFoundException
	{
		Map<String, String> typesMap = lookupService.getLookupTypes();
		assertNotNull(typesMap);
		assertTrue(typesMap.containsKey(LookupType.EntityLK.name()));
		assertTrue(typesMap.containsKey(LookupType.EnumLK.name()));
	}
	
	
	@Test
	void testGetPublicLookups() throws NotFoundException
	{
		Map<String, List<? extends LookupDTO>> mapList = lookupService.getPublicLookups(new String[] {"EntityLK","EnumLK" });
		assertNotNull(mapList);
		assertTrue(mapList.containsKey(LookupType.EntityLK.name()));
		for ( LookupDTO lkp: mapList.get(LookupType.EntityLK.name()))
		{   //all are false
			assertFalse(lkp.getDeprecated());
		} 
		assertTrue(mapList.containsKey(LookupType.EnumLK.name()));
		for ( LookupDTO lkp: mapList.get(LookupType.EnumLK.name()))
		{   //all are false
			assertFalse(lkp.getDeprecated());
		} 
	}
	
	
	@Test
	void testGetLookups() throws NotFoundException
	{
		Map<String, List<? extends LookupDTO>> mapList = lookupService.getAllLookups(new String[] {"EntityLK","EnumLK" });
		assertNotNull(mapList);
		assertTrue(mapList.containsKey(LookupType.EntityLK.name())); 
		assertTrue(mapList.containsKey(LookupType.EnumLK.name())); 
	}
	

}
