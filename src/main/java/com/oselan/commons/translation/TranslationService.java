package com.oselan.commons.translation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/***
 * Implementation of a database based message source.  
 * Creates translations on the fly if not found in the database 
 * @author Ahmad Hamid
 *
 */
@Component("messageSource")
@Slf4j
public class TranslationService extends AbstractMessageSource implements ITranslationService {

    @Value( "${app.localization.auto-create}" )
    private Boolean autoCreate;
  
	@Autowired
	private TranslationRepository translationRepository; 
	
	/**Batch*/
	private ConcurrentLinkedQueue<Translation> translationsToSave = new  ConcurrentLinkedQueue< Translation>();
	private CompletableFuture<Void> translationSaver = null;

	@Override
	protected MessageFormat resolveCode(String key, Locale locale) {
		Translation message = getTranslation(key, null, locale.getLanguage());
		String value = message==null? key:message.getValue();
		return new MessageFormat(value, locale);
	}
	
	 
	/***
	 * Helper method alternative to the messageSource getMessage that automatically reads the locale from context 
	 * and saves default value if not found
	 * if default is null , it uses the key as default value.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@Override 
    public String getMessage(String key,String defaultValue)
    {
    	Translation message = this.getTranslation(key, defaultValue, LocaleContextHolder.getLocale().getLanguage());
    	return message.getValue();
    }
     
    
    /***
     * Helper method alternative to the messageSource getMessage that automatically reads the locale from context 
	 * and saves key as default value if not found
     * @param key
     * @return
     */
    @Override 
    public String getMessage(String key )
    {
    	Translation message = this.getTranslation(key, null, LocaleContextHolder.getLocale().getLanguage());
    	return message.getValue();
    }
    
    /***
     * Helper method to retrieve build messages for multiple keys
     */
	@Override
	public Map<String, List<TranslationDTO>> getMessages(Map<String,String> keyDefaultValueMap) {
		 
		Map<String, List<TranslationDTO>> translations = new HashMap<String, List<TranslationDTO>>();
		for (Entry<String,String>  keyValue: keyDefaultValueMap.entrySet() )
		{ 
			List<Translation> transList = this.getTranslation(keyValue.getKey(), keyValue.getValue());
			
			List<TranslationDTO> transDTOlist = transList.stream()
					.map(t->TranslationDTO.builder().value(t.getValue()).locale(t.getLocale()).build())
					.collect(Collectors.toList());
			//find any pending-to-save translations and add them to the list they will override the db ones
			translationsToSave.stream().filter(t->t.getKey().equalsIgnoreCase(keyValue.getKey()))
					.forEach(t->{
						 TranslationDTO dto = transDTOlist.stream().filter(td->td.getLocale().equalsIgnoreCase(t.getLocale())).findAny().orElse(null); 
						 if (dto!=null) 
							 dto.setValue(t.getValue());
						 else 
							 transDTOlist.add(TranslationDTO.builder().locale(t.getLocale()).value(t.getValue()).build()); 
					});
		    
			translations.put(keyValue.getKey(),transDTOlist);
		}
		return translations;
	}
    
	/***
	 * Attempts to retrieve translation from the database for the particular language and if not found 
	 * queues the translation key/locale to be inserted in the database 
	 * @param key
	 * @param language
	 * @return
	 */
	protected Translation getTranslation(String key , String defaultValue ,String language )
	{
		Translation message = translationRepository.findAll().stream().filter(t->t.getKey().equalsIgnoreCase(key) && t.getLocale().equalsIgnoreCase(language)).findAny().orElse(null);  //.findByKeyAndLocale(key, language);
		if (message == null)
		{    
			message = new Translation(  key,language,defaultValue==null? key: defaultValue);
			translationsToSave.add(message);
			triggerTranslationSave();
		}
		return message;
	}
	
	/***
	 * Retrieves all translations from the database for a particular key. 
	 * @param key
	 * @param defaultValue
	 * @param language
	 * @return
	 */
	protected List<Translation> getTranslation(String key , String defaultValue   )
	{
		List<Translation> messages = translationRepository.findAll()
				.stream().filter(t->t.getKey().equalsIgnoreCase(key)).collect(Collectors.toList());   
		if (messages.isEmpty() )
		{    
			Translation message = new Translation(  key, LocaleContextHolder.getLocale().getLanguage(),defaultValue==null? key: defaultValue);
			translationsToSave.add( message);
			triggerTranslationSave();
			messages.add(message);
		}
		return messages;
	}
	
	
	/**
	 * Trigger batch insert after 5 seconds giving time for multiple 
	 */ 
	private void triggerTranslationSave()
	{
		if (translationSaver != null || translationsToSave.isEmpty())
			return;
		synchronized (this) {
			if (translationSaver != null || translationsToSave.isEmpty())
				return;
			translationSaver = CompletableFuture.runAsync(() -> {
				log.info("Saving new translation that were not found for this locale");
				try {//sleep five seconds
					TimeUnit.SECONDS.sleep(5); 
					List<Translation> batch = new ArrayList<>();
					while (!translationsToSave.isEmpty())
						batch.add(translationsToSave.poll());
					translationRepository.saveAllAndFlush(batch);
					translationSaver = null;
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				} 
			});
		}
	}
	
	
	@EventListener(ApplicationReadyEvent.class)
	protected void initializeTranslationsOnSystemStart() {

		log.info("Caching translations ...");
        if (Boolean.TRUE.compareTo(autoCreate)==0 )
          translationRepository.createTable(); 
        else 
        	log.info("Define property app.localization.auto-create to auto create translation table.");
		translationRepository.findAll();
		log.info("Cached translations ..."); 
	}
	
	 
	@Override
	public void setMessage(String key, String value)
    { 
		Translation message = new Translation( key, LocaleContextHolder.getLocale().getLanguage(),value);
		translationsToSave.add(message);
		triggerTranslationSave();
    }
	
	@Override
	public void setMessages(Map<String , List<TranslationDTO>> keyTransMap)
    { 
		keyTransMap.entrySet().stream().forEach( entry -> 
				entry.getValue().stream().forEach(trans->{
			Translation message = new Translation( entry.getKey(),trans.getLocale(),trans.getValue());
			translationsToSave.add( message);
		}));
		triggerTranslationSave();
    }


	
	
	

}