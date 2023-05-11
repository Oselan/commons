package com.oselan.commons.translation;

import java.util.List;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
 
public interface TranslationRepository extends JpaRepository<Translation, Long> {
	
	 @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	  Translation findByKeyAndLocale(String key, String locale);
	  
	  @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	  @Query(value = "SELECT t FROM #{#entityName} t ")
	  List<Translation> findAll();
	  
	  
	  @Query(nativeQuery = true,value="CREATE TABLE  IF NOT EXISTS  {h-schema}$[#{#entityName}]  (\r\n"
	      + "    id serial4 NOT NULL,\r\n"
	      + "    \"key\" varchar NOT NULL,\r\n"
	      + "    locale varchar NULL,\r\n"
	      + "    value varchar NULL,\r\n"
	      + "    CONSTRAINT $[#{#entityName}]_pk PRIMARY KEY (id),\r\n"
	      + "    CONSTRAINT $[#{#entityName}]_un UNIQUE (\"key\",locale)\r\n"
	      + ");" )
	    @Modifying
	    @Transactional
	    void createTable(); 
}





