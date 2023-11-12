package com.oselan.commons.search;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.NoArgsConstructor;

 
@NoArgsConstructor 
public  class SearchSpecification<T >  implements Specification<T> {

  

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<SearchCriterion> criteria = new ArrayList<>() ;
 
	
	public SearchSpecification(final SearchCriterion criterion) {
		super();
		this.criteria.add( criterion);
	}
	
	public SearchSpecification(final SearchCriterion ... criteria) {
      super();
      this.criteria.addAll(Arrays.asList(criteria));
    }

	public List<SearchCriterion> getCriteria() {
		return criteria;
	}
 
	/**
	 * find the expression to filter either on the main class or if a join (or
	 * nested joins) if needed with other classes. this expression is passed to the
	 * toPredicate common method along with @param root and @param query
	 *
	 */
	@Override
	public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
	  
	    //create a new predicate list
        Stack<Predicate> predicates = new Stack<>();
        LogicOperator currLogicOperator = null;
 
        Predicate combinedPredicated = builder.conjunction();
        for (SearchCriterion criterion : criteria)
        {
      		if ( criterion.getOperation() != SearchOperator.ISNULL
      				&& criterion.getOperation() != SearchOperator.ISNOTNULL && criterion.getValue() == null)
      			return builder.conjunction(); // true if there are not parameters
      		//LEFT join on any subentities e.g. company.contact.name creates a left join between company and contact.
      		final String ENTITY_SEPARATOR = ".";
      		Predicate predicate;
      		if ( criterion.getKey().contains(ENTITY_SEPARATOR)) {
      			String key =  criterion.getKey();
      			String subentities = key.substring(0, key.lastIndexOf(ENTITY_SEPARATOR));
      			String fieldKey = key.substring(key.lastIndexOf(ENTITY_SEPARATOR) + 1);
       
      			String[] subEntitiesSplitted = subentities.split("\\" + ENTITY_SEPARATOR);
      			Join<Object, Object> joinParent = root.join(subEntitiesSplitted[0], JoinType.LEFT);
      			if (subEntitiesSplitted.length > 1) {
      				for (int i = 1; i < subEntitiesSplitted.length; i++) {
      					joinParent = joinParent.join(subEntitiesSplitted[i], JoinType.LEFT);
      				}
      			}
      			predicate = toPredicate(root, query, builder, joinParent.get(fieldKey),criterion);
      		} else {
      		    predicate = toPredicate(root, query, builder, root.get(criterion.getKey()),criterion);
      		}
      		/**
      		 * Add first and second critera predicates to the stack, on second save the logic operator. 
      		 * keep adding criteria as long as the operators are the same, all OR or AND
      		 * Once operator changes or we reach the end of the list
      		 * build a predicate from all predicates using the prevous operator 
      		 * */
      		if (predicates.size() <=1 || currLogicOperator==criterion.getLogicOperator()) 
      		{    predicates.push(predicate);
      		     if (predicates.size() ==2 ) //set the current operator.
      		         currLogicOperator = criterion.getLogicOperator();
      		}
      		else //operator changed. 
      		{  
      		   //pop all prev predicates and combine them  
      		   combinedPredicated = (currLogicOperator == LogicOperator.OR) ?
      		              builder.or(predicates.toArray(new Predicate[predicates.size()])) : 
      		              builder.and(predicates.toArray(new Predicate[predicates.size()]));
      		   predicates.clear();
      		   predicates.push(combinedPredicated);//push prev predicate combind back 
      		   predicates.push(predicate); //push next predicate. 
      		   currLogicOperator = criterion.getLogicOperator(); //reset the current operator
      		}
      	 
          }
          if (predicates.size()>1)
          { 
            combinedPredicated = (currLogicOperator == LogicOperator.OR) ?
            builder.or(predicates.toArray(new Predicate[predicates.size()])) : 
            builder.and(predicates.toArray(new Predicate[predicates.size()]));
          }
          else if (predicates.size()==1)
            combinedPredicated = predicates.pop(); 
        return combinedPredicated;
	}	
	
	
	/**
	 * Creates a WHERE clause for a query of the referenced entity in form of a
	 * {@link Predicate} for the given {@link Root} and {@link CriteriaQuery}.
	 * value is adapted by adaptValue method to expression's field type
	 * 
	 * @param root
	 * @param query
	 * @param builder
	 * @param expression
	 * @return
	 */
 	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder, Path expression,SearchCriterion criterion) {
		Expression<? extends Object> exp = expression;
		Object value = null;
		if (criterion.getValue() != null) {
			if (expression.getJavaType().isAssignableFrom(String.class) && criterion.isIgnoreCase())
				exp = builder.lower(expression);
			value = adaptValue(expression.getJavaType(), criterion.getValue());

		}
		
		switch (criterion.getOperation()) {
		case EQUALITY:
			return builder.equal(  exp ,value);
		case NEGATION:
			return builder.notEqual(exp,value);
		case GREATER_THAN://<Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Y y);
			if (value instanceof Comparable )
				return builder.greaterThan(expression,(Comparable)  value );
			else 
				return builder.greaterThan(expression, value.toString() );
		case LESS_THAN: 
			if (value instanceof Comparable )
				return builder.lessThan(expression,(Comparable)  value );
			else 
				return builder.lessThan(expression, value.toString() );
		case GREATER_THAN_OR_EQUAL: 
			if (value instanceof Comparable )
				return builder.greaterThanOrEqualTo(expression,(Comparable)  value );
			else 
				return builder.greaterThanOrEqualTo(expression, value.toString() ); 
		case LESS_THAN_OR_EQUAL:
			if (value instanceof Comparable )
				return builder.lessThanOrEqualTo(expression,(Comparable)  value );
			else 
				return builder.lessThanOrEqualTo(expression, value.toString() );  
		case LIKE:
			return builder.like( (Expression<String>) exp,value.toString() );
		case STARTS_WITH:
			return builder.like( (Expression<String>) exp, value + "%");
		case ENDS_WITH:
			return builder.like( (Expression<String>) exp, "%" + value);
		case CONTAINS:
			return builder.like( (Expression<String>) exp, "%" + value + "%");
		case ISNULL:
			return builder.isNull(exp);
		case ISNOTNULL:
			return builder.isNotNull(exp);
		case IN:
			if (value.getClass().isArray()) {
				In<Object> in = builder.in(exp);
				Object[] arrayValue = (Object[]) value;

				for (int i = 0; i < arrayValue.length; i++) {
					in.value(arrayValue[i]);
				}
				return in;
			}
		default:
			return null;
		}
	}

 	/***
 	 * Adapts value or array of values from object to the field data type. 
 	 * @param <X>
 	 * @param fieldType
 	 * @param value
 	 * @return
 	 */
	@SuppressWarnings("unchecked")
	private <X> Object adaptValue(Class<X> fieldType, Object value) {
		if (value != null) {
			if (value.getClass().isArray()) {
				Object[] mappedArrayValue = null;
				Object[] arrayValue = (Object[]) value;
				mappedArrayValue = new Object[arrayValue.length];
				for (int i = 0; i < arrayValue.length; i++) {
					mappedArrayValue[i] = adaptValue(fieldType, arrayValue[i].toString());
				}
				return (X[]) mappedArrayValue;
			}
			return adaptValue(fieldType, value.toString());
		}

		return (X) value;

	}

	/**
 	 * common method to adapt value from string to the field data type. 
	 * @param <X>
	 * @param fieldType
	 * @param stringValue
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <X> Object adaptValue(Class<X> fieldType, String stringValue) {
		Object mappedValue = stringValue;
		if (stringValue != null) {
			if (fieldType.isAssignableFrom(Double.class) || fieldType.isAssignableFrom(double.class)) {
				mappedValue = Double.valueOf(stringValue);
			} else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
				mappedValue = Integer.valueOf(stringValue);
			} else if (fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)) {
				mappedValue = Long.valueOf(stringValue);
			} else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
				mappedValue = Boolean.valueOf(stringValue);
			} else if (fieldType.isAssignableFrom(LocalDate.class) || fieldType.isAssignableFrom(LocalDateTime.class)
					|| fieldType.isAssignableFrom(LocalTime.class))
				mappedValue = DateTimeConverter.mapStringToDate(fieldType, stringValue);
			else if (fieldType.isAssignableFrom(String.class)) {
				mappedValue = stringValue.toLowerCase();
			}
			else if (Enum.class.isAssignableFrom(fieldType))
			{ 
				mappedValue = Enum.valueOf((Class<? extends Enum>) fieldType, stringValue);
			}
		}
		return mappedValue;
	}
	
//	private Object castToRequiredType(Class fieldType, List<String> value) {
//	  List<Object> lists = new ArrayList<>();
//	  for (String s : value) {
//	    lists.add(castToRequiredType(fieldType, s));
//	  }
//	  return lists;
//	}
 
}