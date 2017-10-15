package FormulaSpec;

public class OldBoolVar implements Formula, Var{
	private String name;

	
	public OldBoolVar(String n){
		name = n;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);	 
	 }
	
	@Override
    public String toString(){    	
    	return "old "+ name;
	}
    	
    public String getName(){
    	return name;
    }
    
    public Type getType(){
    	return Type.BOOL;
    }

    public String toAlloy(String metaName, String state){
    	String result = "Prop_"+name+"[metaname,state]";
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
