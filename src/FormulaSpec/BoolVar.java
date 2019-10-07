package FormulaSpec;

import java.util.LinkedList;

public class BoolVar implements ElemFormula, Var {
	
	private String name;
	private boolean isPrim;
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
									   // useful for StringTemplate :(
	
	public BoolVar(String n){
		this.name = n;
		this.isPrim = false; // by default the vars are not prim
		this.otherVars = new LinkedList<Var>();
	}
	
	
	public void setIsPrim(boolean v){
		this.isPrim = v;
	}
	
	public void addOtherVars(LinkedList<Var> others){
		this.otherVars.addAll(others);
	}
	
	public void addOtherVariable(Var other){
		if (!this.otherVars.contains(other))
			this.otherVars.add(other);
	}
	
	public boolean isPrimType(){
		return this.isPrim;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	@Override
    public String toString(){    	
    	return (this.getOwner()+".Prop_"+getUnqualifiedName());
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
    
    public LinkedList<String> getOtherPrimsBooleanNames(){
    	LinkedList<String> result = new LinkedList<>();
    	for (Var v:this.otherVars){
    		if (v.getType() == Type.PRIMBOOL && !v.getName().equals(this.name))
    			result.add(v.getName());
    	}
    	return result;
    }
    
    public String toAlloy(String metaName, String state){
    	// TO DO: We should distinghuish between global and local vars
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
    
    public boolean usesVar(String name){
		return this.getUnqualifiedName().equals(name);			
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
    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
		if (instances.contains(this.getOwner()))
			return new BoolConstant(true);
		else
			return this;
	}
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
}
