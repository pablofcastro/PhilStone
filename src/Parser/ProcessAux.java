package Parser;
import java.util.*;
import Utils.*;

import FormulaSpec.*;
import Spec.*;

// NOTE: ADD ACTIONS

public class ProcessAux {
	private String name; // the name
	private HashMap<String, Type> localVars; // the local vars
	private HashMap<String, LinkedList<String>> enumVars; // for the enum vars we have the list of possible values, the position of the values is important
	//private HashMap<String, Type> pars; // the parameters of the process
	private LinkedList<Pair<String, Type>> pars; // the parameters of the process, it is a linked list since the order of apparence matters
	private LinkedList<String> primPars; // the list of prim pars, note that we dont need this for local vars 
	private LinkedList<String> ownedVars; // the list of owned vars
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
		this.enumVars = new HashMap<String, LinkedList<String>>();
		this.primPars = new LinkedList<String>();
		//this.pars = new HashMap<String, Type>();
		this.pars = new LinkedList<Pair<String,Type>>();
		this.invs = new LinkedList<ExprAux>();
		this.actions = new LinkedList<ActionAux>();
		this.line = line;
		this.errors = "";
		this.duplicatedVars = false;
		this.ownedVars = new LinkedList<String>();
	}
	
	public ProcessAux(String name, int line){
		this.name = name;
		this.localVars = new HashMap<String,Type>();
		this.enumVars = new HashMap<String, LinkedList<String>>();
		//this.pars = new HashMap<String, Type>();
		this.pars = new LinkedList<Pair<String,Type>>();
		this.primPars = new LinkedList<String>();
		this.invs = new LinkedList<ExprAux>();
		this.actions = new LinkedList<ActionAux>();
		this.line = line;
		this.errors = "";
		this.duplicatedVars = false;
		this.ownedVars = new LinkedList<String>();
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
	
	public LinkedList<Pair<String, Type>> getPars(){
		return this.pars;
	}
	
	public boolean containsPar(String name){
		for (int i=0; i < pars.size(); i++){
			if (pars.get(i).getFirst().equals(name))
				return true;
		}
		return false;
	}
	
	public boolean isPrimPar(String name){
		return this.primPars.contains(name);
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
			if (type==Type.PRIMBOOL) // if it is a primitive boolean then we just add it as a boolean
				localVars.put(name, Type.BOOL);
			if (type==Type.PRIMINT) // similarly for int
				localVars.put(name, Type.INT);
			if (type==Type.ENUMPRIM)
				localVars.put(name, Type.ENUM);
			else
				localVars.put(name, type); // otherwise we add it with the given type
		}
	}
	
	public void addAllLocalVars(HashMap<String, Type> h){
		Set<String> keys = h.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()){
			String current = it.next();
			if (h.get(current) == Type.PRIMBOOL) // volatile bools in processes lack sense
				localVars.put(current, h.get(Type.BOOL));
			if (h.get(current) == Type.PRIMINT)	// volatile ints  in processes lack sense
				localVars.put(current, h.get(Type.INT));
			if (h.get(current) == Type.ENUMPRIM)	// volatile ints  in processes lack sense
				localVars.put(current, h.get(Type.ENUM));
			else{
				localVars.put(current, h.get(current));	
			}
		}

	}
	
	/**
	 * This method set the collection of possible values to a enum var
	 * we assume that var names are added before their enum values
	 * @param varName	the name of the enum var
	 * @param values	the list of its possible values
	 */
	public void addValuesToEnum(String varName, LinkedList<String> values, Type type){
		
		if (localVars.containsKey(varName))
			throw new RuntimeException("Enum Var was declared twice: "+varName);
		else{
			this.addLocalVar(varName, type); // we add the var to the collection of shared variables
			this.enumVars.put(varName, values); // we add the values corresponding to this shared var
		}
	}
	
	/**
	 * This methods adds the values corresponding to a collection of enum vars
	 * @param values	the collection of enum variables to which the values will be added
	 */
	public void addAllValuesToEnum(HashMap<String, Pair<LinkedList<String>,Type>> values){
		for (String v:values.keySet()){
			this.addValuesToEnum(v, values.get(v).getFirst(), values.get(v).getSecond());
		}
	}
	
	public void addParameter(String name, Type t){
		if (localVars.containsKey(name) || this.containsPar(name)){
			this.duplicatedVars = true;
			this.addError("Duplicated variable in Process, line: "+line);
		}
		else{
			if (t==Type.BOOL || t==Type.INT || t==Type.LOCK || t==Type.ENUM){
				this.pars.add(new Pair<String,Type>(name, t));
				return;
			}
			if (t==Type.PRIMBOOL){
				this.pars.add(new Pair<String,Type>(name, Type.BOOL)); // we added as an INT 
				this.primPars.add(name); // and we add it as a primitive type
				return;
			}
			if (t==Type.PRIMINT){
				this.pars.add(new Pair<String,Type>(name, Type.INT)); // we added as an INT 
				this.primPars.add(name); // and we add it as a primitive type
				return;
			}	
			if (t==Type.ENUMPRIM){
				this.pars.add(new Pair<String,Type>(name, Type.ENUM)); // we added as an INT 
				this.primPars.add(name); // and we add it as a primitive type
				return;
			}		
		}
	}
	
	public void addAllParameters(LinkedList<Pair<String, Type>> l){		
		for (int i=0; i<l.size(); i++){
			this.addParameter(l.get(i).getFirst(), l.get(i).getSecond());
		}
	}
	
	public void addAllOwnedVars(LinkedList<String> list){
		for (int i=0; i<list.size(); i++){
			this.ownedVars.add(list.get(i));
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
		// we check if it is a local var
		if (this.localVars.containsKey(name)){
			return this.localVars.get(name);
		}
		// we check if it is a parameter
		for (int i=0; i<this.pars.size(); i++){
			if (this.pars.get(i).getFirst().equals(name))
				return this.pars.get(i).getSecond();
		}
		// otherwise we return an error
		return Type.ERROR;
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
		if (localVars.containsKey(name))
			return this.localVars.get(name);
		else 
			return Type.ERROR;
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
		
		// check the owned vars
		for (int i=0; i<this.ownedVars.size(); i++){
			if (!this.mySpec.checkVarIsGlobal(this.ownedVars.get(i))){
				ok = false;
				this.addError("Owned var: "+ this.ownedVars.get(i) + " is not a shared var" );
			}
		}
		
		//check the invariants
		for (int i = 0; i<invs.size(); i++){
			if (invs.get(i).getType(table, mySpec, this.name) != Type.BOOL){
				ok = false;
				this.addError("Type Error in Invariant, line:" + invs.get(i).getLine());
			}	
		}	
		
		// check the actions 
		for (int i=0; i<actions.size(); i++){
			if (!actions.get(i).isWellFormed(table, mySpec)){
				ok = false;
				this.addError(actions.get(i).getErrors());
			}
		}
		Type initType = init.getType(table, mySpec, this.name);
		
		// check the init condition
		if (initType == Type.ERROR){
			this.addError(init.getError());
			ok = false;
		}
		if (initType != Type.BOOL){
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
	public ProcessSpec getProcessSpec(HashMap<String, Type> t){
		ProcessSpec result = new ProcessSpec(this.name);
		HashMap<String, Type> table = new HashMap<String, Type>();
		for (String val:t.keySet()){
			table.put(val, t.get(val));
		}
		
		// we add the parameters in the table
		for (Pair<String, Type> p:this.pars){
			if (table.containsKey(p.getFirst()))
				throw new RuntimeException("repeated var name");
			table.put(p.getFirst(), p.getSecond());
		}
		
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
			if (table.get(currentVar) == Type.ENUM){
				EnumVar v = new EnumVar(currentVar);
				result.addLocalVar(v);
			}	
		}
		
		// we add the parameters
		for (int i=0; i<pars.size(); i++){
			if (pars.get(i).getSecond() == Type.BOOL){
				BoolVar v = new BoolVar(pars.get(i).getFirst());
				if (this.isPrimPar(pars.get(i).getFirst()))
					v.setIsPrim(true);
				result.addPar(v);
			}
			if (pars.get(i).getSecond() == Type.INT){
				IntVar v = new IntVar(pars.get(i).getFirst());
				if (this.isPrimPar(pars.get(i).getFirst()))
					v.setIsPrim(true);
				result.addPar(v);
			}	
			if (pars.get(i).getSecond() == Type.ENUM){
				EnumVar v = new EnumVar(pars.get(i).getFirst());
				if (this.isPrimPar(pars.get(i).getFirst()))
					v.setIsPrim(true);
				result.addPar(v);
			}	
			// We add the parameters that are locks
			if (pars.get(i).getSecond() == Type.LOCK){
				Lock l = new Lock(pars.get(i).getFirst(), true);
				result.addPar(l);
			}
		}
		
		
		for (int i=0; i<this.ownedVars.size(); i++){
			result.addOwnedVar(this.ownedVars.get(i));
			//result.addOwnedVar(this.ownedVars.get(i)); 
		}
		
		// we add the invariants
		for (int i=0; i<invs.size();i++){
			result.addInv((TemporalFormula) invs.get(i).getExpr(table, this.mySpec));
		}
		
		// and the initial condition
		result.setInit((Formula) init.getExpr(table, this.mySpec));
		return result;
	}
}
