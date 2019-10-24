package FormulaSpec;

import java.util.LinkedList;

public class EnumPar implements EnumExpression, Var{

	private String name;
	private EnumVar myVar; // the var which the parameter references to
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
	   // useful for StringTemplate :(
	
	public EnumPar(String name){
		this.name = name;
		this.otherVars = new LinkedList<Var>();
	}

	public EnumPar(String name, EnumVar myVar){
		this.name = name;
		this.myVar = myVar;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void addOtherVars(LinkedList<Var> others){
		this.otherVars.addAll(others);
	}
	
	public void addOtherVariable(Var other){
		if (!this.otherVars.contains(other))
			this.otherVars.add(other);
	}
	
	public void setVar(EnumVar myVar){
		this.myVar = myVar;
	}
	
	public String getName(){
		return this.name;
	}
	
	public EnumVar getVar(){
		return myVar;
	}
	
	public boolean isPrimType(){
		return this.myVar.isPrimType();
	}
	
	public void setIsPrim(boolean b){
		this.myVar.setIsPrim(b);
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
		return myVar.toAlloy(metaName, state);
	}
	
	public Type getType(){
		if (!this.myVar.isPrimType())
			return Type.ENUM;
		else
			return Type.ENUMPRIM;
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
	
	public void accept(EnumVisitor visitor){
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