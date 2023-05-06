package com.oselan.jpalookups.common.translation;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

//@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
	
	 @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	  Translation findByKeyAndLocale(String key, String locale);
	  
	  @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"))
	  @Query(value = "SELECT t FROM #{#entityName} t ")
	  List<Translation> findAll();
}





