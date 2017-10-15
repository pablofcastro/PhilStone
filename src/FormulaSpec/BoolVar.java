package FormulaSpec;

public class BoolVar implements ElemFormula, Var {
	
	private String name;
	
	public BoolVar(String n){
		name = n;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	@Override
    public String toString(){    	
    	return name;
    }
    
    public String getName(){
    	return name;
    }
    
    public Type getType(){
    	return Type.BOOL;
    }
    
    public String toAlloy(String metaName, String state){
    	String result = "Prop_"+this.getUnqualifiedName()+"["+metaName+","+state+"]";
    	return result;
    }

    public String getUnqualifiedName(){
    	String result = name;
    	for (int i=0; i<name.length(); i++){
    		if (result.charAt(i) == '.'){
    			result = result.substring(i+1);
    			break;
    		}
    	}
    	return result;	
    }
}
