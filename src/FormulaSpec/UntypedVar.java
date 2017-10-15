package FormulaSpec;

public class UntypedVar implements Expression {
	String name;
	
	public UntypedVar(String name){
		this.name = name;
	}
	
	public IntVar toIntVar(){
		IntVar result = new IntVar(this.name,0);
		return result;
	}
	
	public String toAlloy(String metaName, String state){
		return name;
	}

}
