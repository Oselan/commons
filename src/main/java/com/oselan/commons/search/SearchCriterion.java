package com.oselan.commons.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class SearchCriterion {

  @Builder.Default
  private LogicOperator logicOperator = LogicOperator.AND;
  
  private String key;

  private SearchOperator operation;

  private Object value;
 
  @Builder.Default
  private boolean ignoreCase = true;

  public SearchCriterion() {

  }

  /***
   * Build a search criteria of key operation of value
   * @param key
   * @param operation
   * @param value
   */
  public SearchCriterion(String key, SearchOperator operation, Object value) {
    this(null, key, operation, value);
  }

  /** 
   * @param joinOperator
   * @param key
   * @param operation
   * @param value
   */
  public SearchCriterion(LogicOperator logicOperator, String key, SearchOperator operation, Object value) {
    this.logicOperator = logicOperator;
    this.key = key;
    this.operation = operation;
    this.value = value;
  }
 
}