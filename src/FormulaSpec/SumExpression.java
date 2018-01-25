package FormulaSpec;

import java.util.LinkedList;

public class SumExpression extends AritExpression{
	
	public SumExpression(AritExpression e1, AritExpression e2){
        super(e1,e2);		
	}
    
	public void accept(IntegerVisitor v){
		v.visit(this);
	}
 
	public String toAlloy(String metaName, String state){
		String result =  this.getExpr1().toAlloy(metaName,state) + " + " + this.getExpr2().toAlloy(metaName,state);
		return result;
	}
	
	public boolean usesVar(String name){
		return (this.getExpr1().usesVar(name) || this.getExpr2().usesVar(name));
	}
	
	public String toString(){
		return this.getExpr1().toString() + "+" + this.getExpr2().toString(); 
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.getExpr1().containsVarOwnedBy(instances) || this.getExpr2().containsVarOwnedBy(instances);
	}
	
	
}
