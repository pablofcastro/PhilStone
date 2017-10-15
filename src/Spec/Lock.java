package Spec;
import java.util.*;

public class Lock{
	private String varName; // the associated var name to the lock
	private Spec mySpec;
	
	
	public Lock(String varName, Spec mySpec) {
		this.varName = "change_"+varName;
		this.varName = varName;
		this.mySpec = mySpec;
	}
	
	public LinkedList<String> getOtherGlobalVars(){
		LinkedList<String> globalVars = this.mySpec.getGlobalVarsNames();
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<globalVars.size(); i++){
			if (!globalVars.get(i).equals(this.varName)){
				result.add(globalVars.get(i));
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
