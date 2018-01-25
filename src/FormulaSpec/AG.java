package FormulaSpec;

import java.util.LinkedList;

public class AG extends TemporalFormula{
	public AG(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		//String result = "all s:"+metaName+".nodes | all s':(*"+metaName+".succs)[s] ";
		//String f = this.getExpr1().toAlloy(metaName, "s'");
		//String result = "all s':(*("+metaName+".succs)["+state+"]) | " + f;	
		String result = "Form"+this.getId()+"["+metaName+","+state+"]";
		return result;
	}
	
	public String getAlloy(){
		String result = "all s:<name>Meta.nodes | all s':(<name>Meta.*succs)[s]";
		return result;
	}
	
	public String getAlloyAux(String state, String modelName){
		return "";
	}
	
	public boolean usesVar(String name){
		return this.getExpr1().usesVar(name);
	}
	
	public String toString(){
		return "A("+ this.getExpr1().toString() + "W false)";
	}
	
	public String getAuxPred(String modelName){
		String f = this.getExpr1().toAlloy(modelName, "s'");
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n all s':(*("+modelName+".succs)[s]) | " + f + " }";
		return result;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxPred(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		return result;
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new AG(this.getExpr1().removeVarOwnedBy(instances));
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances);
	}
}
