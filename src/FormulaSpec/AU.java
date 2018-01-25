package FormulaSpec;

import java.util.LinkedList;

public class AU extends TemporalFormula {
	private AF auxForm1; // we use this for the semantics
	private AW auxForm2;
	
	public AU(Formula e1, Formula e2){
        super(e1,e2);
        this.auxForm1 = new AF(e1);
        this.auxForm2 = new AW(e1, e2);
	}
	public AF getAuxForm1(){
		return auxForm1;
	}
	
	public AW gerAuxForm2(){
		return auxForm2;
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	//public String toAlloy(String metaName, String state){
	//	String result = "A(" + this.getExpr1().toAlloy(metaName, state) + "U" + this.getExpr2().toAlloy(metaName,state) + ")";
	//	return result;
	//}
	
	public String toAlloy(String metaName, String state){
		//String f = this.getExpr1().toAlloy(metaName, "s'");
		//String result = "some s':(*("+metaName+".succs)["+state+"]) | " + f;
		String result = "Form"+this.getId()+"["+metaName+","+state+"]";
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String toString(){
		return "A["+ this.getExpr1().toString() + "U"+ this.getExpr2().toString() +"]";
	}
	
	public String getAuxPred(String modelName){
		//String inf =  auxForm.toAlloy(modelName, "s");
		//String f   = "("+this.getExpr2().toAlloy(modelName,"s")+" or (all s':i.succs | Prop_Form"+this.getId()+"[i,s']))";
		String f1 = auxForm1.getAuxPred(modelName);
		String f2 = auxForm2.getAuxPred(modelName);
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n "+f1+" and "+f2+" )}";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.auxForm1.getAuxPred(modelName)); // we dont need to apply recursion here since all the formula in it are in the  main formula
		result.add(this.auxForm1.getAuxSucc(modelName));
		result.add(this.auxForm1.getAuxPred(modelName));
		result.add(this.auxForm2.getAuxSucc(modelName));
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		if (this.getExpr2() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr2()).generatePreds(modelName));
		return result;
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new AU(this.getExpr1().removeVarOwnedBy(instances), this.getExpr2().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances) || this.getExpr2().containsVarOwnedBy(instances);
	}
	
}
