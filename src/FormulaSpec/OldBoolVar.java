package FormulaSpec;

import java.util.LinkedList;

public class OldBoolVar implements Formula, Var{
	private String name;
	private boolean isPrim;
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
	   // useful for StringTemplate :(

	
	public OldBoolVar(String n){
		this.name = n;
		this.isPrim = false;
		this.otherVars = new LinkedList<Var>();
	}
	
	public void setIsPrimtType(boolean b){
		this.isPrim = b;
	}
	
	public boolean isPrimType(){
		return this.isPrim;
	}
	
	public void setIsPrim(boolean b){
		this.isPrim = b;
	}
	
	public void addOtherVars(LinkedList<Var> others){
		this.otherVars.addAll(others);
	}
	
	public void addOtherVariable(Var other){
		if (!this.otherVars.contains(other))
			this.otherVars.add(other);
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
    	if (!this.isPrimType())
			return Type.BOOL;
		else
			return Type.PRIMBOOL;
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
    
    public LinkedList<String> getOtherPrimsBooleanNames(){
    	LinkedList<String> result = new LinkedList<>();
    	for (Var v:this.otherVars){
    		if (v.getType() == Type.PRIMBOOL && !v.getName().equals(this.name))
    			result.add(v.getName());
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
