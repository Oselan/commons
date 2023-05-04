package com.oselan.jpalookups.sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.EmptyInterceptor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oselan.jpalookups.common.entity.SnakeCaseNamingStrategy;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class HibernateInterceptor extends EmptyInterceptor {
	
	@Autowired
	protected PhysicalNamingStrategy namingStrategy;
	private final static Pattern regex = Pattern.compile("(\\$\\[.*?\\])");
	
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

}
