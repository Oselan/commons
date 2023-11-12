package com.oselan.commons.localization;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationKey implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3929177993468187024L;
	public String key;
	public String locale;
     
}
