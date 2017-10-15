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
	private LinkedList<Var> globalVars; // the global vars
	private LinkedList<Lock> locks;
	private LinkedList<TemporalFormula> invs; // the invariants
	private HashMap<String, ProcessSpec> instances; // the instances in the specification
	
	public Spec(String name){
		this.name = name;
		this.processes = new LinkedList<ProcessSpec>();
		this.globalVars = new LinkedList<Var>();
		this.locks = new LinkedList<Lock>();
		this.invs = new LinkedList<TemporalFormula>();
		this.instances = new HashMap<String, ProcessSpec>();
	}
	
	public void addGlobalVar(Var v){
		this.globalVars.add(v);
		// for now all the global vars are locks
		Lock l = new Lock(v.getName(), this);
		locks.add(l);
	}
	
	public void addInv(TemporalFormula f){
		this.invs.add(f);
	}
	
	public void addProcess(ProcessSpec p){
		this.processes.add(p);
	}
	
	public void addAllGlobalVar(LinkedList<Var> list){
		this.globalVars.addAll(list);
		for (int i=0; i<list.size(); i++){
			Lock l = new Lock(list.get(i).getName(), this);
			locks.add(l);
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
		return result;
	}
	
	public HashMap<String, String> getGlobalVarsTypes(){
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i<globalVars.size(); i++){
			if (globalVars.get(i).getType() == Type.BOOL)
				result.put(globalVars.get(i).getName(), "BOOL");
			if (globalVars.get(i).getType() == Type.INT)
				result.put(globalVars.get(i).getName(), "INT");
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
	
	/** 
	 * @return	the metamodel of the given process, returns the empty string in the case of 
	 * 			inexistent Process
	 */
	public String metamodelToString(String process, String templateDir){

		for (int i=0; i<processes.size(); i++){
			if (processes.get(i).getName().equals(process)){
				return processes.get(i).metamodelToString(templateDir);
			}
		}
		return "";
	}
	
	/**
	 * Writes down all the metamodels into a gieven file, this method will be replaced for metamodelTo..
	 * @param file	
	 * @param templateDir
	 */
	public void generateMetamodels(FileWriter file, String templateDir){
		for (int i=0; i<this.processes.size(); i++){
			processes.get(i).generateMetamodel(file, templateDir);
		}
	}
	
	public String toString(){
		String result = "";
		result += name + "\n";
		System.out.println(this.globalVars);
		
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

}
