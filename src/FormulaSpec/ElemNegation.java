package FormulaSpec;

import java.util.LinkedList;

/**
 * A elementary negation is the negation of a elementary formula
 * @author Pablo
 *
 */
public class ElemNegation implements ElemFormula{
	ElemFormula f;
	
	public ElemNegation(ElemFormula f){
		this.f = f;
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		
	
	public String toAlloy(String metaName, String state){
		String result =  "not " + f.toAlloy(metaName,state);
		return result;
	}
	
	public boolean usesVar(String name){
		return f.usesVar(name);
	}
	
	public String toString(){
		return "! ("+ f.toString()+")";
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new ElemNegation((ElemFormula) f.removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return f.containsVarOwnedBy(instances);
	}
}
