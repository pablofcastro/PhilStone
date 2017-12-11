package FormulaSpec;

import java.util.LinkedList;

public class EG extends TemporalFormula {
	public EG(Formula e1){
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
		return "EG["+ this.getExpr1().toString() + "]";
	}
	
	
	/**
	 * Produces the Alloy code for this formula, it assumes that fun succs_param1 is present
	 * this can be generated with a private method in the class TemporalFormula
	 * @param modelName 	the name of the current model
	 * @return	A String with the predicate
	 */
	public String getAuxPred(String modelName){
		String findCycle = "(some s':(^(succs_Form"+this.getId()+"["+modelName+"]))[s] | s' in (^(succs_Form"+this.getId()+"["+modelName+"]))[s']";
		String result = "pred Form"+this.getId()+"[i:"+modelName+", s:Node]{\n "+ findCycle + "}";
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
		result.addFirst(this.getAuxSucc(modelName));;
		if (this.getExpr1() instanceof TemporalFormula)
			result.addAll(((TemporalFormula)this.getExpr1()).generatePreds(modelName));
		return result;
	}
}
