package com.oselan.commons.localization;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class LocalizationDTO {  
  
     @Schema(allowableValues = {"en","ar","fr"},defaultValue = "en")
	 private String locale;
	 private String value;
}
