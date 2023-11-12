package com.oselan.commons.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A tree structure of criteria were nodes on the same level 
 * @author Ahmad Hamid
 *
 */
public class SearchCriteriaTree {
     
     private List<SearchCriteriaTree> criteria = new ArrayList<>() ;
     private SearchCriterion searchCriterion;
     
     public SearchCriteriaTree (SearchCriterion searchCriterion) {
       super();
       this.searchCriterion = searchCriterion; 
       criteria.add(new SearchCriteriaTree(this.searchCriterion));
     }
      
     
     public SearchCriteriaTree() {
      super(); 
    }

    public SearchCriteriaTree(SearchCriterion... searchCriteria) {
      super();
      this.searchCriterion = searchCriteria[0];
      criteria = Arrays.asList(searchCriteria).stream().map(criterion -> new SearchCriteriaTree(criterion)).collect(Collectors.toList());
    }
      
    /***
     * Adds criteria to this tree node
     * @param searchCriterion
     */
    public void addCriteria(SearchCriterion searchCriterion)
    {
       if (this.searchCriterion==null) 
         this.searchCriterion = searchCriterion;  
       else  
       {   //add the first critierion and the new one. 
           criteria.add(new SearchCriteriaTree(searchCriterion));
       }
       
     }
     
     /***
      * whether this criteria is a set of conditions
      * @return
      */
     public boolean isConditionSet()
     {
       return criteria.size() >1 ;
     }
     
     public List<SearchCriteriaTree> getCriteriaSet()
     {
       return criteria;
     }
     
     public SearchCriterion  getFirstCriterion()
     {
       return this.searchCriterion;
     }
     
     
}



