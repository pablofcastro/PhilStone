package FormulaSpec;

import java.util.LinkedList;

public class EX extends TemporalFormula {

	public EX(Formula e1){
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
	
	public String getAuxPred(String modelName){
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n some s':(i.succs)[s] | "+this.getExpr1().toAlloy(modelName,"s")+"}";
		return result;
	}
	
	public String toString(){
		return "EX["+ this.getExpr1().toString() + "]";
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		return result;
	}
	
	
}
