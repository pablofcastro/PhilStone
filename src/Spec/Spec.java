package Spec;
import java.io.FileWriter;
import java.util.*;

import FormulaSpec.*;

/**
 * A class representing the specification of a collection of processes
 * @author Pablo
 *
 */
public class Spec {
	String name; // the name of the specification
	private LinkedList<ProcessSpec> processes; // the processes
	private LinkedList<Var> globalVars; // the global vars, this list does not contain the primVars
	private LinkedList<Lock> locks;	// the locks in the specification
	private LinkedList<TemporalFormula> invs; // the invariants
	private HashMap<String, ProcessSpec> instances; // the instances in the specification
	private HashMap<String, LinkedList<Var>> actualPars; // the actual parameters of each instance
	private LinkedList<EnumType> enums; // the enums defined in the specification
	
	/**
	 * A Basic Constructor for Spec
	 * @param name	takes the name of the specification
	 */
	public Spec(String name){
		this.name = name;
		this.processes = new LinkedList<ProcessSpec>();
		this.globalVars = new LinkedList<Var>();
		this.locks = new LinkedList<Lock>();
		this.invs = new LinkedList<TemporalFormula>();
		this.instances = new HashMap<String, ProcessSpec>();
		this.actualPars = new HashMap<String, LinkedList<Var>>();
		this.enums = new LinkedList<EnumType>();
	}
	
	/**
	 * Adds the global var to the collection of global vars of the specification
	 * @param v
	 */
	public void addGlobalVar(Var v){
		this.globalVars.add(v);
		
		// all the global vars are locks except the primitive ones
		if (!v.isPrimType()){
			Lock l = new Lock(v.getName(), this);
			locks.add(l);
		}		
	}
	
	/**
	 * Adds an Enum type to the collection of Enumtype corresponding to the shared variables
	 * @param e	the enum type to be added
	 */
	public void addEnumType(EnumType e){
		this.enums.add(e);
	}
	
	/**
	 * It sets the type enum for the given var
	 * @param v	an enum var, we set the corresponding enum type
	 */
	public void setTypeEnum(EnumVar v, LinkedList<String> values){
		if (this.globalVars.contains(v)){ // if the var is already in the collection of shared vars
			for (EnumType e:this.enums){
				if (e.checkEqValues(values)){
					e.addVar(v);
					v.setEnumType(e);
				}
				else{
					EnumType etype = new EnumType();
					etype.addValues(values);
					etype.addVar(v);
					this.enums.add(etype);
				}
			}
		}
	}
	
	/**
	 * Adds the lock to the collection of locks of the specification
	 * @param l
	 */
	public void addLock(Lock l){
		locks.add(l);
	}
	
	/**
	 * Adds the invariant to the spec
	 * @param f
	 */
	public void addInv(TemporalFormula f){
		this.invs.add(f);
	}
	
	public void addProcess(ProcessSpec p){
		this.processes.add(p);
	}
	
	public void addAllGlobalVar(LinkedList<Var> list){
		this.globalVars.addAll(list);
		
		// for each global var that it is not a primitive type we add an associated lock
		for (int i=0; i<list.size(); i++){
			if (!list.get(i).isPrimType()){
				Lock l = new Lock(list.get(i).getName(), this);
				locks.add(l);
			}
		}
	}
	
	public void addAllInv(LinkedList<TemporalFormula> list){
		invs.addAll(list);
	}
	
	public void addAllProcess(LinkedList<ProcessSpec> list){
		this.processes.addAll(list);
	}
	
	public void addInstance(String name, ProcessSpec process){
		instances.put(name, process);
	}
	
	public void addInstanceActualPar(String name, Var v){
		if (this.actualPars.get(name) == null)
			this.actualPars.put(name, new LinkedList<Var>());
		this.actualPars.get(name).add(v);
	}
	
	public void addInstanceAllActualPars(String name, LinkedList<Var> vars){
		if (this.actualPars.get(name) == null)
			this.actualPars.put(name, new LinkedList<Var>());
		this.actualPars.get(name).addAll(vars);
	}
	
	/**
	 * 
	 * @return	the local names of the locks, for every instance
	 */
	/*public HashMap<Lock,HashMap<String,String>> getLocalNameforLocks(){
		HashMap<Lock,HashMap<String,String>> result = new HashMap<Lock,HashMap<String,String>>();
			
		Iterator<String> it0 = this.instances.keySet().iterator();
		while (it0.hasNext()){
			String current = it0.next();
			ProcessSpec currentProcess = this.instances.get(current);
			currentProcess.
		}
		return result;
	}*/
	
	public HashMap<Var,HashMap<String,String>> getLocalNameforVars(){
		HashMap<Var,HashMap<String,String>> result = new HashMap<Var,HashMap<String,String>>();
		return result;		
	}
	
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return the list of all enum types of GLOBAL variables
	 */
	public LinkedList<EnumType> getEnumTypes(){
		return this.enums;
	}
	
	public ProcessSpec getProcessSpec(String instance){
		return instances.get(instance);
	}
	
	/**
	 * 
	 * @return	the name of the process associated to an instance
	 */
	public HashMap<String, String> getInstanceTypes(){
		HashMap<String, String> result = new HashMap<String, String>();
		Iterator<String> it = instances.keySet().iterator();
		while (it.hasNext()){
			String current = it.next();
			result.put(current, instances.get(current).getName());
		}
		return result;
	}

	public LinkedList<String> getGlobalVarsNames(){
		
		LinkedList<String> result = new LinkedList<String>();
		for (int i = 0; i<globalVars.size(); i++){
			result.add(globalVars.get(i).getName());
		}
		for (int i=0; i<locks.size(); i++){
			result.add(locks.get(i).getName());
		}
		return result;
	}
	
	public LinkedList<String> getGlobalVarsNamesByType(Type t){
		LinkedList<String> result = new LinkedList<String>();
		for (int i = 0; i<globalVars.size(); i++){
			if (globalVars.get(i).getType() == t)
				result.add(globalVars.get(i).getName());
		}
		return result;
	}
	
	public LinkedList<String> getGlobalNonPrimVarsNamesByType(Type t){
		LinkedList<String> result = new LinkedList<String>();
		for (int i = 0; i<globalVars.size(); i++){
			if (globalVars.get(i).getType() == t && !globalVars.get(i).isPrimType())
				result.add(globalVars.get(i).getName());
		}		
		return result;
	}
	
	
	public Var getGlobalVarByName(String name){
		for (int i=0; i<this.globalVars.size();i++){
			if (this.globalVars.get(i).getUnqualifiedName().equals(name)){
				return this.globalVars.get(i);
			}
		}
		
		for (int i=0; i<this.locks.size();i++){
			if (this.locks.get(i).getUnqualifiedName().equals(name)){
				return this.locks.get(i);
			}
		}
		
		throw new RuntimeException("Global Variable Not Found");
	}
	
	/**
	 * It says if a var is of a primitive type
	 * @param name	the name of the var
	 * @return	whether it is primitive or not
	 */
	public boolean isPrimVar(String name){
		for (int i=0; i<this.globalVars.size();i++){
			if (this.globalVars.get(i).getUnqualifiedName().equals(name))
				return this.globalVars.get(i).isPrimType();
		}
		return false; // if no such a variable we return false
	}
	
	public HashMap<String, String> getGlobalVarsTypes(){
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i<globalVars.size(); i++){
			if (globalVars.get(i).getType() == Type.BOOL)
				result.put(globalVars.get(i).getName(), "BOOL");
			if (globalVars.get(i).getType() == Type.INT)
				result.put(globalVars.get(i).getName(), "INT");
			if (globalVars.get(i).getType() == Type.ENUM)
				result.put(globalVars.get(i).getName(), "ENUM");
			if (globalVars.get(i).getType() == Type.LOCK)
				result.put(globalVars.get(i).getName(), "LOCK");
			if (globalVars.get(i).getType() == Type.PRIMBOOL)
				result.put(globalVars.get(i).getName(), "PRIMBOOL");
			if (globalVars.get(i).getType() == Type.PRIMINT)
				result.put(globalVars.get(i).getName(), "PRIMINT");
			if (globalVars.get(i).getType() == Type.ENUMPRIM)
				result.put(globalVars.get(i).getName(), "PRIMENUM");
			
		}
		// and the locks that are not variables are added
		for (int i=0; i<this.locks.size(); i++){
			if (this.locks.get(i).isOnlyLock())
				result.put(locks.get(i).getName(), "LOCK");
		}
		return result;
	}
	
	public LinkedList<String> getProcessesNames(){
		LinkedList<String> result = new LinkedList<String>();
		for (int i =0; i<processes.size(); i++){
			result.add(processes.get(i).getName());
		}
		return result;
	}
	
	public LinkedList<Lock> getLocks(){
		return locks;
	}
	
	public Formula getGlobalProperty(){
		if (invs.size() == 0)
			return (new BoolConstant(true));
		if (invs.size() == 1)
			return invs.get(0);
		else{
			Conjunction result = new Conjunction(invs.get(0), invs.get(1));
			int i = 2;
			while (i<invs.size()){
				result = new Conjunction(result, invs.get(i));
			}
			return result;
		}
	}
	
	public ProcessSpec getProcessByName(String name){
		for (int i=0; i<this.processes.size();i++){
			if (this.processes.get(i).getName().equals(name)){
				return this.processes.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param instance	the instance to which we want to obtain the actual parameters
	 * @return	the list of the parameters of the instance
	 */
	public LinkedList<String> getActualPars(String instance){
		LinkedList<String> result = new LinkedList<String>();
		if (this.actualPars.get(instance) != null){
			Iterator<Var> it = this.actualPars.get(instance).iterator();
			while(it.hasNext()){
				Var v = it.next();
				result.add(v.getName());
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param 	the instance for which we want to obtain the actual parameters
	 * @return	Returns the ith actual parameter of the instance
	 */
	public Var getActualIthPar(String instance, int k){
		if (this.actualPars.get(instance) != null){
			Iterator<Var> it = this.actualPars.get(instance).iterator();
			int i=0;
			while(it.hasNext()){
				Var v = it.next();
				if (i == k)
					return v;
				i++;
			}
		}
		throw new RuntimeException("Instance has no par");
	}
	
	/**
	 * 
	 * @param instance	the instance for which we want to obtain the formal par
	 * @param k			the number of parameter we want to obtain
	 * @return			the	ith formal parameter of the given instance 
	 */
	public Var getFormalIthPar(String instance, int k){
		ProcessSpec myProcess = this.getProcessSpec(instance);
		return myProcess.getIthFormalPar(k);
	}
	
	
	/**
	 * 
	 * @param name
	 * @return the type of a given global var, it returns ERROR if the variable is not in the spec
	 */
	public Type getGlobalVarType(String name){

		for (int i=0; i<this.globalVars.size(); i++){
			if (this.globalVars.get(i).getName().equals(name))
				return this.globalVars.get(i).getType();
		}
		// they could be locks
		for (int i=0; i<this.locks.size(); i++){
			if (this.locks.get(i).getName().equals(name))
				return Type.LOCK;
		}
		return Type.ERROR;
	}
	
	/** 
	 * @return	the metamodel of the given process, returns the empty string in the case of 
	 * 			inexistent Process
	 */
	public String metamodelToString(String process, String templateDir, int scope){

		for (int i=0; i<processes.size(); i++){
			if (processes.get(i).getName().equals(process)){
				return processes.get(i).metamodelToString(templateDir, scope);
			}
		}
		return "";
	}
	
	/**
	 * Writes down all the metamodels into a given file, this method will be replaced for metamodelTo..
	 * @param file	
	 * @param templateDir
	 */
	public void generateMetamodels(FileWriter file, String templateDir, int scope){
		for (int i=0; i<this.processes.size(); i++){
			processes.get(i).generateMetamodel(file, templateDir, scope);
		}
	}
	
	public String toString(){
		String result = "";
		result += name + "\n";
		//System.out.println(this.globalVars);
		
		for(int i = 0; i<globalVars.size(); i++){
			result += globalVars.get(i).toString();
		}
		for(int i = 0; i<processes.size(); i++){
			result += processes.get(i).toString();
		}
		result += "\nmain(){\n";
		
		for(int i = 0; i<invs.size(); i++){
			result += "    property: " +  invs.get(i).toString() + "\n";
		}
		result += "}";
		return result;
	}
	
	/**
	 * Restrict a specification to a subset of the instances
	 * @param instances
	 * @return	the restricted specification
	 */
	public Spec restrictTo(LinkedList<String> instances){
		Spec result = new Spec(this.name);
		LinkedList<String> toRemove = new LinkedList<String>();
		Iterator<String> it = this.instances.keySet().iterator();
		while (it.hasNext()){
			String current = it.next();
			if (!instances.contains(current))
				toRemove.add(current);
		}
		result.addAllGlobalVar(this.globalVars);
		// for now the locks are inserted as global variables, this must be fixed at some point
		
		for (int i=0; i<instances.size(); i++){
			result.addInstance(instances.get(i), this.instances.get(instances.get(i)));
			result.addProcess(this.instances.get(instances.get(i)));
		}
		
		for (int i=0; i<invs.size(); i++){
			result.addInv((TemporalFormula) invs.get(i).removeVarOwnedBy(toRemove));
		}
		
		Iterator<String> it1 = actualPars.keySet().iterator();
		while (it1.hasNext()){
			String current = it1.next();
			result.addInstanceAllActualPars(current, actualPars.get(current));
		}
		return result;
	}

}
