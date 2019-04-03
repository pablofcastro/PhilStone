package FormulaSpec;

import java.util.LinkedList;

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
		// we obtain the owner
		String owner = "";
		int i=0;
		while (i < lock.getName().length()){
			if (lock.getName().charAt(i)=='.'){
				owner = lock.getName().substring(0, i);
			}	
		i++;
		}
		
    	return owner+"."+"Own_"+lock.getUnqualifiedName();
    }
    
	public String getOwner(){
		String owner = "";
		int i=0;
		while (i < lock.getName().length()){
			if (lock.getName().charAt(i)=='.'){
				owner = lock.getName().substring(0, i);
			}	
		i++;
		}
		return owner;
	}
 
    public Type getType(){
    	return Type.BOOL;
    }
    
    public boolean usesVar(String name){
    	return lock.usesVar(name);
    }
    
    public String toAlloy(String metaName, String state){
    	String result = "Own_"+lock.getUnqualifiedName()+"["+metaName+","+state+"]";
    	return result;
    }
    
    public boolean containsVarOwnedBy(LinkedList<String> instances){
		return lock.containsVarOwnedBy(instances);
	}

    
    public Formula removeVarOwnedBy(LinkedList<String> instances){
		if (instances.contains(lock.getOwner()))
			return new BoolConstant(true);
		else
			return this;
	}
}
