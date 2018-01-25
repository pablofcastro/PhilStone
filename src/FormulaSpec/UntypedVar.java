package FormulaSpec;

import java.util.LinkedList;

public class UntypedVar implements Expression {
	String name;
	
	public UntypedVar(String name){
		this.name = name;
	}
	
	public IntVar toIntVar(){
		IntVar result = new IntVar(this.name,0);
		return result;
	}
	
	public boolean usesVar(String name){
		return this.name.equals(name);
	}
	
	public String toAlloy(String metaName, String state){
		return name;
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
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
}
