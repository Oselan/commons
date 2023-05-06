package com.oselan.jpalookups.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.hibernate.EmptyInterceptor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.oselan.jpalookups.common.entity.SnakeCaseNamingStrategy;

import lombok.extern.slf4j.Slf4j;

 
@Slf4j
@Configuration
public class HibernateInterceptorConfig   {
	
	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory(
	        EntityManagerFactoryBuilder factory, DataSource dataSource,
	        JpaProperties properties) {
	    Map<String, Object> jpaProperties = new HashMap<String, Object>();
	    jpaProperties.putAll(properties.getHibernateProperties(dataSource));
	    jpaProperties.put("hibernate.ejb.interceptor", hibernateInterceptor());
	    return factory.dataSource(dataSource).packages("sample.data.jpa")
	            .properties((Map) jpaProperties).build();
	}

	private final static Pattern regex = Pattern.compile("(\\$\\[.*?\\])");
	
	@Bean
	EmptyInterceptor hibernateInterceptor(PhysicalNamingStrategy namingStrategy) {
	    return new EmptyInterceptor() {
	    	@Override
	        public String onPrepareStatement(String sql) {
	              String prepedStatement = super.onPrepareStatement(sql);
	              prepedStatement = 
	            		  regex.matcher(prepedStatement).replaceAll(m->{
	            			  String entityName = m.group();
	            			  entityName = entityName.substring(2,entityName.length()-1);
	            			  String tableName = (new SnakeCaseNamingStrategy()).toPhysicalTableName(new Identifier(entityName,false), null).getText();
	                    	  log.info("Translating entity  " + entityName + " to " + tableName);
	                    	  return tableName;
	            		  })   ;  
	              return prepedStatement;
	        }
	    };
	}
	 
	 
}
