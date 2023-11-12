package com.oselan.commons.lookup;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor  
@ToString(callSuper=true, includeFieldNames=true) 
@Builder
public class LookupTypeDTO implements Serializable   {

    private static final long serialVersionUID = 3339776955244716802L;
    
    private String name;
    private String description; 
    private Boolean isReadOnly;
}
