
package com.oselan.commons.search;

public enum SearchOperator {
    EQUALITY(":"),
    IN(":"),
    NEGATION("!"),
    GREATER_THAN(">"), 
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL(">:"), 
    LESS_THAN_OR_EQUAL("<:"),
    LIKE("~"), 
    STARTS_WITH("~"),
    ENDS_WITH("~"),
    CONTAINS("~"),
    ISNULL(""), 
    ISNOTNULL("") ;

      private String symbol; 
      private SearchOperator(String symbol) {
        this.symbol = symbol;
      }

      public String getSymbol()
      {
        return symbol;
      }
      /* order is important for >= <= to work
       * GreaterOrEqual, LessOrEqual,Equal, notequal, Greater , less , like
       * In operator is same as equal but with an operand being a ; separate list. 
      */
	  public static final String[] OPERATION_SYMBOLS = { GREATER_THAN_OR_EQUAL.symbol,GREATER_THAN.symbol
	           ,LESS_THAN_OR_EQUAL.symbol,LESS_THAN.symbol,
	           EQUALITY.symbol,
	           NEGATION.symbol,
	           LIKE.symbol }; 
	   
	  public static final String WILD_CARD_SYMBOL = "*";

	  public static final String LEFT_PARANTHESIS = "(";

	  public static final String RIGHT_PARANTHESIS = ")";
	  
	  public static final String SET_SEPARATOR = ";";
	    

	  /***
	   * Translates the character to an operator
	   * @param operation
	   * @return
	   */
	  public static SearchOperator getOperatorBySymbol(final String operation ) {
	      switch (operation) {
	      case ":": 
	          return EQUALITY; 
	      case "!":
	          return NEGATION;
	      case ">":
	          return GREATER_THAN;
	      case "<":
	          return LESS_THAN;
	      case ">:":
	        return GREATER_THAN_OR_EQUAL;
	      case "<:":
	        return LESS_THAN_OR_EQUAL;
	      case "~":
	          return LIKE;
	      default:
	          return null;
	      }
	  }
	  
      public static SearchOperator getSubOperatorBySymbol(SearchOperator operation, String prefix, Object value, String suffix) {
        switch (operation) {
        case EQUALITY:
          if(value != null && value.toString().contains(SET_SEPARATOR))
            operation = IN;
          break;
        case LIKE:
          final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperator.WILD_CARD_SYMBOL);
          final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperator.WILD_CARD_SYMBOL);
          if (startWithAsterisk && endWithAsterisk) {
             operation = SearchOperator.CONTAINS;
          } else if (startWithAsterisk) {
             operation = SearchOperator.ENDS_WITH;
          } else if (endWithAsterisk) {
             operation = SearchOperator.STARTS_WITH;
          } 
          break;
        default:
          break;
        }
        return operation;
      } 
 
 }