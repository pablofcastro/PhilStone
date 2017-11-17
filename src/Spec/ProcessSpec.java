
package Spec;

import java.util.*;
import java.io.*;
import org.stringtemplate.v4.*;

import FormulaSpec.*;

/**
 * @author Pablo
 * A Class for the Specification of a Process
 */
public class ProcessSpec {
	String name; // the name of the process
	LinkedList<Action> actions; // the actions of the process
	LinkedList<Var> sharedVars; // the global vars
	LinkedList<Var> localVars;  // the local vars of the process
	LinkedList<Var> pars;		// the parameters of the process
	LinkedList<TemporalFormula> invs; // the invariants of the process
	LinkedList<String> instances; // the instances of the specification
	Spec mySpec;
	Formula init;
	
	/**
	 * Basic Class Constructor
	 * @param name
	 */
	public ProcessSpec(String name) {
		this.name = name;
		this.sharedVars = new LinkedList<Var>();
		this.localVars = new LinkedList<Var>();
		this.pars = new LinkedList<Var>();
		this.actions = new LinkedList<Action>();
		this.invs = new LinkedList<TemporalFormula>();
	}

	/**
	 * 
	 * @return	the name of the Process
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name
	 * @param name	the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Adds an action
	 * @param act
	 */
	public void addAction(Action act){
		actions.add(act);
	}
	
	/**
	 * Adds an invariant
	 * @param inv
	 */
	public void addInv(TemporalFormula inv){
		invs.add(inv);
	}
	
	/**
	 * Adds a shared variable
	 * @param v
	 */
	public void addSharedVar(Var v){
		sharedVars.add(v);
	}
	
	
	public void addAllSharedVar(LinkedList<Var> list){
		sharedVars.addAll(list);
	}
	
	/**
	 * Adds a local var to the process
	 * @param v
	 */
	public void addLocalVar(Var v){
		localVars.add(v);
	}
	
	public void addPar(Var v){
		this.pars.add(v);
	}
	
	public void addInstance(String name){
		instances.add(name);
	}
	
	public void setInit(Formula f){
		this.init = f;
	}
	
	public Formula getInit(){
		return this.init;
	}
	
	public void setSpec(Spec mySpec){
		this.mySpec = mySpec;
	}
	
	public LinkedList<String> getSharedVarsNames(){
		return this.mySpec.getGlobalVarsNames();
	}
	
	public LinkedList<String> getLocalVarsNames(){
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<this.localVars.size(); i++){
			result.add(this.localVars.get(i).getName());
		}
		return result;
	}
	
	public LinkedList<String> getBoolParNames(){
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<this.pars.size();i++){
			if (this.pars.get(i).getType() == Type.BOOL){
				result.add(this.pars.get(i).getName());
			}
		}
		return result;	
	}
	
	/**
	 * A method to check whether a process mention a global var o nor
	 * @param var
	 * @return	true when the global variable is mentioned in the process
	 */
	public boolean usesSharedVar(String var){
		for (int i=0; i<actions.size();i++){
			if (actions.get(i).usesVar(var))
				return true;
		}
		for (int i=0; i< invs.size();i++){
			if (invs.get(i).usesVar(var))
				return true;
		}
		return init.usesVar(var);
	}
	
	public LinkedList<String> getIntParNames(){
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<this.pars.size();i++){
			if (this.pars.get(i).getType() == Type.INT){
				result.add(this.pars.get(i).getName());
			}
		}
		return result;	
	}
	
	public String metamodelToString(String templateDir){
		List<String> localBoolProps = new ArrayList<String>();
		for (int i = 0; i < localVars.size(); i++){
			if (localVars.get(i).getType() == Type.BOOL)
				localBoolProps.add(localVars.get(i).getName());
		}
		
		// we set the actions
		List<Action> localActions = new ArrayList<Action>();
		List<Action> envActions = new ArrayList<Action>();
		for (int i = 0; i < actions.size(); i++){
			if (actions.get(i).getIsLocal())
				localActions.add(actions.get(i));
			else
				envActions.add(actions.get(i));
		}
		
		List<String> invariants = new ArrayList<String>();
		for (int i=0; i<this.invs.size(); i++){
			invariants.add(invs.get(i).toAlloy(name+"Meta", "s"));
		}

		List<Action> actions = new ArrayList<Action>();
		actions.addAll(localActions);
		actions.addAll(envActions);
		
		STGroup group = new STGroupDir(templateDir);
		ST st = group.getInstanceOf("metamodel");
		
		st.add("name", this.name);
		st.add("boolProps", localBoolProps);
		//st.add("sharedBoolProps", sharedBoolProps);
		LinkedList<String> globalVars = new LinkedList<String>();
		//globalVars.addAll(mySpec.getGlobalVarsNames());
		
		
		// we add just the global variables used in this process
		for (int i=0; i< mySpec.getGlobalVarsNames().size(); i++){
			if (this.usesSharedVar(mySpec.getGlobalVarsNames().get(i))){
				globalVars.add(mySpec.getGlobalVarsNames().get(i));
			}
		}
		
		// define usedglobalvars
		LinkedList<String> usedGlobalVars = new LinkedList<String>();
		for (int i=0; i<this.mySpec.getGlobalVarsNames().size();i++){
			if (this.usesSharedVar(this.mySpec.getGlobalVarsNames().get(i)))
				usedGlobalVars.add(this.mySpec.getGlobalVarsNames().get(i));		
		}
		usedGlobalVars.addAll(this.getBoolParNames());
		
		// we set the important global vars for the locks
		for (int i=0; i<this.mySpec.getLocks().size();i++){
			this.mySpec.getLocks().get(i).setUsedGlobalVars(usedGlobalVars);
			//System.out.println(this.mySpec.getLocks().get(i).getOtherGlobalVars());
		}
		
		LinkedList<Lock> usedLocks= new LinkedList<Lock>();
		for (int i=0;i<this.mySpec.getLocks().size();i++){
			if (usedGlobalVars.contains(this.mySpec.getLocks().get(i).getVarName()))
				usedLocks.add(this.mySpec.getLocks().get(i));
		}
		// parameters are considered global vars
		globalVars.addAll(this.getBoolParNames());
		
		// parameters are also considered locks 
		for (int i=0; i<this.getBoolParNames().size();i++){
			Lock parLock = new Lock(this.getBoolParNames().get(i), this.mySpec);
			parLock.setUsedGlobalVars(usedGlobalVars);
			usedLocks.add(parLock);
		}
		
		for (int i=0; i<usedLocks.size(); i++){
			usedLocks.get(i).getOtherGlobalVars();
		}
		
		st.add("sharedBoolProps", globalVars); // we take the parameters as a global variables
		st.add("locks", usedLocks); //for now the global vars are locks
		st.add("localActions", localActions);
		st.add("envActions", envActions);
		st.add("actions", actions);
		st.add("invariants", invariants);
		st.add("init", this.init.toAlloy(this.name+"Meta", "s"));
		
		
		String result = st.render();
		return result;
	}
	
	
	/**
	 * It generates the metamodel of the process  in alloy
	 * DEPRECATED it will be replace for he methods above
	 * @param file	where the metamodel will be written
	 */
	public void generateMetamodel(FileWriter file, String templateDir){
		
		// we set the propositions
		
		//List<String> sharedBoolProps = new ArrayList<String>();
		//for (int i = 0; i < sharedVars.size(); i++){
		//		if (sharedVars.get(i).getType() == Type.BOOL)
		//			sharedBoolProps.add(sharedVars.get(i).getName());
		//			
		//}		
		
		List<String> localBoolProps = new ArrayList<String>();
		for (int i = 0; i < localVars.size(); i++){
			if (localVars.get(i).getType() == Type.BOOL)
				localBoolProps.add(localVars.get(i).getName());
		}
		
		// we set the actions
		List<Action> localActions = new ArrayList<Action>();
		List<Action> envActions = new ArrayList<Action>();
		for (int i = 0; i < actions.size(); i++){
			if (actions.get(i).getIsLocal())
				localActions.add(actions.get(i));
			else
				envActions.add(actions.get(i));
		}
		
		List<String> invariants = new ArrayList<String>();
		for (int i=0; i<this.invs.size(); i++){
			invariants.add(invs.get(i).toAlloy(name+"Meta", "s"));
		}

		List<Action> actions = new ArrayList<Action>();
		actions.addAll(localActions);
		actions.addAll(envActions);
		
		STGroup group = new STGroupDir(templateDir);
		ST st = group.getInstanceOf("metamodel");
		
		//System.out.println(localActions);
		
		st.add("name", this.name);
		st.add("boolProps", localBoolProps);
		//st.add("sharedBoolProps", sharedBoolProps);
		st.add("sharedBoolProps", mySpec.getGlobalVarsNames());
		st.add("localActions", localActions);
		st.add("envActions", envActions);
		st.add("actions", actions);
		st.add("invariants", invariants);
		st.add("init", this.init.toAlloy(this.name+"Meta", "s"));
		//List<String> values = new ArrayList<String>();
		//values.add("one");
		//values.add("two");
		//st.add("values", values);
		
		String result = st.render();
		try{
		    PrintWriter writer = new PrintWriter("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+name+"Template.als", "UTF-8");
		    writer.print(result);
		    writer.close();
		} catch (IOException e) {
			System.out.println("Input-Output Error trying to write the metamodel.");
		}
		//System.out.println(result);
	}
	
	public String toString(){
		String result = "";
		result += "Process " + name + "(){\n";
		result += "vars: ";
		for (int i=0; i<localVars.size(); i++){
			result += localVars.get(i).toString()+",";
		}
		result += "\n";
		for (int i=0; i<actions.size(); i++){
			result += actions.get(i).toString();
		}
		for (int i=0; i<invs.size(); i++){
			result += "inv: " + invs.get(i).toString()+"\n" ;
		}
		result += "}\n";
		return result;		
	}
	
	
}
