package FormulaSpec;


import java.util.LinkedList;

public class DecEnum implements EnumExpression{
	private EnumExpression myEnum;
	
	public DecEnum(EnumExpression e){
        this.myEnum = e;		
	}
	
	//public String toString(){
	//}
    
	public void accept(EnumVisitor v){
		v.visit(this);
	}
	
	public String toAlloy(String metaName, String state){
		// TBD
		return "";
	}
	
	public boolean usesVar(String name){
		return (this.myEnum.usesVar(name));
	}
	
	public String toString(){
		return "inc"+this.myEnum.toString(); 
	}
	
	public boolean containsVarOwnedBy(LinkedList<String> instances){
		return this.myEnum.containsVarOwnedBy(instances);
	}
	
	
}