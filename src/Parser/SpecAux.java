package Parser;
import java.util.*;

import FormulaSpec.*;
import Spec.*;
/**
 * This is an auxiliar class used for parsing
 * @author Pablo
 *
 */
public class SpecAux {

	private String name;
	private HashMap<String,ProcessAux> instances; //the instances of the specification as declared in the spec
	private LinkedList<ProcessAux> processes; //the list of processes declared
	private HashMap<String,Type> sharedVars; // the list of the shared var declared
	private LinkedList<ExprAux> invs; // the list of the invariants
	private String errors; // a string used for keeping track of type errors
	private boolean instancesOK;
	//private boolean duplicatedInstances;
	//private int errorLine; // the line of the error
	
	public SpecAux(String name){
		this.name = name;
		this.instances = new HashMap<String, ProcessAux>();
		this.processes = new LinkedList<ProcessAux>();
		this.sharedVars = new HashMap<String,Type>();
		this.invs = new LinkedList<ExprAux>();
		this.errors = "";
		this.instancesOK = true;
		//this.duplicatedInstances = false;
	}
	
	public void addAllProcesses(LinkedList<ProcessAux> list){		
		for (int i=0; i<list.size(); i++){
			this.addProcess(list.get(i));
		}
	}
	
	public void addProcess(ProcessAux p){
		processes.add(p);
		p.setSpec(this);
	}
	
	public void addInstance(String name, String p){
		boolean isOK = false;
		if (instances.containsKey(name)){
			this.errors = this.errors + "Duplicated variables in main program!";
			this.instancesOK = false;
		}
		else{
			for (int i=0; i<processes.size(); i++){
				if (processes.get(i).getName().equals(p)){
					instances.put(name, processes.get(i));
					isOK = true;
				}
			}
			if (!isOK){
				this.errors = this.errors + "\n" + "error in instance definition in main program";
				instancesOK = isOK;
			}
		}
	}
	
	public void addSharedVar(String name, Type type){
		this.sharedVars.put(name, type);
	}
	
	public void addAllSharedVars(HashMap<String, Type> v){
		Set<String> keys = v.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			sharedVars.put(current, v.get(current));
		}
	}
	
	public void addInvariant(ExprAux e){
		invs.add(e);
	}
	
	public String getErrors(){
		return this.errors;
	}
	
	/**
	 * 
	 * @return	true iff the spec is grammatically correct
	 */
	public boolean typeCheck(){
		
		// first  we construct a table with all the variables
		HashMap<String, Type> table = new HashMap<String, Type>(); // symbol table
		Set<String> keys = this.sharedVars.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			table.put(current, this.sharedVars.get(current));			
		}
		
		// get the local vars
		for (int i=0; i < this.processes.size(); i++){
			HashMap<String, Type> localt = processes.get(i).getLocalVars();
			Set<String> localKeys = localt.keySet();
			Iterator<String> it2 = localKeys.iterator();
			while (it2.hasNext()){
				String current = it2.next();
				table.put(current, localt.get(current));			
			}	
		}
		boolean checkOk = true; // to signal if everything is OK
		for (int i=0;i<processes.size();i++){
			if (!processes.get(i).isWellFormed(table) || processes.get(i).getDuplicatedVars()){
				checkOk = false;
				this.errors = this.errors + "\n" + processes.get(i).getErrors();			
			}
		}
		for (int i=0; i< this.invs.size(); i++){
			if (invs.get(i).getType(table, this, "global") == Type.INT){
				checkOk = false;
				this.errors = this.errors + "\n" + "Error in global property, line: "+  this.invs.get(i).getLine();
			}
			if (invs.get(i).getType(table, this, "global") == Type.ERROR){
				checkOk = false;
				this.errors = this.errors + "\n " +  this.invs.get(i).getError();			
			}
			if (invs.get(i).containsLock()){
				checkOk = false;
				this.errors +=  "\nInvariant contains AV/OWN primitive, line :" +  this.invs.get(i).getLine();	
			}	
		}
		if (!this.instancesOK)
			checkOk = false;
		return checkOk;
	}
	
	/**
	 * 
	 * @return	the spec from the expression
	 */
	public Spec getSpec(){
		HashMap<String, Type> table = new HashMap<String, Type>(); // symbol table
		Set<String> keys = this.sharedVars.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			table.put(current, this.sharedVars.get(current));			
		}
		
		// get the local vars
		for (int i=0; i < this.processes.size(); i++){
			HashMap<String, Type> localt = processes.get(i).getLocalVars();
			Set<String> localKeys = localt.keySet();
			Iterator<String> it2 = localKeys.iterator();
			while (it2.hasNext()){
				String current = it2.next();
				table.put(current, localt.get(current));			
			}	
		}
		
		Spec result = new Spec(this.name);
		for (int i=0; i<processes.size();i++){
			ProcessSpec current = processes.get(i).getProcessSpec(table);
			result.addProcess(current);
			current.setSpec(result);
			
			// we set the instances corresponding to this process
			Iterator<String> it3 = instances.keySet().iterator();
			while (it3.hasNext()){
				String currentInstance = it3.next();
				if (instances.get(currentInstance) == processes.get(i)){
					result.addInstance(currentInstance, current);
				}
			}
		}
		
		// set the shared vars
		Set<String> vars = this.sharedVars.keySet();
		Iterator<String> it1 = vars.iterator();
		while (it1.hasNext()){
			String current = it1.next();
			if (this.sharedVars.get(current) == Type.BOOL){
				BoolVar v = new BoolVar(current);
				result.addGlobalVar(v);
			}
			if (this.sharedVars.get(current) == Type.INT){
				IntVar v = new IntVar(current);
				result.addGlobalVar(v);
			}
		}
		
		// get the invariants
		for (int i=0; i<this.invs.size(); i++){
			result.addInv((TemporalFormula) invs.get(i).getExpr(table));
		}
		
		// the instances
		
		return result;
	}
	
	/**
	 * 
	 * @return True when the variable is a lock (global variable)
	 */
	public boolean isLock(String name){
		return this.sharedVars.containsKey(name);
	}
	
	/**
	 * 
	 * @param name	the unqualified name of the var
	 * @param instance	the name of the instance
	 * @return	true when the variable is declared in the instance
	 */
	public boolean checkVarBelongInstance(String name, String instance){
		if (!instances.containsKey(instance)){
			return false;
		}
		else{
			return instances.get(instance).declaredVar(name);
		}
		
	}
	
	public boolean checkVarIsGlobal(String name){
		return this.sharedVars.containsKey(name);
	}
	
	/**
	 * 
	 * @param name	the unqualified name of the var
	 * @param processName
	 * @return	true iff the var is declared in the given process
	 */
	public boolean checkVarDeclaredInProcess(String name, String processName){
		for (int i=0; i<this.processes.size(); i++){
			if (this.processes.get(i).declaredVar(name))
				return true;
		}
		return false;
	}
	
	/**
	 * A method to look up for the type of a var
	 * @param 	name	the unqualified var name
	 * @param 	owner	the name of the owner process, it could be "global" for global vars
	 * @return	the type of the var or a type error
	 */
	public Type getTypeVar(String name, String owner){
		if (owner.equals("global")){
			if (this.sharedVars.containsKey(name)){
				return this.sharedVars.get(name);
			}
			else{
				return Type.ERROR;
			}	
		}
		else{ // it is a local var
			ProcessAux myProcess = null;
			for (int i = 0; i<this.processes.size(); i++){
				if (this.processes.get(i).getName().equals(owner)){
					myProcess = this.processes.get(i);
					break;
				}
			}
			if (myProcess == null)
				return Type.ERROR;
			else{
				return myProcess.getVarType(name);
			}
		}
		
	}
	
	public Type getTypeVarFromInstance(String name, String instance){
		if (instance.equals("global")){
			if (this.sharedVars.containsKey(name)){
				return this.sharedVars.get(name);
			}
			else{
				return Type.ERROR;
			}	
		}
		else{ // it is a local var		
			if (!instances.containsKey(instance))
				return Type.ERROR;
			else{
				return instances.get(instance).getVarType(name);
			}
		}
		
	}
	
	/**
	 * 
	 * @return	the list of the global vars in the specification
	 */
	public LinkedList<Var> getGlobalVars(){
		LinkedList<Var> result = new LinkedList<Var>(); 
		Set<String> vars = this.sharedVars.keySet();
		Iterator<String> it = vars.iterator();
		while (it.hasNext()){
			String current = it.next();
			if (this.sharedVars.get(current) ==  Type.INT){
				IntVar var = new IntVar(current);
				result.add(var);
			}
			if (this.sharedVars.get(current) ==  Type.BOOL){
				BoolVar var = new BoolVar(current);
				result.add(var);
			}	
		}
		return result;
	}
	
}
