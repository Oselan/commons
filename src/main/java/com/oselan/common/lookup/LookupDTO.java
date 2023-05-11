package com.oselan.common.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
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
public class LookupDTO extends LookupSimpleDTO {

    private static final long serialVersionUID = 3339776955244716802L;
    protected Integer order;
    @Builder.Default
    protected Boolean deprecated=Boolean.FALSE;
}
