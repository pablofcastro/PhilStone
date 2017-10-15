package FormulaSpec;

public class Av implements ElemFormula{
	private Var lock;
	
	public Av(Var v){
		this.lock = v;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	@Override
    public String toString(){    	
    	return "Av("+lock.toString()+")";
    }
    
 
    public Type getType(){
    	return Type.BOOL;
    }
    
    public String toAlloy(String metaName, String state){
    	String result = "Av_"+lock.getUnqualifiedName()+"["+metaName+","+state+"]";
    	return result;
    }
    


}
