package com.oselan.commons.lookup;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oselan.commons.localization.LocalizationDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) 
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper=true, includeFieldNames=true)
@SuperBuilder
public class LookupLocalizedDTO extends LookupDTO {

    private static final long serialVersionUID = 3339776955244716802L;
    
    protected List<LocalizationDTO> values;
    
    
}
