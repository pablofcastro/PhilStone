package FormulaSpec;

import java.util.LinkedList;

public class NegExpression extends AritExpression {
	public NegExpression(AritExpression e1){
        super(e1,null);		
	}
    
	public void accept(IntegerVisitor v){
		v.visit(this);
	}
	
	public boolean usesVar(String name){
		return this.getExpr1().usesVar(name);
	}
	
	public String toAlloy(String metaName, String state){
		String result = "- " + this.getExpr1().toAlloy(metaName,state);
		return result;
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances);
	}
}
