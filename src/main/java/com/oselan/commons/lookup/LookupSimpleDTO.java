
package com.oselan.commons.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oselan.commons.dto.SimpleIdDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) 
@JsonIgnoreProperties(ignoreUnknown = true) 
@SuperBuilder
public class LookupSimpleDTO extends  SimpleIdDTO {

    private static final long serialVersionUID = 3339776955244716802L;
    protected String key; 
    protected String value; 
}
