package FormulaSpec;

import java.util.LinkedList;

public class AW extends TemporalFormula {
	private EU auxForm;
	
	
	public AW(Formula e1, Formula e2){
        super(e1,e2);	
        auxForm = new EU(new Negation(e2), new Conjunction(new Negation(e1), new Negation(e2)));
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		String result = "Form"+this.getId()+"["+metaName+","+state+"]";
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String toString(){
		return "A("+ this.getExpr1().toString() + "W"+ this.getExpr2().toString() +")";
	}
	
	public String getAuxPred(String modelName){
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n not ("+ auxForm.toAlloy(modelName, "s")  +")}";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.auxForm.getAuxPred(modelName)); // we dont need to apply recursion here since all the formula in it are in the  main formula
		result.add(this.auxForm.getAuxSucc(modelName));
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		if (this.getExpr2() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr2()).generatePreds(modelName));
		return result;
	}
	
	public String getAuxSucc(String modelName){
		return auxForm.getAuxSucc(modelName);
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new AW(this.getExpr1().removeVarOwnedBy(instances), this.getExpr2().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances) || this.getExpr2().containsVarOwnedBy(instances);
	}
}
