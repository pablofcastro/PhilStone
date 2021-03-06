package FormulaSpec;

import java.util.LinkedList;

public class BoolPar implements Formula, Var{
	private String name;
	private BoolVar myVar; // the var which the parameter references to
	
	public BoolPar(String name){
		this.name = name;
	}

	public BoolPar(String name, BoolVar myVar){
		this.name = name;
		this.myVar = myVar;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setVar(BoolVar myVar){
		this.myVar = myVar;
	}
	
	public String getName(){
		return this.name;
	}
	
	public BoolVar getVar(){
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
		if (!myVar.isPrimType())
			return Type.BOOL;
		else
			return Type.PRIMBOOL;
	}
	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	public boolean usesVar(String name){
		return this.name.equals(name);			
	}
	
	public String getUnqualifiedName(){
		return myVar.getUnqualifiedName();
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
		return this;
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
	
}


