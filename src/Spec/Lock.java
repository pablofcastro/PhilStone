package Spec;
import java.util.*;
import FormulaSpec.*;

public class Lock implements Var{
	private String varName; // the associated var name to the lock
	private Spec mySpec;
	private LinkedList<String> usedGlobalVarsWithLocks;
	private LinkedList<String> usedGlobalVars;
	private LinkedList<String> usedBooleanGlobalVars;
	private LinkedList<String> usedIntGlobalVars;
	private LinkedList<String> usedEnumGlobalVars;
	private boolean onlyLock; // this variable is use to point out that the associated variable is only a lock, do not need more data
	
	
	/**
	 * A Basic Constructor
	 * @param varName	the name of the var associated to the lock
	 * @param mySpec	the name of the spec where the lock resides
	 */
	public Lock(String varName, Spec mySpec) {
		this.varName = "change_"+varName;
		this.varName = varName;
		this.mySpec = mySpec;
		this.onlyLock = false;
		this.usedGlobalVarsWithLocks = new LinkedList<String>();
		this.usedGlobalVars = new LinkedList<String>();
		this.usedBooleanGlobalVars = new LinkedList<String>();
		this.usedIntGlobalVars = new LinkedList<String>();
		this.usedEnumGlobalVars = new LinkedList<String>();
	}
	
	public Lock(String varName, boolean onlyLock){
		this.varName = varName;
		this.mySpec = null;
		this.onlyLock = onlyLock;
		this.usedGlobalVarsWithLocks = new LinkedList<String>();
		this.usedGlobalVars = new LinkedList<String>();
		this.usedBooleanGlobalVars = new LinkedList<String>();
		this.usedIntGlobalVars = new LinkedList<String>();
		this.usedEnumGlobalVars = new LinkedList<String>();
	}
	
	/**
	 * Another constructor taking as parameter a boolean statin that this is only a lock.
	 * @param varName	the name of the var or lock	
	 * @param mySpec	the name of the associated specification
	 * @param onlyLock	the boolean stating whether it is a lock or not
	 */
	public Lock(String varName, Spec mySpec, boolean onlyLock) {
		this.varName = "change_"+varName;
		this.varName = varName;
		this.mySpec = mySpec;
		this.onlyLock = onlyLock;
		this.usedGlobalVarsWithLocks = new LinkedList<String>();
		this.usedGlobalVars = new LinkedList<String>();
		this.usedBooleanGlobalVars = new LinkedList<String>();
		this.usedIntGlobalVars = new LinkedList<String>();
		this.usedEnumGlobalVars = new LinkedList<String>();
		
	}
	
	public String getName(){
		return this.varName;
	}
	
	public Type getType(){
		return Type.LOCK;
	}
	
    public boolean isPrimType(){
    	return false;
    }
    
    /**
     * Since the usedVars variables are dependent of each process one needs to reset them for each process
     */
    public void resetUsedVars(){
    	this.usedGlobalVarsWithLocks.clear();
    	this.usedGlobalVars.clear();
    	this.usedBooleanGlobalVars.clear();
    	this.usedIntGlobalVars.clear();
    	this.usedEnumGlobalVars.clear();
    }
	
	
    public String toAlloy(String metaName, String state){
    	// Locks does not have asociated alloy code, all its behavior comes through Av and Own
    	String result = "";
    	return result;
    }

    public String getUnqualifiedName(){
    	String result = this.varName;
    	for (int i=0; i<this.varName.length(); i++){
    		if (result.charAt(i) == '.'){
    			result = result.substring(i+1);
    			break;
    		}
    	}
    	return result;	
    }
    
    public boolean usesVar(String name){
		return this.getUnqualifiedName().equals(name);			
	}
    
    public String getOwner(){
    	String result = this.varName;
    	for (int i=0; i<this.varName.length(); i++){
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
    
	/**
	 * Basic getter
	 * @return	the onlyLock boolean
	 */
	public boolean isOnlyLock(){
		return this.onlyLock;
	}
	
	/**
	 * A basic setter
	 * @param list	the list of global vars used in the associated specification
	 */
	public void setUsedGlobalVars(LinkedList<String> list){
		this.usedGlobalVars = list;
	}
	
	/**
	 * This method does nothing for locks
	 */
	public void setIsPrim(boolean isPrime){
		
	}
	
	public void addAllUsedGlobalVars(LinkedList<String> list){
		for (String var:list){
			if (!this.usedGlobalVars.contains(var))
				this.usedGlobalVars.add(var);
		}
		//this.usedGlobalVars.addAll(list);
	}
	
	public void addAllUsedBooleanGlobalVars(LinkedList<String> list){
		for (String var:list){
			if (!this.usedBooleanGlobalVars.contains(var))
				this.usedBooleanGlobalVars.add(var);
		}
		//this.usedBooleanGlobalVars.addAll(list);
	}
	
	public void addAllUsedIntGlobalVars(LinkedList<String> list){
		for (String var:list){
			if (!this.usedIntGlobalVars.contains(var))
				this.usedIntGlobalVars.add(var);
		}
		//this.usedIntGlobalVars.addAll(list);
	}
	
	public void addAllUsedEnumGlobalVars(LinkedList<String> list){
		for (String var:list){
			if (!this.usedEnumGlobalVars.contains(var))
				this.usedEnumGlobalVars.add(var);
		}
		//this.usedEnumGlobalVars.addAll(list);
	}
	
	public void addAllUsedGlobalVarsWithLocks(LinkedList<String> list){
		for (String var:list){
			if (!this.usedGlobalVarsWithLocks.contains(var))
				this.usedGlobalVarsWithLocks.add(var);
		}
		//this.usedGlobalVarsWithLocks.addAll(list);
	}
	
	/**
	 * A basic setter
	 * @param list	the list of global vars used in the associated specification
	 */
	public void setUsedGlobalVarsWithLocks(LinkedList<String> list){
		this.usedGlobalVarsWithLocks = list;
	}
	
	/**
	 * A Basic Setter
	 * @param list	the list of used boolean vars in the associated specification
	 */
	public void setUsedBooleanGlobalVars(LinkedList<String> list){
		this.usedBooleanGlobalVars = list;
	}
	
	/**
	 * A Basic Setter
	 * @param list	the list of used int vars in the associated specification
	 */
	public void setUsedIntGlobalVars(LinkedList<String> list){
		this.usedIntGlobalVars = list;
	}
	
	/**
	 * A Basic Setter
	 * @param list	the list of used enum vars in the associated specification
	 */
	public void setUsedEnumGlobalVars(LinkedList<String> list){
		this.usedEnumGlobalVars = list;
	}
	
	/**
	 * 
	 * @return	the list of shared vars different from the actual one residing in the specification
	 */
	public LinkedList<String> getOtherGlobalVars(){
		//we calculate this using a process and the method above setUsedGlobalVars
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<usedGlobalVars.size(); i++){
			if (!usedGlobalVars.get(i).equals(varName)){
				result.add(usedGlobalVars.get(i));
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return	the list of shared vars different from the actual one residing in the specification that have associated locks
	 */
	public LinkedList<String> getOtherGlobalVarsWithLocks(){
		//we calculate this using a process and the method above setUsedGlobalVars
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<usedGlobalVarsWithLocks.size(); i++){
			if (!usedGlobalVarsWithLocks.get(i).equals(varName)){
				result.add(usedGlobalVarsWithLocks.get(i));
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * 
	 * @return	the list of BOOLEAN shared vars different from the actual one residing in the specification
	 */
	public LinkedList<String> getOtherBooleanGlobalVars(){
		//we calculate this using a process and the method above setUsedGlobalVars
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<usedBooleanGlobalVars.size(); i++){
			if (!usedBooleanGlobalVars.get(i).equals(varName)){
				result.add(usedBooleanGlobalVars.get(i));
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return	the list of INT shared vars different from the actual one residing in the specification
	 */
	public LinkedList<String> getOtherIntGlobalVars(){
		//we calculate this using a process and the method above setUsedGlobalVars
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<usedIntGlobalVars.size(); i++){
			if (!usedIntGlobalVars.get(i).equals(varName)){
				result.add(usedIntGlobalVars.get(i));
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return	the list of ENUM shared vars different from the actual one residing in the specification
	 */
	public LinkedList<String> getOtherEnumGlobalVars(){
		//we calculate this using a process and the method above setUsedGlobalVars
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<usedEnumGlobalVars.size(); i++){
			if (!usedEnumGlobalVars.get(i).equals(varName)){
				result.add(usedEnumGlobalVars.get(i));
			}
		}
		return result;
	}
	
	/**
	 * @return	the name of the action associated to the lock
	 */
	public String getActionName(){
		return "change_"+varName;
	}

	/**
	 * 
	 * @return	the name of the var
	 */
	public String getVarName(){
		return this.varName;
	}
	
}
