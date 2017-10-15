package FormulaSpec;

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
	
	public String toString(){
		return this.getExpr1().toString() + "+" + this.getExpr2().toString(); 
	}
}
