package FormulaSpec;

public abstract class AritExpression implements IntegerExpression {

	private AritExpression expr1;
	private AritExpression expr2;
	
	public AritExpression(AritExpression e1, AritExpression e2){
		expr1 = e1;
		expr2 = e2; 	
	}
	
	
	//@Override
    //public abstract  String toString();
	
	 /***
     * @return the first expression.
     */
    public AritExpression getExpr1(){
    	return this.expr1;
    	
    }
    
    /**
     * @return	the second expression
     */
    public AritExpression getExpr2(){
    	return this.expr2;
    }
    
    public abstract void accept(IntegerVisitor v);
	
	
}
