package FormulaSpec;

import java.util.LinkedList;

public class AX extends TemporalFormula {
	
	public AX(Formula e1){
        super(e1,null);		
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
		return this.getExpr1().usesVar(name);
	}
	
	public String toString(){
		return "AX["+ this.getExpr1().toString() + "]";
	}
	
	public String getAuxPred(String modelName){
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n all s':(i.succs)[s] | "+this.getExpr1().toAlloy(modelName,"s'")+"}";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula){
			result.addAll(((TemporalFormula) this.getExpr1()).generatePreds(modelName));
		}
		return result;
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new AX(this.getExpr1().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances);
	}
}
