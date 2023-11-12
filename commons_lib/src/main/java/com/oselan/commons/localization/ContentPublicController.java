package com.oselan.commons.localization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name="Content Public API",description = "Dynamic content operations") 
@RequiredArgsConstructor
@Validated  
public class ContentPublicController  {
  
    protected final ILocalizationService translationService;
      
    @Operation(summary = "Retrieve Contents", description = "Get localized list of values by keys" )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "" ) 
    @Parameter(name = "Accept-Language", in = ParameterIn.HEADER,  description = "User selected language, default (en)", required = false, examples = {
        @ExampleObject(name = "English", value = "en"),
        @ExampleObject(name = "Arabic", value = "ar") })
    public ResponseEntity<Map<String,String>> getContentsforKeys( 
            @RequestParam(name = "keys") List<String> keys ) {
      Map<String,String> keyValueMap = new HashMap<>(keys.size());
      keys.stream().forEach(key->keyValueMap.put(key, translationService.getMessage(key)));
      return new ResponseEntity<Map<String,String>>(keyValueMap, HttpStatus.OK);
    }
     
   
}
