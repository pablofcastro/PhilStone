package FormulaSpec;

import java.util.LinkedList;

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
    
    public boolean usesVar(String name){
    	return this.name.equals(name);  
    }
    
    public String getOwner(){
    	String result = name;
    	for (int i=0; i<name.length(); i++){
    		if (result.charAt(i) == '.'){
    			result = result.substring(0, i);
    			break;
    		}
    	}
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
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
		if (instances.contains(this.getOwner()))
			return new BoolConstant(true);
		else
			return this;
	}
}
