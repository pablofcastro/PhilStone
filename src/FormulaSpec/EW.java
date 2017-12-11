package FormulaSpec;

import java.util.LinkedList;

public class EW extends TemporalFormula {
	EU auxForm1;
	EG auxForm2;
	
	public EW(Formula e1, Formula e2){
        super(e1,e2);
        auxForm1 = new EU(e1, e2);
        auxForm2 = new EG(e1);
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
		return "E["+ this.getExpr1().toString() + "W"+ this.getExpr2().toString() +"]";
	}
	
	public String getAuxPred(String modelName){
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n ("+this.getExpr1().toAlloy(modelName,"s")+") or ("+this.getExpr1().toAlloy(modelName,"s")+")}";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxPred(modelName));
		result.add(auxForm1.getAuxPred(modelName));
		result.add(auxForm2.getAuxPred(modelName));
		result.add(auxForm1.getAuxSucc(modelName));
		result.add(auxForm2.getAuxSucc(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		if (this.getExpr2() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr2()).generatePreds(modelName));
		return result;
	}
}
