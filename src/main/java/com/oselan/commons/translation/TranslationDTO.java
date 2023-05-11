package com.oselan.commons.translation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode( ) 
@Builder
public class TranslationDTO {  
	 private String locale;
	 private String value;
}
