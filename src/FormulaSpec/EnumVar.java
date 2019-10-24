package FormulaSpec;
import java.util.LinkedList;
import Spec.*;


import java.util.LinkedList;

public class EnumVar implements EnumExpression, Var{
	private String name;
	private String value;
	private boolean isPrim;
	private EnumType myEnum;
	private LinkedList<Var> otherVars; // it is needed to keep track of the other vars in the spec
	   // useful for StringTemplate :(
	
	public EnumVar(String name, String value){
		this.name = name;
        this.value = value;
        this.isPrim = false;
        this.otherVars = new LinkedList<Var>();
	}
	
	public EnumVar(String name){
		this.name = name;
        this.value = "";
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
	
	public void accept(EnumVisitor v){
		v.visit(this);
	}
	
	public void setValue(String v){
		this.value = v;
	}
	
	public LinkedList<String> getOtherPrimsBooleanNames(){
    	LinkedList<String> result = new LinkedList<>();
    	for (Var v:this.otherVars){
    		if (v.getType() == Type.PRIMBOOL && !v.getName().equals(this.name))
    			result.add(v.getName());
    	}
    	return result;
    }
	
	public String getValue(){
		return value;
	}
	
	public EnumType getEnumType(){
		return this.myEnum;
	}
	
	public void setEnumType(EnumType t){
		this.myEnum = t;
	}
	
	public String getName(){
		return name;
	}
	
	public String getEnumTypeName(){
		return this.myEnum.getName();
	}

	public Type getType(){
		if (!this.isPrimType())
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
	
	/**
	 * 
	 * @return	the list of possible values of this enum var
	 */
	public LinkedList<String> getValues(){
		return this.myEnum.getValues();
	}
	
	public String toAlloy(String metaName, String state){
		// we use function expressions to capture enums in Alloy
		String result = "Val_"+this.getUnqualifiedName()+"["+metaName+","+state+"]";
		// the name of the signature corresponding to the var
    	return result;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean usesVar(String name){
		return this.getUnqualifiedName().equals(name);			
	}
	
	public String getUnqualifiedName(){
    	String result = name;
    	for (int i=0; i<name.length(); i++){
    		if (result.charAt(i) == '.'){
    			result = result.substring(i+1);
    			return result;
    		}
    	}
    	return result;	
    }
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return instances.contains(this.getOwner());
	}
}