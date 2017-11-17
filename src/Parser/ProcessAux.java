package Parser;
import java.util.*;
import Utils.*;

import FormulaSpec.*;
import Spec.*;

// NOTE: ADD ACTIONS

public class ProcessAux {
	private String name; // the name
	private HashMap<String, Type> localVars; // the local vars
	//private HashMap<String, Type> pars; // the parameters of the process
	private LinkedList<Pair<String, Type>> pars; // the parameters of the process, it is a linked list since the order of apparence matters
	LinkedList<ActionAux> actions; // the actions of the process
	private ExprAux init;
	private LinkedList<ExprAux> invs; // the local invariants
	private SpecAux mySpec;
	private int line;
	private String errors; // 
	private boolean duplicatedVars;
	
	
	public ProcessAux(int line){
		this.name = "";
		this.localVars = new HashMap<String,Type>();
		//this.pars = new HashMap<String, Type>();
		this.pars = new LinkedList<Pair<String,Type>>();
		this.invs = new LinkedList<ExprAux>();
		this.actions = new LinkedList<ActionAux>();
		this.line = line;
		this.errors = "";
		this.duplicatedVars = false;
	}
	
	public ProcessAux(String name, int line){
		this.name = name;
		this.localVars = new HashMap<String,Type>();
		//this.pars = new HashMap<String, Type>();
		this.pars = new LinkedList<Pair<String,Type>>();
		this.invs = new LinkedList<ExprAux>();
		this.actions = new LinkedList<ActionAux>();
		this.line = line;
		this.errors = "";
		this.duplicatedVars = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInit(ExprAux i){
		this.init = i;
	}
	
	public ExprAux getInit(){
		return this.init;
	}
	
	public SpecAux getSpec(){
		return this.mySpec;
	}
	
	public boolean containsPar(String name){
		for (int i=0; i < pars.size(); i++){
			if (pars.get(i).getFirst().equals(name))
				return true;
		}
		return false;
	}
	
	public void setSpec(SpecAux spec){
		this.mySpec =  spec;
	}
	
	public void addError(String error){
		this.errors = this.errors + error + "\n";
	}
	
	public String getErrors(){
		return this.errors;
	}
	
	
	public boolean getDuplicatedVars(){
		return this.duplicatedVars;
	}
	
	public void addAction(ActionAux a){
		a.setMyProcess(this);
		actions.add(a);
	}
	
	public void addAllActions(LinkedList<ActionAux> list){
		for (int i=0; i<list.size(); i++){
			list.get(i).setMyProcess(this);
		}
		actions.addAll(list);
	}
	
	public HashMap<String, Type> getLocalVars(){
		return this.localVars;
	}
	
	public void addLocalVar(String name, Type type){
		if (localVars.containsKey(name)){
			this.duplicatedVars = true;
			this.addError("Duplicated variable in Process, line: "+line);
		}
		else{
			localVars.put(name, type);
		}
	}
	
	public void addAllLocalVars(HashMap<String, Type> h){
		Set<String> keys = h.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			localVars.put(current, h.get(current));			
		}

	}
	
	public void addParameter(String name, Type t){
		if (localVars.containsKey(name) || this.containsPar(name)){
			this.duplicatedVars = true;
			this.addError("Duplicated variable in Process, line: "+line);
		}
		else{
			this.pars.add(new Pair<String,Type>(name, t));
		}
	}
	
	public void addAllParameters(LinkedList<Pair<String, Type>> l){		
		for (int i=0; i<l.size(); i++){
			this.addParameter(l.get(i).getFirst(), l.get(i).getSecond());
		}
	}
	
	public int getNumberPars(){
		return this.pars.size();
	}
	
	public Type getParType(int i){
		if (i<this.pars.size())
			return pars.get(i).getSecond();
		else
			return Type.ERROR;
	}
	
	public void addInvs(ExprAux i){
		invs.add(i);
	}
	
	public void addAllInvs(LinkedList<ExprAux> list){
		invs.addAll(list);
	}
	
	public boolean declaredVar(String name){
		return localVars.containsKey(name);
	}
	
	public Type getVarType(String name){
		if (this.localVars.containsKey(name)){
			return this.localVars.get(name);
		}
		else{
			return Type.ERROR;
		}
	}
	
	public Type getParType(String name){
		for (int i=0; i<this.pars.size(); i++){
			if (this.pars.get(i).getFirst().equals(name)){
				return this.pars.get(i).getSecond();
			}
		}
		return Type.ERROR;
	}
	
	public Type getLocalVarType(String name){
		return this.localVars.get(name);
	}
	
	/**
	 * Checks if the process is well formed
	 * @param table
	 * @return
	 */
	public boolean isWellFormed(HashMap<String, Type> table){
		boolean ok = true;
		//boolean invsOK = true;
		//String invError = "";
		for (int i = 0; i<invs.size(); i++){
			if (invs.get(i).getType(table, mySpec, this.name) != Type.BOOL){
				ok = false;
				this.addError("Type Error in Invariant, line:" + invs.get(i).getLine());
			}	
		}			
		for (int i=0; i<actions.size(); i++){
			if (!actions.get(i).isWellFormed(table, mySpec)){
				ok = false;
				this.addError(actions.get(i).getErrors());
			}
		}
		Type initType = init.getType(table, mySpec, this.name);
		
		if (initType == Type.ERROR){
			this.addError(init.getError());
			ok = false;
		}
		if (initType == Type.INT){
			this.addError("Initial condition is not a boolean formula, line: " + init.getLine());
			ok = false;
		}
		return ok;
	}
	
	/**
	 * 
	 * @param table
	 * @return	The process corresponding to this expression
	 */
	public ProcessSpec getProcessSpec(HashMap<String, Type> table){
		ProcessSpec result = new ProcessSpec(this.name);
		
		// we add the actions
		for (int i=0; i<actions.size(); i++){
			Action currentAction = actions.get(i).getAction(table, mySpec);
			result.addAction(currentAction);
			currentAction.setProcess(result);
		}
		
		// we add the local vars
		Set<String> vars = localVars.keySet();
		Iterator<String> it = vars.iterator();
		while (it.hasNext()){
			String currentVar = it.next();
			if (table.get(currentVar) == Type.INT){
				IntVar v = new IntVar(currentVar);
				result.addLocalVar(v);
			}
			if (table.get(currentVar) == Type.BOOL){
				BoolVar v = new BoolVar(currentVar);
				result.addLocalVar(v);
			}		
		}
		
		// we add the parameters
		for (int i=0; i<pars.size(); i++){
			if (pars.get(i).getSecond() == Type.BOOL){
				BoolVar v = new BoolVar(pars.get(i).getFirst());
				result.addPar(v);
			}
			if (pars.get(i).getSecond() == Type.INT){
				IntVar v = new IntVar(pars.get(i).getFirst());
				result.addPar(v);
			}	
		}
		
		// we add the invariants
		for (int i=0; i<invs.size();i++){
			result.addInv((TemporalFormula) invs.get(i).getExpr(table));
		}
		
		// and the initial condition
		result.setInit((Formula) init.getExpr(table));
		return result;
	}
}
