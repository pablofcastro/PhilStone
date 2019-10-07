package FormulaSpec;

import java.util.LinkedList;

public class IntPar implements IntegerExpression, Var{

	private String name;
	private IntVar myVar; // the var which the parameter references to
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
	   // useful for StringTemplate :(
	
	public IntPar(String name){
		this.name = name;
		this.otherVars = new LinkedList<Var>();
	}
	

	public IntPar(String name, IntVar myVar){
		this.name = name;
		this.myVar = myVar;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setVar(IntVar myVar){
		this.myVar = myVar;
	}
	
	public void addOtherVars(LinkedList<Var> others){
		this.otherVars.addAll(others);
	}
	
	public void addOtherVariable(Var other){
		if (!this.otherVars.contains(other))
			this.otherVars.add(other);
	}
	
	public String getName(){
		return this.name;
	}
	
	public IntVar getVar(){
		return myVar;
	}
	
	public boolean isPrimType(){
		return this.myVar.isPrimType();
	}
	
	public void setIsPrim(boolean b){
		this.myVar.setIsPrim(b);
	}
	
	public String toAlloy(String metaName, String state){
		return myVar.toAlloy(metaName, state);
	}
	
	public Type getType(){
		if (!this.myVar.isPrimType())
			return Type.INT;
		else
			return Type.PRIMINT;
	}
	
	public LinkedList<String> getOtherPrimsBooleanNames(){
    	LinkedList<String> result = new LinkedList<>();
    	for (Var v:this.otherVars){
    		if (v.getType() == Type.PRIMBOOL && !v.getName().equals(this.name))
    			result.add(v.getName());
    	}
    	return result;
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
	
	public void accept(IntegerVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	public boolean usesVar(String name){
		return this.name.equals(name);			
	}
	
	public String getUnqualifiedName(){
		return myVar.getUnqualifiedName();
	}
	
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
	
	
}
