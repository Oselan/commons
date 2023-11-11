package com.oselan.commons.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
/***
 * Builds a search specification   
 */
public class SearchSpecificationBuilder<E >   {
   
   protected final List<Specification<E>> specifications;
   private Map<String, String>  fieldsMapping;
 	 
   public SearchSpecificationBuilder() { 
       specifications =new ArrayList<Specification<E>>();
   }
   
   public int getCriteriaCount()
   {
     return specifications.size();
   }
   
	/***
	 * returns list of search criteria collected.
	 * @deprecated does not support groups of specifications
	 * @return
	 */
    @Deprecated
	public List<SearchCriterion> getParams() {
		return specifications.stream().filter(spec->spec instanceof SearchSpecification<?> )
		    .map(spec->((SearchSpecification<E>)spec).getCriteria().get(0))
		    .collect(Collectors.toList())
		    ;
	}

	/***
	 * Search for the parameter by key
	 * @param key
	 * @deprecated does not support groups of specifications
	 * @return
	 */
    @Deprecated
	public SearchCriterion getParam(String key) {
		String actualKey = getFieldMapping(key);
		Optional<Specification<E>> keySpecification= specifications.stream().filter(spec->(spec instanceof SearchSpecification<?> 
		    &&  ((SearchSpecification<E>)spec).getCriteria().get(0).getKey().equalsIgnoreCase(actualKey))).findAny();
		if (keySpecification.isPresent())
		  return ((SearchSpecification<E>) keySpecification.get()).getCriteria().get(0);
		else 
		  return null;
	}

	/**
	 * 
	 * @param fieldsMapping Map is used to map custom named fields in search string
	 *                      to corresponding field names recognized by Search
	 *                      Specification ex: {"name","surveyorUser.name"}, if the
	 *                      search string key is "name", the corresponding key that
	 *                      should be filtered is "surveyorUser.name", which is
	 *                      related to the "name" property in the "surveyorUser"
	 *                      property in the entity class. Mapping done in
	 *                      getFieldMapping method
	 */
	public SearchSpecificationBuilder(Map<String, String> fieldsMapping) {
		this();
		this.fieldsMapping = fieldsMapping;
	}
	

	/***
	 * @param searchString: Pass a search string of one or more expressions
	 *                      separated by comma , for AND or pipe | for OR  of the format
	 *                      <field><operator><value> field can be a field name - or
	 *                      field.subfield if field is a subentity. operator can be
	 *                      any of : ! > < ~ for equal not-equal greater-than
	 *                      less-than and like respectively. the value is a string
	 *                      format of the field data type and can be enclosed in
	 *                      punctuation marks(single quote.) if operator is like an
	 *                      * can be used as wild character
	 */
	public final SearchSpecificationBuilder<E> with(final String searchString) {
		if (StringUtils.hasText(searchString)) {
			
			
			String operationSetExper = String.join("|", SearchOperator.OPERATION_SYMBOLS);
			String logicalOperationSet = String.join("", LogicOperator.LOGIC_OPERATION_SYMBOLS );
			Pattern pattern = Pattern.compile(
					"((?:\\w|\\.)+?)(" + operationSetExper + ")(\\p{Punct}?)(.+?)(\\p{Punct}?)(["+logicalOperationSet+"]?)",
					Pattern.UNICODE_CHARACTER_CLASS);
			Matcher matcher = pattern.matcher(searchString);
			while (matcher.find()) {
			    String key= matcher.group(1); 
			    String prefix= matcher.group(3);
			    String value = matcher.group(4);
			    String suffix= matcher.group(5);
			    LogicOperator joinOperator =LogicOperator.getLogicOperatorBySymbol(matcher.group(6));
			    SearchOperator operator = SearchOperator.getOperatorBySymbol(matcher.group(2));
			    operator = SearchOperator.getSubOperatorBySymbol(operator,prefix,value,suffix);
			    if (joinOperator!=null)
			      with(joinOperator,key, operator , value  );
			    else  
			      with(key, operator,  value );
			}
		}
		return this;
	}

	/**
	 * Pass a orPredicate <String>, key <String>, operation <String>, value
	 * <Object>, prefix <String>, suffix <String>. operation will be mapped to its
	 * corresponding search operation. all parameters will be passed to the main
	 * method to be compiled and added as new search criterion.
	 * 
	 * @param logicalOperator  | or  , and
	 * @param key
	 * @param operator
	 * @param value 
	 * @return SearchSpecificationBuilder<E>
	 */

	public final SearchSpecificationBuilder<E> with(final String logicalOperator,final String key, final String operator,final String prefix, final Object value,
        final String suffix) {

	  LogicOperator joinOperator = LogicOperator.getLogicOperatorBySymbol(logicalOperator);
      SearchOperator expOperator = SearchOperator.getOperatorBySymbol(operator);
      expOperator = SearchOperator.getSubOperatorBySymbol(expOperator, prefix, value, suffix);
      
      if(joinOperator != null)
        with(joinOperator, key, expOperator,  value );
      else
        with(key, expOperator, value );
      return this;
    }

 

	/**
	 * Pass a orPredicate <String>, key <String>, operation <SearchOperation>, value
	 * <Object>, prefix <String>, suffix <String> to be compiled and added as new
	 * search criterion
	 * 
	 * @param key
	 * @param operation
	 * @param value 
	 * @return SearchSpecificationBuilder<E>
	 */
	public final SearchSpecificationBuilder<E> with(final String key, SearchOperator operator, final Object value ) {
		return with(null,  key, operator,  value );
	}
	
	/**
	 * Pass a orPredicate <String>, key <String>, operation <SearchOperation>, value
	 * <Object>, prefix <String>, suffix <String> to be compiled and added as new
	 * search criterion
	 * 
	 * @param orPredicate
	 * @param key
	 * @param operation
	 * @param value 
	 * @return SearchSpecificationBuilder<E>
	 */
	
	public final SearchSpecificationBuilder<E> with(final LogicOperator logicJoinOperator, final String key,
			SearchOperator operator,  final Object value ) {
	     Object valueStr  ;
	     if (operator == SearchOperator.LIKE)   // the operation may be complex operation
	       valueStr = value.toString().replaceAll("[*]", "%"); 
	     else if (operator == SearchOperator.IN)
	        if (value.getClass().isArray())
	          valueStr = value;
	        else //attempt to split it to an array
	          valueStr = value.toString().split("\\;");
	     else 
	       valueStr = value.toString();
	     specifications.add(new SearchSpecification<>(new SearchCriterion(logicJoinOperator, getFieldMapping(key), operator, valueStr)));
		 return this;
	}
	
	 
	
	/**
   * Attaches a search specification criteria to parameters list
   * @param spec
   * @return
   */
  public final SearchSpecificationBuilder<E> with(SearchSpecification<E> spec) {
  	return with(spec );
  }

  /**
   * Attaches a search criteria to parameters list
   * @param criteria
   * @return
   */
  public final SearchSpecificationBuilder<E> with(SearchCriterion criterion) {
    specifications.add(new SearchSpecification<>(criterion));
  	return this;
  }
  
  /***
   * Attaches multiple criteria grouped into single specification
   * @param criteria
   * @return
   */
  public final SearchSpecificationBuilder<E> with(SearchCriterion...criteria) {
    SearchSpecification<E> spec = new SearchSpecification<>(); 
    specifications.add(new SearchSpecification<>(criteria));
    return this;
  }
  

  /***
   * Attaches a specification to the list of criteria specifications. 
   * @param specification
   */
  public SearchSpecificationBuilder<E> with(Specification<E> specification) {
  	specifications.add(specification);
  	return this;
  }
  
  /***
   * Adds a distinct specification to the query
   * @return
   */
  public SearchSpecificationBuilder<E> withDistinct()
  {
    Specification<E> spec = (root, query, criteriaBuilder) -> {
      query.distinct(true);
      return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    };
    specifications.add(spec);
    return this;
  }

  /***
	 * Builds the specifications and joins them together returning a cummulative
	 * specification of all criteria
	 * 
	 * @return
	 */
	public Specification<E> build() {

		Specification<E> result = null;
		// compile and add params
//		if (!params.isEmpty()) {
//			result = new SearchSpecification<E>(params.get(0));
//			for (int i = 1; i < params.size(); i++) {
//				result = params.get(i).isOrPredicate()
//						? Specification.where(result).or(new SearchSpecification<E>(params.get(i)))
//						: Specification.where(result).and(new SearchSpecification<E>(params.get(i)));
//			}
//		}
		// add any specifications .
		if (!specifications.isEmpty()) {
			result =  specifications.get(0) ;
			for (int i = 1; i < specifications.size(); i++) {
			     if (specifications.get(i) instanceof SearchSpecification<?> 
			          && ((SearchSpecification<E>)specifications.get(i)).getCriteria().get(0).getLogicOperator()== LogicOperator.OR)
			       result = result.or(specifications.get(i));
			     else
			       result = result.and(specifications.get(i));
			}
		}
		if (result == null) // no parameters add a false criteria
			result = new SearchSpecification<E>();

		return result;
	}

	/**
	 * returns the mapping of a field key. if no mapping exists, the field key is
	 * returned
	 * 
	 * @param fieldKey
	 * @return actual field name to filter on
	 */
	private String getFieldMapping(String fieldKey) {
		if (fieldsMapping == null || !fieldsMapping.containsKey(fieldKey))
			return fieldKey;
		return fieldsMapping.get(fieldKey);
	}
  
}