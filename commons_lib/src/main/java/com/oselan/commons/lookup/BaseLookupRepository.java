package com.oselan.commons.lookup;

import java.util.List;

import javax.persistence.QueryHint;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

/***
 * Base interface of all lookup repositories. 
 * @author Ahmad Hamid
 * @param <T>
 */ 
@NoRepositoryBean
public interface BaseLookupRepository<T extends BaseLookupEntity> extends JpaRepository<T, Long> {
 
	@QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	@Query(value = "SELECT t FROM #{#entityName} t  ORDER BY t.order asc, t.value asc")
	List<T> findAll();
	
//	@Query(value = "SELECT t FROM  {h-schema}#{#entityName} t  ORDER BY t.order asc, t.value asc", nativeQuery=true)
//	List<T> findAll2();
	
	void deleteByKey(String key); 
	
	@Query(nativeQuery = true,value="CREATE TABLE  IF NOT EXISTS {h-schema}$[#{#entityName}] (\r\n"
			+ "	id serial4 NOT NULL,\r\n"
			+ "	\"key\" varchar NULL,\r\n"
			+ "	value varchar NULL,\r\n"
			+ "	\"order\" int4 NULL,\r\n"
			+ "	deprecated boolean NULL DEFAULT false,\r\n"
			+ "	CONSTRAINT #{#entityName}_pk PRIMARY KEY (id),\r\n"
			+ "	CONSTRAINT #{#entityName}_un UNIQUE (\"key\")\r\n"
			+ ");")
	@Modifying
	@Transactional
	void createTable(); 
}
