package Parser;
import java.util.*;

import Utils.*;
import FormulaSpec.*;
import Spec.*;
/**
 * This is an auxiliar class used for parsing the final specification will be generated from it
 * @author Pablo
 *
 */
public class SpecAux{

	private String name;
	private HashMap<String,ProcessAux> instances; //the instances of the specification as declared in the spec
	private HashMap<String, LinkedList<String>> instanceActualPars; //the actual parameters of each instance 
	private LinkedList<ProcessAux> processes; //the list of processes declared
	private HashMap<String,Type> sharedVars; // the list of the shared vars declared in the specification
	private LinkedList<String> sharedPrimIntVars; // the list of volatile ints in the specs
												  // these names are also in sharedVars as Ints but we need
												  // to discriminate them
	private LinkedList<String> sharedPrimBoolVars; // similarly to above but for booleans
	private LinkedList<ExprAux> invs; // the list of the invariants
	private String errors; // a string used for keeping track of type errors
	private boolean instancesOK;
	//private boolean duplicatedInstances;
	//private int errorLine; // the line of the error
	
	public SpecAux(String name){
		this.name = name;
		this.instances = new HashMap<String, ProcessAux>();
		this.instanceActualPars =  new HashMap<String, LinkedList<String>>(); //the parameters of the instances in the corresponding order 
		this.processes = new LinkedList<ProcessAux>();
		this.sharedVars = new HashMap<String,Type>();
		this.invs = new LinkedList<ExprAux>();
		this.errors = "";
		this.instancesOK = true;
		this.sharedPrimBoolVars = new LinkedList<String>();
		this.sharedPrimIntVars = new LinkedList<String>();
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
	
	
	public void addInstanceActualPars(String instance, LinkedList<String> pars){
		// we check if the instance is correct
		if (!this.instances.containsKey(instance)){
			this.instancesOK = false;
			this.errors = this.errors + "Wrong instance invocation in main process!";
			return;
		}
		
		// first we check if the number of parameters is OK
		if (pars.size() != this.instances.get(instance).getNumberPars()){
			
			this.instancesOK = false;
			this.errors = this.errors + "Wrong instance invocation in main process!";
			return;
		}
		
		// we now check whether the types of the variables are ok
		for (int i=0; i<pars.size(); i++){
			if (this.sharedVars.get(pars.get(i)) != this.instances.get(instance).getParType(i)){
				this.instancesOK = false;
				this.errors = this.errors + "Wrong instance invocation in main process!";
				return;
			}	
		}
		
		// if everything is OK we add the pars
		this.instanceActualPars.put(instance, pars);			
	}
	
	public void addSharedVar(String name, Type type){
		// we deal with volatile vars first
		if (type == Type.PRIMBOOL){
			this.sharedVars.put(name, Type.BOOL); // a corresponding boolean var is added
			this.sharedPrimBoolVars.add(name); // we indicate that it is a prim type
		}
		if (type == Type.PRIMINT){ // similarly as above for int
			this.sharedVars.put(name, Type.INT);
			this.sharedPrimIntVars.add(name);
		}
		if (type == Type.INT || type == Type.BOOL || type== Type.LOCK){ // otherwise it is a standard bool or standard int 
			this.sharedVars.put(name, type);
		}
	}
	
	public void addAllSharedVars(HashMap<String, Type> v){
		Set<String> keys = v.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			Type currentType = v.get(current); 
			if (currentType == Type.PRIMBOOL){
				this.sharedVars.put(current, Type.BOOL); // primitive boolean are also consideras as boolean for type checking purposes
				this.sharedPrimBoolVars.add(current);
			}
			if (currentType == Type.PRIMINT){	// and similarly for PRIMINT
				this.sharedVars.put(current, Type.INT);
				this.sharedPrimIntVars.add(current);
			}
			if (currentType == Type.INT || currentType == Type.BOOL || currentType == Type.LOCK){
				sharedVars.put(current, currentType);
			}
		}
	}
	
	public void addInvariant(ExprAux e){
		invs.add(e);
	}
	
	public boolean isPrimTypeVar(String name){
		return this.sharedPrimBoolVars.contains(name) || this.sharedPrimIntVars.contains(name);
	}
	
	public String getErrors(){
		return this.errors;
	}
	
	/**
	 * @return	true iff the spec is correctly typed
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
			
			// we add the parameters of each process to the table
			LinkedList<Pair<String, Type>> pars = processes.get(i).getPars();
			for (int k=0; i<pars.size(); i++){
				Pair<String, Type> currentPar = pars.get(k);
				table.put(currentPar.getFirst(), currentPar.getSecond());
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
	 * @return	returns the spec
	 */
	public Spec getSpec(){
		
		// we create a symbol table for storing the expression and their types
		HashMap<String, Type> table = new HashMap<String, Type>(); // the symbol table
		
		// we add the shared vars
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
			// we add the boolean shared vars
			if (this.sharedVars.get(current) == Type.BOOL){
				BoolVar v = new BoolVar(current);
				if (this.isPrimTypeVar(current))
					v.setIsPrim(true);
				result.addGlobalVar(v);
			}
			
			// we add the int shared vars 
			if (this.sharedVars.get(current) == Type.INT){
				IntVar v = new IntVar(current);
				if (this.isPrimTypeVar(current))
					v.setIsPrim(true);
				result.addGlobalVar(v);
			}
			
			// we the locks
			if (this.sharedVars.get(current) == Type.LOCK){
				Lock l = new Lock(current, result, true);
				result.addLock(l);
			}
		}
		
		// get the invariants
		for (int i=0; i<this.invs.size(); i++){
			result.addInv((TemporalFormula) invs.get(i).getExpr(table));
		}
		
		// the instances
		Set<String> ins = this.instances.keySet();
		Iterator<String> it2 = ins.iterator();
		while (it2.hasNext()){
			String current = it2.next();
			result.addInstance(current, result.getProcessByName(this.instances.get(current).getName()));
		}
		
		// the parameters of the instances
		Iterator<String> it3 = this.instanceActualPars.keySet().iterator();
		while (it3.hasNext()){
			String current = it3.next();
			for (int i=0; i<this.instanceActualPars.get(current).size(); i++){
				String actualPar = this.instanceActualPars.get(current).get(i);
				result.addInstanceActualPar(current, result.getGlobalVarByName(actualPar));
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return True when the variable is a lock (global variable)
	 */
	public boolean isLock(String name){
		// for now all the shared variables has associated locks, 
		// if primitive types are added then we should modify this
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
			if (this.processes.get(i).declaredVar(name) && this.processes.get(i).getName().equals(processName))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param name	the unqualified name of the var
	 * @param processName	the process name
	 * @return	true iff the param is declared in the process
	 */
	public boolean checkParDeclaredInProcess(String name, String processName){
		for (int i=0; i<this.processes.size(); i++){
			if (this.processes.get(i).containsPar(name) && this.processes.get(i).getName().equals(processName))
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
	
	public Type getTypePar(String name, String owner){
		if (owner.equals("global")){
				return Type.ERROR; // parameters cannot be global
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
				return myProcess.getParType(name);
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
	
	public ProcessAux getProcessByName(String name){
		for (int i=0; i< this.processes.size(); i++){
			if (this.processes.get(i).getName().equals(name))
				return this.processes.get(i);
		}
		throw new RuntimeException("Process Not Found.");
	}
	
	
	public boolean isParameter(String unqualifiedName, String owner){
		if (this.instances.containsKey(owner))
			return this.instances.get(owner).containsPar(unqualifiedName);
		else
			return false;
	}
}
