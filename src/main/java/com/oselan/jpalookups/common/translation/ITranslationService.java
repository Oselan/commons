package com.oselan.jpalookups.common.translation;

import java.util.List;
import java.util.Map;

/***
 * Handles translation operations and managing database driven service
 * @author Ahmad Hamid 
 */
public interface ITranslationService {

	/***
	 * Queues key/value to be saved in translation message source using the locale context.
	 * @param key
	 * @param value
	 */
	void setMessage(String key, String value);

	/***
	 * Queues a set of Keys/Values to be saved in translation into message source  using the locale context.
	 * @param keyValueMap
	 */
//	void setMessages(Map<String, String> keyValueMap);
	
	
	/***
	 * Queues a list of key-translations for saving into the message source 
	 * @param keyTranslationsMap
	 */
	void setMessages(Map<String,List<TranslationDTO>> keyTranslationsMap);
	
	/***
	 * Helper method alternative to the messageSource getMessage that automatically reads the locale from context 
	 * and saves key as default value if not found
	 * @param key
	 * @return
	 */
	String getMessage(String key);

	/***
	 * Helper method alternative to the messageSource getMessage that automatically reads the locale from context 
	 * and saves default value if not found
	 * if default is null , it uses the key as default value.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String getMessage(String key, String defaultValue);

	/***
	 * Given a list of keys , returns a map of keys and translations available for that key.
	 * @param keys
	 * @return
	 */
	Map<String,List<TranslationDTO>> getMessages(Map<String,String> keyDefaultValueMap);

}
