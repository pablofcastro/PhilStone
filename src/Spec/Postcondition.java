package Spec;

import java.util.LinkedList;

import FormulaSpec.Clause;

/**
 * A postcondition is written in CNF, this improves the translation to Alloy
 * @author Pablo
 *
 */

public class Postcondition {
	private LinkedList<Clause> formulas; // a precondition is given by a collection of clauses DNF

	public Postcondition(){
		this.formulas = new LinkedList<Clause>();
	}

	public void addFormula(Clause f){
		formulas.add(f);
	}
	
	
	public void addAllFormulas(LinkedList<Clause> fs){
		formulas.addAll(fs);
	}
	
	/**
	 * 
	 * @return	a textual description of the precondition in Alloy notation
	 */
	public String toAlloy(String metaName, String state){
		String result = "(";
		for (int i=0; i<formulas.size(); i++){
			if (i==0)
				result = result + "(" +formulas.get(i).toAlloy(metaName, state) + ")";
			else
				result = result + "or "+ "(" + formulas.get(i).toAlloy(metaName, state) + ")";
		}
		result = result + ")";
		return result;
	}
	
	/**
	 * This getter is needed for producing the alloy template
	 * @return	the list of clauses
	 */
	public LinkedList<Clause> getClauses(){
		return formulas;
	}
	
	public String toString(){
		String result = "Post: ";
		for (int i = 0; i<formulas.size(); i++){
			if (i == 0)
				result += formulas.get(i).toString();
			else
				result += "||" + formulas.get(i).toString();
		}
		return result;
	}
}
