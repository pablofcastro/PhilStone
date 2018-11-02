package Spec;
import java.util.*;

public class Lock{
	private String varName; // the associated var name to the lock
	private Spec mySpec;
	private LinkedList<String> usedGlobalVarsWithLocks;
	private LinkedList<String> usedGlobalVars;
	private LinkedList<String> usedBooleanGlobalVars;
	private LinkedList<String> usedIntGlobalVars;
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
	}
	
	public Lock(String varName, boolean onlyLock){
		this.varName = varName;
		this.mySpec = null;
		this.onlyLock = false;
		this.usedGlobalVarsWithLocks = new LinkedList<String>();
		this.usedGlobalVars = new LinkedList<String>();
		this.usedBooleanGlobalVars = new LinkedList<String>();
		this.usedIntGlobalVars = new LinkedList<String>();
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
	}
	
	/**
	 * Basic getter
	 * @return	the onlyLock boolean
	 */
	public boolean getOnlyLock(){
		return this.onlyLock;
	}
	
	/**
	 * A basic setter
	 * @param list	the list of global vars used in the associated specification
	 */
	public void setUsedGlobalVars(LinkedList<String> list){
		this.usedGlobalVars = list;
	}
	
	public void addAllUsedGlobalVars(LinkedList<String> list){
		this.usedGlobalVars.addAll(list);
	}
	
	public void addAllUsedBooleanGlobalVars(LinkedList<String> list){
		this.usedBooleanGlobalVars.addAll(list);
	}
	
	public void addAllUsedIntGlobalVars(LinkedList<String> list){
		this.usedIntGlobalVars.addAll(list);
	}
	
	public void addAllUsedGlobalVarsWithLocks(LinkedList<String> list){
		this.usedGlobalVarsWithLocks.addAll(list);
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
		for (int i=0; i<usedGlobalVars.size(); i++){
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
