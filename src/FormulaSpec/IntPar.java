package FormulaSpec;

import java.util.LinkedList;

public class IntPar implements IntegerExpression, Var{

	private String name;
	private IntVar myVar; // the var which the parameter references to
	
	public IntPar(String name){
		this.name = name;
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
	
	public String getName(){
		return this.name;
	}
	
	public IntVar getVar(){
		return myVar;
	}
	
	public String toAlloy(String metaName, String state){
		return myVar.toAlloy(metaName, state);
	}
	
	public Type getType(){
		return Type.INT;
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
