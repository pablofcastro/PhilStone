package FormulaSpec;

import java.util.LinkedList;

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
    
    public boolean usesVar(String name){
    	return false;
    }
    
    public String toAlloy(String metaName, String state){
    	Integer i = new Integer(value);
		return i.toString();
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
    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
		return this;
	}

}
