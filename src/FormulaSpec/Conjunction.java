package FormulaSpec;

import java.util.LinkedList;

public class Conjunction extends TemporalFormula {
	
	public Conjunction(Formula e1, Formula e2){
		super(e1, e2);
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}
	
	public String toAlloy(String metaName, String state){
		String result = "("+this.getExpr1().toAlloy(metaName, state)+")" + " and " +"(" + this.getExpr2().toAlloy(metaName, state) + ")";
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String toString(){
		return "("+this.getExpr1().toString()+")" + " & " + "("+this.getExpr2().toString()+")"; 
	}
	
	public String getAuxPred(String modelName){
		String result = "";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
    	LinkedList<String> result = new LinkedList<String>();
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		if (this.getExpr2() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr2()).generatePreds(modelName));
    	return result;
    }
}
