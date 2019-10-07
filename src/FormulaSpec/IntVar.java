package FormulaSpec;

import java.util.LinkedList;

public class IntVar implements IntegerExpression, Var{
	private String name;
	private int value;
	private boolean isPrim;
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
	   // useful for StringTemplate :(
	
	public IntVar(String name, int value){
		this.name = name;
        this.value = value;
        this.isPrim = false;
        this.otherVars = new LinkedList<Var>();
	}
	
	public IntVar(String name){
		this.name = name;
        this.value = 0;
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
		return isPrim;
	}
	
	public void accept(IntegerVisitor v){
		v.visit(this);
	}
	
	public void setValue(int i){
		this.value = i;
	}
	
	public int getValue(){
		return value;
	}
	
	public String getName(){
		return name;
	}

	public Type getType(){
		if (!this.isPrimType())
			return Type.INT;
		else
			return Type.PRIMINT;
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
	
	public String toAlloy(String metaName, String state){
		// we use function expressions to capture ints in Alloy
		String result = "Val_"+this.getUnqualifiedName()+"["+metaName+","+state+"]:Int";
    	return result;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean usesVar(String name){
		return this.getUnqualifiedName().equals(name);			
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
    		}
    	}
    	return result;	
    }
	
	
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
}
