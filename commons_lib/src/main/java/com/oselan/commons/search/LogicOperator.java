
package com.oselan.commons.search;
/***
 * Criteria join operator
 * @author Ahmad Hamid
 *
 */
public enum LogicOperator {

  AND(","), OR("|");

  private String symbol;

  private LogicOperator(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public static final String[] LOGIC_OPERATION_SYMBOLS = { AND.symbol, OR.symbol };

  /***
   * Checks if the input operator is alogical operator and either AND or OR
   * 
   * @param input
   * @return
   */
  public static LogicOperator getLogicOperatorBySymbol(final String input) {
    switch (input) {
    case "|":
      return OR;
    case ",":
      return AND;
    default:
      return null;
    }
  }

}