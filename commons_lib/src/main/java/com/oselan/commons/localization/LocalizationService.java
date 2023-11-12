package com.oselan.commons.localization;

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

import org.springframework.transaction.annotation.Transactional;

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
 * Creates localizations on the fly if not found in the database 
 * @author Ahmad Hamid
 *
 */
@Component("messageSource")
@Slf4j
public class LocalizationService extends AbstractMessageSource implements ILocalizationService {

    @Value( "${app.localization.auto-create:false}" )
    private Boolean autoCreate;
  
	@Autowired
	private LocalizationRepository localizationRepository; 
	
	/**Batch*/
	private ConcurrentLinkedQueue<Localization> localizationsToSave = new  ConcurrentLinkedQueue< Localization>();
	private CompletableFuture<Void> localizationSaver = null;

	@Override
	protected MessageFormat resolveCode(String key, Locale locale) {
		Localization message = getLocalization(key, null, locale.getLanguage());
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
    	Localization message = this.getLocalization(key, defaultValue, LocaleContextHolder.getLocale().getLanguage());
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
    	Localization message = this.getLocalization(key, null, LocaleContextHolder.getLocale().getLanguage());
    	return message.getValue();
    }
    
    /***
     * Helper method to retrieve build messages for multiple keys
     */
	@Override
	public Map<String, List<LocalizationDTO>> getMessages(Map<String,String> keyDefaultValueMap) {
		 
		Map<String, List<LocalizationDTO>> localizations = new HashMap<String, List<LocalizationDTO>>();
		for (Entry<String,String>  keyValue: keyDefaultValueMap.entrySet() )
		{ 
			List<Localization> localeList = this.getLocalization(keyValue.getKey(), keyValue.getValue());
			
			List<LocalizationDTO> transDTOlist = localeList.stream()
					.map(t->LocalizationDTO.builder().value(t.getValue()).locale(t.getLocale()).build())
					.collect(Collectors.toList());
			//find any pending-to-save translations and add them to the list they will override the db ones
			localizationsToSave.stream().filter(t->t.getKey().equalsIgnoreCase(keyValue.getKey()))
					.forEach(t->{
						 LocalizationDTO dto = transDTOlist.stream().filter(td->td.getLocale().equalsIgnoreCase(t.getLocale())).findAny().orElse(null); 
						 if (dto!=null) 
							 dto.setValue(t.getValue());
						 else 
							 transDTOlist.add(LocalizationDTO.builder().locale(t.getLocale()).value(t.getValue()).build()); 
					});
		    
			localizations.put(keyValue.getKey(),transDTOlist);
		}
		return localizations;
	}
    
	/***
	 * Attempts to retrieve translation from the database for the particular language and if not found 
	 * queues the translation key/locale to be inserted in the database 
	 * @param key
	 * @param language
	 * @return
	 */
	protected Localization getLocalization(String key , String defaultValue ,String language )
	{
		Localization message = localizationRepository.findAll().stream().filter(t->t.getKey().equalsIgnoreCase(key) && t.getLocale().equalsIgnoreCase(language)).findAny().orElse(null);  //.findByKeyAndLocale(key, language);
		if (message == null)
		{    
			message = new Localization(  key,language,defaultValue==null? key: defaultValue);
			localizationsToSave.add(message);
			triggerSaveQueuedLocalizations();
		}
		return message;
	}
	
	/***
	 * Retrieves all localizations from the database for a particular key. 
	 * @param key
	 * @param defaultValue
	 * @param language
	 * @return
	 */
	protected List<Localization> getLocalization(String key , String defaultValue   )
	{
		List<Localization> messages = localizationRepository.findAll()
				.stream().filter(t->t.getKey().equalsIgnoreCase(key)).collect(Collectors.toList());   
		if (messages.isEmpty() )
		{    
			Localization message = new Localization(  key, LocaleContextHolder.getLocale().getLanguage(),defaultValue==null? key: defaultValue);
			localizationsToSave.add( message);
			triggerSaveQueuedLocalizations();
			messages.add(message);
		}
		return messages;
	}
	
	
	/**
	 * Trigger batch insert after 5 seconds giving time for multiple 
	 */ 
	private void triggerSaveQueuedLocalizations()
	{
		if (localizationSaver != null || localizationsToSave.isEmpty())
			return;
		synchronized (this) {
			if (localizationSaver != null || localizationsToSave.isEmpty())
				return;
			localizationSaver = CompletableFuture.runAsync(() -> {
				log.info("Saving new translation that were not found for this locale");
				try {//sleep five seconds
					TimeUnit.SECONDS.sleep(5); 
					List<Localization> batch = new ArrayList<>();
					while (!localizationsToSave.isEmpty())
						batch.add(localizationsToSave.poll());
					localizationRepository.saveAllAndFlush(batch);
					localizationSaver = null;
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				} 
			});
		}
	}
	
	
	@EventListener(ApplicationReadyEvent.class)
	@Transactional(rollbackFor = Exception.class)
	protected void initializeOnSystemStart() {

		log.info("Caching localizations ...");
        if (Boolean.TRUE.compareTo(autoCreate)==0 )
          localizationRepository.createTable(); 
        try {	
		localizationRepository.findAll();
        }catch(RuntimeException re)
        {
          log.error("Define property app.localization.auto-create to auto create localization table.");
//          throw re;//so that application startup proceeds 
        }
		log.info("Cached localizations ..."); 
	}
	
	 
	@Override
	public void setMessage(String key, String value)
    { 
		Localization message = new Localization( key, LocaleContextHolder.getLocale().getLanguage(),value);
		localizationsToSave.add(message);
		triggerSaveQueuedLocalizations();
    }
	
	@Override
	public void setMessages(Map<String , List<LocalizationDTO>> keyTransMap)
    { 
		keyTransMap.entrySet().stream().forEach( entry -> 
				entry.getValue().stream().forEach(trans->{
			Localization message = new Localization( entry.getKey(),trans.getLocale(),trans.getValue());
			localizationsToSave.add( message);
		}));
		triggerSaveQueuedLocalizations();
    }


	
	
	

}