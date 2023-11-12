
package com.oselan.commons.lookup;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonView;
import com.oselan.commons.dto.BaseViews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
/***
 * Any time Lookup Simple is used , either Id and Key must be provided by front end .
 * @author Ahmad Hamid
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonView({BaseViews.AllView.class})
public class LookupSimpleDTO implements Serializable {

    private static final long serialVersionUID = 3339776955244716802L;
 
    protected Long id; 
    protected String key; 
    protected String value; 
    
    @Override
    public boolean equals(Object obj) {
      if(this == obj)
        return true;
      if(obj == null)
        return false;
      if(getClass() != obj.getClass())
        return false;
      LookupSimpleDTO other = (LookupSimpleDTO) obj;
      return Objects.equals(id, other.id) || Objects.equals(key, other.key);
    }
    @Override
    public int hashCode() {
      return Objects.hash(id, key);
    }
    
    
}
