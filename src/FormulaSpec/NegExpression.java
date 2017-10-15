package FormulaSpec;

public class NegExpression extends AritExpression {
	public NegExpression(AritExpression e1){
        super(e1,null);		
	}
    
	public void accept(IntegerVisitor v){
		v.visit(this);
	}
	
	public String toAlloy(String metaName, String state){
		String result = "- " + this.getExpr1().toAlloy(metaName,state);
		return result;
	}
	
}
