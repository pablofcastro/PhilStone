package Spec;
import java.util.*;

public class Lock{
	private String varName; // the associated var name to the lock
	private Spec mySpec;
	private LinkedList<String> usedGlobalVars;
	private boolean onlyLock; // this variable is use to point out that the associated variables is only a lock, do not need more data
	
	
	public Lock(String varName, Spec mySpec) {
		this.varName = "change_"+varName;
		this.varName = varName;
		this.mySpec = mySpec;
		this.onlyLock = false;
	}
	
	public Lock(String varName, Spec mySpec, boolean onlyLock) {
		this.varName = "change_"+varName;
		this.varName = varName;
		this.mySpec = mySpec;
		this.onlyLock = onlyLock;
	}
	
	public boolean getOnlyLock(){
		return this.onlyLock;
	}
	
	public void setUsedGlobalVars(LinkedList<String> list){
		this.usedGlobalVars = list;
	}
	
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
	
	public String getActionName(){
		return "change_"+varName;
	}

	public String getVarName(){
		return this.varName;
	}
	
}
