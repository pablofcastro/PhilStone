package FormulaSpec;

import java.util.LinkedList;

public class EnumConstant implements EnumExpression {
	//private String name;
	private String value; 
	
	public EnumConstant(String v){
		//super(null, null);
		//name = s;
		value = v;
	}
	
		
	@Override	
	public void accept(EnumVisitor visitor){
		 visitor.visit(this);		 
	 }	
		
	@Override
    public String toString(){    	
		//Integer i = new Integer(value);
		return value;
    };
    
    /***
     * 
     * @return Return the boolean value of the constant.
     */
    public String getValue(){
		 return value;		 
	}	
    
    public boolean usesVar(String name){
    	return false;
    }
    
    public String toAlloy(String metaName, String state){
		return value;
    }
    
    public String getAuxPred(String modelName){
		String result = "";
		return result;
	}
    
    public LinkedList<String> generatePreds(String modelName){
    	LinkedList<String> result = new LinkedList<String>();
    	return result;
    }
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return false;
	}
    


}