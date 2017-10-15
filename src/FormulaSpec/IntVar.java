package FormulaSpec;

public class IntVar implements IntegerExpression, Var{
	private String name;
	private int value;
	
	public IntVar(String name, int value){
		this.name = name;
        this.value = value;
	}
	
	public IntVar(String name){
		this.name = name;
        this.value = 0;
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
		return Type.INT;
	}
	
	public String toAlloy(String metaName, String state){
		return name;
	}
	
	public String toString(){
		return name;
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
}
