package FormulaSpec;

/**
 * This class implements the basic behavior of a Own proposition used for synchronization
 * @author Pablo
 *
 */
public class Own implements ElemFormula {
	private Var lock;
	
	public Own(Var v){
		this.lock = v;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	@Override
    public String toString(){    	
    	return "Own("+lock.toString()+")";
    }
    
 
    public Type getType(){
    	return Type.BOOL;
    }
    
    public String toAlloy(String metaName, String state){
    	String result = "Own_"+lock.getUnqualifiedName()+"["+metaName+","+state+"]";
    	return result;
    }

}
