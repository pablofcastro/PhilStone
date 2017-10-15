package FormulaSpec;

public abstract class TemporalFormula implements Formula {

	private Formula expr1;
	private Formula expr2;
	
	public TemporalFormula(Formula e1, Formula e2){
		expr1 = e1;
		expr2 = e2; 	
	}
	
	//@Override
    //public abstract  String toString();
	
	 /***
     * @return the first expression.
     */
    public Formula getExpr1(){
    	return this.expr1;
    	
    }
    
    /**
     * @return	the second expression
     */
    public Formula getExpr2(){
    	return this.expr2;
    }
    

}
