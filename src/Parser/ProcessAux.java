package Parser;
import java.util.*;

import FormulaSpec.*;
import Spec.*;

// NOTE: ADD ACTIONS

public class ProcessAux {
	private String name; // the name
	private HashMap<String, Type> localVars; // the local vars
	private HashMap<String, Type> pars; // the parameters of the process
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
		this.pars = new HashMap<String, Type>();
		this.invs = new LinkedList<ExprAux>();
		this.actions = new LinkedList<ActionAux>();
		this.line = line;
		this.errors = "";
		this.duplicatedVars = false;
	}
	
	public ProcessAux(String name, int line){
		this.name = name;
		this.localVars = new HashMap<String,Type>();
		this.pars = new HashMap<String, Type>();
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
		for (int i=0; i<actions.size(); i++){
			Action currentAction = actions.get(i).getAction(table, mySpec);
			result.addAction(currentAction);
			currentAction.setProcess(result);
		}
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
		for (int i=0; i<invs.size();i++){
			result.addInv((TemporalFormula) invs.get(i).getExpr(table));
		}
		result.setInit((Formula) init.getExpr(table));
		return result;
	}
}
