package com.oselan.commons.localization;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name="Content Admin API",description = "Dynamic content operations") 
@RequiredArgsConstructor
@Validated  
public class ContentController {
  
    protected final ILocalizationService translationService;
      
    @Operation(summary = "Get Contents", description = "get list of localized values for keys" )
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK) 
    public  Map<String,List<LocalizationDTO>>  getContentsforKeysWithLocalizations( 
        @RequestParam(name = "keys") List<String> keys ) {
        Map<String,String> keyDefaultMap =  keys.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        Map<String,List<LocalizationDTO>> keysLocalizationsMap= translationService.getMessages(keyDefaultMap);
       return  keysLocalizationsMap ;
    }
    
    @Operation(summary = "Update Contents", description = "set list of localized values for keys" )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void  setContentsforKeys( @RequestBody Map<String,List<LocalizationDTO>> keyTranslations ) {
       translationService.setMessages(keyTranslations); 
    }
     
   
}
