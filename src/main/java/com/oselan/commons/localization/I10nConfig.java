package com.oselan.commons.localization;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/***
 * TODO define a ehcachManager for spring caching
 * @author Ahmad Hamid
 *
 */
@Configuration 
public class I10nConfig implements WebMvcConfigurer {

    @Bean("headerresolver")
    @Primary
    LocaleResolver localeResolver() {
		   AcceptHeaderLocaleResolver ahlr = new AcceptHeaderLocaleResolver();
		   ahlr.setDefaultLocale(Locale.US); 
	      return ahlr;
	  }
	 
	  @Override
	  public void addInterceptors(InterceptorRegistry registry) {
	        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
	        localeChangeInterceptor.setParamName("lang");
	        registry.addInterceptor(localeChangeInterceptor);
	    }
}
