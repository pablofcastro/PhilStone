package FormulaSpec;

public class BoolConstant extends TemporalFormula {
	//private String name;
	private boolean value; 
	
	public BoolConstant(boolean v){
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
		if (value)
    		return "true";
    	else
    		return "false";
    };
    
    /***
     * 
     * @return Return the boolean value of the constant.
     */
    public boolean getValue(){
		 return value;		 
	}	
    
    public boolean usesVar(String name){
		return false;
	}
    
    public String toAlloy(String metaName, String state){
    	if (value)
    		return "true";
    	else
    		return "false";
    }
}
