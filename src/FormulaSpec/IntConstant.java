package FormulaSpec;

public class IntConstant extends TemporalFormula {
	//private String name;
	private int value; 
	
	public IntConstant(int v){
		super(null, null);
		//name = s;
		value =v;
	}
	
		
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);		 
	 }	
		
	@Override
    public String toString(){    	
		Integer i = new Integer(value);
		return i.toString();
    };
    
    /***
     * 
     * @return Return the boolean value of the constant.
     */
    public int getValue(){
		 return value;		 
	}	
    
    public String toAlloy(String metaName, String state){
    	Integer i = new Integer(value);
		return i.toString();
    }

}
