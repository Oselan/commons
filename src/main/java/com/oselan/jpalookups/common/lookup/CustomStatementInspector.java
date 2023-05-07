package com.oselan.jpalookups.common.lookup;

import java.util.regex.Pattern;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.lookups", name = "auto-create", havingValue = "true")
public class CustomStatementInspector implements StatementInspector {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -5025844973195865864L;

	@Bean
	@ConditionalOnProperty(prefix = "app.lookups", name = "auto-create", havingValue = "true")
    HibernatePropertiesCustomizer hibernateCustomizer(PhysicalNamingStrategy namingStrategy ) {
        return (properties) -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, new CustomStatementInspector(namingStrategy));
    }
	
	private final static Pattern regex = Pattern.compile("(\\$\\[.*?\\])");
	
	private PhysicalNamingStrategy namingStrategy; 
    public CustomStatementInspector(PhysicalNamingStrategy namingStrategy) {
		super();
		this.namingStrategy = namingStrategy;
	}


	@Override
    public String inspect(String sql) {
        // check if query is modifying and change sql query 
    	String newSql = regex.matcher(sql).replaceAll(m->{
			  String entityName = m.group();
			  entityName = entityName.substring(2,entityName.length()-1);
			  String tableName = namingStrategy.toPhysicalTableName(new Identifier(entityName,false), null).getText();
      	      log.info("Translating entity  " + entityName + " to " + tableName);
      	      return tableName;
		  })   ;  
    	return newSql;
    }
}