package com.oselan.commons.translation;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TranslationKey implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3929177993468187024L;
	private String key;
    private String locale;
     
}
