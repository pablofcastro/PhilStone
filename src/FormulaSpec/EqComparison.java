package FormulaSpec;

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
	
    public String toAlloy(String metaName, String state){
    	String result = exp1.toAlloy(metaName,state) + " = "  + exp2.toAlloy(metaName,state);
    	return result;
    }

}
