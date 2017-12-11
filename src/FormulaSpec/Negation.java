package FormulaSpec;

import java.util.LinkedList;

public class Negation extends TemporalFormula {
	
	public Negation(Formula e1){
		super(e1, null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		

	public String toAlloy(String metaName, String state){
		String result = "not (" + this.getExpr1().toAlloy(metaName,state) + ")";
		return result;
	}
	
	public boolean usesVar(String name){
		return this.getExpr1().usesVar(name);
	}
	
	public String getAuxPred(String modelName){
		String result = "";
		return result;
	}
	
	public String toString(){
		return "!" + "(" + this.getExpr1().toString() + ")";
	}
	
	public LinkedList<String> generatePreds(String modelName){
    	LinkedList<String> result = new LinkedList<String>();
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
    	return result;
    }
}
