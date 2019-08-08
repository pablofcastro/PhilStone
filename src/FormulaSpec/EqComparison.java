package FormulaSpec;

import java.util.LinkedList;

public class EqComparison implements ElemFormula {
	private Expression exp1;
	private Expression exp2;
      
	public EqComparison(Expression e1, Expression e2){
		//super(null,null);
		this.exp1 = e1;
		this.exp2 = e2;
	}
	
    public void accept(FormulaVisitor v){
		v.visit(this);		
	}

    public Expression getExp1(){
    	return this.exp1;
    	
    }
    
    public Expression getExp2(){
    	return this.exp2;
    	
    }
    
    public boolean usesVar(String name){
    	return (exp1.usesVar(name) || exp2.usesVar(name));
    }
	
    public String toAlloy(String metaName, String state){
    	String result = "("+exp1.toAlloy(metaName,state) + " = "  + exp2.toAlloy(metaName,state)+")";
    	return result;
    }
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return exp1.containsVarOwnedBy(instances) || exp2.containsVarOwnedBy(instances);
	}
    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
		if (this.exp1.containsVarOwnedBy(instances) || this.exp2.containsVarOwnedBy(instances))
			return new BoolConstant(true);
		else
			return this;
	}

}
