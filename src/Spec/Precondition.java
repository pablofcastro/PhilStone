package Spec;
import java.util.*;

import FormulaSpec.*;

/**
 * A Class for modeling preconditions of actions
 * @author Pablo
 *
 */
public class Precondition {
	private LinkedList<Clause> formulas; // a precondition is given by a collection of clauses DNF

	public Precondition(){
		this.formulas = new LinkedList<Clause>();
	}

	public void addFormula(Clause f){
		formulas.add(f);
	}
	
	public void addAllFormulas(LinkedList<Clause> f){
		formulas.addAll(f);
	}
	
	/**
	 * 
	 * @return	a textual description of the precondition in Alloy notation
	 */
	public String toAlloy(String metaName, String state){
		String result = "(";
		for (int i=0; i <formulas.size(); i++){
			if (i==0)
				result = result + "(" + formulas.get(i).toAlloy(metaName, state) + ")";
			else
				result = result + " or "+ "(" + formulas.get(i).toAlloy(metaName, state) + ")";
		}
		result = result + ")";
		return result;
	}
	
	public String toString(){
		String result = "Pre: ";
		for (int i = 0; i<formulas.size(); i++){
			if (i == 0)
				result += formulas.get(i).toString();
			else
				result += "||" + formulas.get(i).toString();
		}
		return result;
	}
}
