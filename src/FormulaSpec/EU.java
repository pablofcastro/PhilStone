package FormulaSpec;

import java.util.LinkedList;

public class EU extends TemporalFormula {
	//Negation auxForm; // we use the following equality E(p U q) = !A(!q W !p ^ !q)

	public EU(Formula e1, Formula e2){
        super(e1,e2);	
        //auxForm = new Negation(new AW(new Negation(e2), new Conjunction(new Negation(e1), new Negation(e2))));
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
		return "E["+ this.getExpr1().toString() + "U"+ this.getExpr2().toString() +"]";
	}
	
	public String getAuxPred(String modelName){
		String next = "(some s':(^(succs_Form"+this.getId()+"["+modelName+"]))[s] | "+ this.getExpr1().toAlloy(modelName, "s'")+")";
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n "+ next + "}";
		return result;
	}
	
	public String getAuxSucc(String modelName){
		String sig = "fun succs"+this.getId()+"(m:"+modelName+"):Node -> Node";
		String body = "{\n n:m.nodes+, n':+m.nodes | (n->n' in m.succs) and "+ this.getExpr1().toAlloy("m", "n") +"}";
		return sig + body;
	}
	
	public LinkedList<String> generatePreds(String modelName){
		LinkedList<String> result = new LinkedList<String>();
		result.add(this.getAuxPred(modelName));
		result.add(this.getAuxSucc(modelName));
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		return result;
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances) || this.getExpr2().containsVarOwnedBy(instances);
	}
	
	public Formula removeVarOwnedBy(LinkedList<String> instances){
		return new EU(this.getExpr1().removeVarOwnedBy(instances), this.getExpr2().removeVarOwnedBy(instances));
	}
	
	
}
