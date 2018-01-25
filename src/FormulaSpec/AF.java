package FormulaSpec;

import java.util.LinkedList;

public class AF extends TemporalFormula{
	private EG auxForm; // an auxiliar formula EG is used for translating to Alloy
	
	public AF(Formula e1){
        super(e1,null);	
        auxForm = new EG(new Negation(e1));
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
		return "A(true U "+ this.getExpr1().toString() + ")";
	}
	
	public EG getAuxForm(){
		return auxForm;
	}

	public String getAuxPred(String modelName){
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n not ("+ auxForm.toAlloy("i", "s")+")}";
		return result;	
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxForm().getAuxPred(modelName)); // we dont need to apply recursion here since all the formula in it are in the  main formula
		result.add(this.getAuxForm().getAuxSucc(modelName));
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		return result;
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new AF(this.getExpr1().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances);
	}
	
	public String getAuxSucc(String modelName){
		return auxForm.getAuxSucc(modelName);
	}
}
