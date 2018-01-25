package FormulaSpec;

import java.util.LinkedList;

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
    
    public boolean usesVar(String name){
		return lock.usesVar(name);
	}
    
    public String toAlloy(String metaName, String state){
    	String result = "Av_"+lock.getUnqualifiedName()+"["+metaName+","+state+"]";
    	return result;
    }
    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
    	if (instances.contains(lock.getOwner()))
    		return new BoolConstant(true);
    	else
    		return this;
	}
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return lock.containsVarOwnedBy(instances);
	}


}
