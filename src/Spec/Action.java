package Spec;
import java.util.*;

import FormulaSpec.*;

import java.io.*;
/**
 * 
 * @author Pablo
 *
 */
public class Action {
	String name; // an action has a name
	Precondition pre; // a precondition
	Postcondition post;  // a postcondition
	LinkedList<Var> frame; // a frame condition
	boolean isLocal;
	ProcessSpec process; // the name of the process where this action resides
	
	/**
	 * Basic constructor
	 * @param name
	 * @param pre
	 * @param post
	 */
	public Action(ProcessSpec process, String name, Precondition pre, Postcondition post, boolean isLocal) {
		//super();
		this.process = process;
		this.name = name;
		this.pre = pre;
		this.post = post;
		this.frame = new LinkedList<Var>();
		this.isLocal = isLocal;
	}

	/**
	 * Another constructor
	 * @param name
	 * @param pre
	 * @param post
	 */
	public Action(String name, Precondition pre, Postcondition post, boolean isLocal) {
		//super();
		this.process = process;
		this.name = name;
		this.pre = pre;
		this.post = post;
		this.frame = new LinkedList<Var>();
		this.isLocal = isLocal;
	}
	
	/**
	 * Setter for name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return	the precondition of the method
	 */
	public Precondition getPrecondition() {
		return pre;
	}

	public void setPre(Precondition pre) {
		this.pre = pre;
	}

	/**
	 * 
	 * @return	te postcondition of the method
	 */
	public Postcondition getPostcondition() {
		return post;
	}

	
	public boolean getIsLocal(){
		return this.isLocal;
	}
	/**
	 * Setter for postcondition
	 * @param post
	 */
	public void setPost(Postcondition post) {
		this.post = post;
	}
	
	/**
	 * Setter for the process
	 * @param process
	 */
	public void setProcess(ProcessSpec process){
		this.process = process;
	}
	
	/**
	 * Generates the Alloy code for the action
	 * @param file
	 */
	public void generateAlloy(FileWriter file){
		
	}

	/**
	 * @return 	a textual representation of the precondition in Alloy code
	 */
	public String getPre(){
		String state = "s";
		String metaName = process.getName() + "Meta";	
		String result = this.pre.toAlloy(metaName, state);
		return result;
	}
	
	public String getPost(){
		String state = "s'";
		String metaName = process.getName() + "Meta";	
		String result = this.post.toAlloy(metaName, state);
		return result;
	}
	
	public LinkedList<String> getPostClauses(){
		LinkedList<String> result = new LinkedList<String>();
		LinkedList<Clause> qs = post.getClauses();
		for (int i=0; i<qs.size(); i++){
			String clause = qs.get(i).toAlloy(process.getName()+"Meta", "s'");
			result.add(clause);
		}
		return result;
	}
	
	public void addVarToFrame(Var v){
		this.frame.add(v);
	}
	
	/**
	 * 
	 * @return	The variables in the frame of the action
	 */
	public LinkedList<String> getFrame(){
		LinkedList<String> result = new LinkedList<String>();
		for (int  i=0; i<this.frame.size(); i++){
			result.add(this.frame.get(i).getName());
		}
		return result;
	}
	
	/**
	 * 
	 * @return	the collection of the LOCAL vars names that are not in the action frame
	 */
	private LinkedList<String> getAllFrameComplement(){
		LinkedList<String> vars  = new LinkedList<String>();
		LinkedList<String> frameNames = this.getFrame();
		vars.addAll(process.getLocalVarsNames());
		//vars.addAll(process.getSharedVarsNames());
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<vars.size(); i++){
			String current = vars.get(i);
			if (!frameNames.contains(current)){
				result.add(current);
			}
		}
		return result;
	}
	
	/**
	 * These methods are used for generating the Alloy Template, to improve the Alloy code 
	 * we need one per type
	 * @return	the collection of local BOOLEAN vars that are not in the action frame
	 */
	private LinkedList<String> getAllBooleanFrameComplement(){
		LinkedList<String> localVars  = process.getLocalVarsNamesByType(Type.BOOL); // the local vars
		LinkedList<String> sharedVars  = process.getSharedVarsNamesByType(Type.BOOL); // the shared vars 
		LinkedList<String> frameNames = this.getFrame();
		//vars.addAll(process.getLocalVarsNamesByType(Type.BOOL)); // adds the local vars
		//vars.addAll(process.getSharedVarsNamesByType(Type.BOOL)); // adds the shared vars
		LinkedList<String> result = new LinkedList<String>(); // the list with the result
		
		// we add the primitive booleans
		sharedVars.addAll(process.getSharedVarsNamesByType(Type.PRIMBOOL));
		
		// we deal with the local vars
		for (int i=0; i<localVars.size(); i++){
			String current = localVars.get(i);
			if (!frameNames.contains(current)){
				result.add(current);
			}
		}
		
		// we deal with the shared vars
		for (int i=0; i<sharedVars.size(); i++){
			String current = sharedVars.get(i);
			if (!frameNames.contains(current) && process.usesSharedVar(current)){ // we need to check if the shared var is used
				result.add(current);
			}
		}
		
		// and the parameters:
		for (int i=0; i<process.getBoolParNamesWithLock().size(); i++){
			if (!frameNames.contains(process.getBoolParNamesWithLock().get(i)))
				result.add(process.getBoolParNamesWithLock().get(i));
		}
		
		for (int i=0; i<process.getOwnedBoolParNames().size(); i++){
			if (!frameNames.contains(process.getOwnedBoolParNames().get(i)))
				result.add(process.getOwnedBoolParNames().get(i));
		}
		
		for (int i=0; i<process.getBoolPrimParNames().size(); i++){
			if (!frameNames.contains(process.getBoolPrimParNames().get(i)))
				result.add(process.getBoolPrimParNames().get(i));
		}
		
		
		
		return result;
	}
	
	/**
	 * These methods are used for generating the Alloy Template, to improve the Alloy code 
	 * we need one per type...
	 * @return	the collection of local ENUM vars that are not in the action frame
	 */
	private LinkedList<String> getAllEnumFrameComplement(){
		LinkedList<String> localVars  = process.getLocalVarsNamesByType(Type.ENUM); // the local vars
		LinkedList<String> sharedVars  = process.getSharedVarsNamesByType(Type.ENUM); // the shared vars 
		LinkedList<String> frameNames = this.getFrame();
		//vars.addAll(process.getLocalVarsNamesByType(Type.BOOL)); // adds the local vars
		//vars.addAll(process.getSharedVarsNamesByType(Type.BOOL)); // adds the shared vars
		LinkedList<String> result = new LinkedList<String>(); // the list with the result
		
		sharedVars.addAll(process.getSharedVarsNamesByType(Type.ENUMPRIM)); // we add the PRIMINTS
		
		// we deal with the local vars
		for (int i=0; i<localVars.size(); i++){
			String current = localVars.get(i);
			if (!frameNames.contains(current)){
				result.add(current);
			}
		}
		
		// we deal with the shared vars
		for (int i=0; i<sharedVars.size(); i++){
			String current = sharedVars.get(i);
			if (!frameNames.contains(current) && process.usesSharedVar(current)){ // we need to check if the shared var is used
				result.add(current);
			}
		}
		

		// and the parameters:
		for (int i=0; i<process.getEnumParNamesWithLock().size(); i++){
			if (!frameNames.contains(process.getIntParNamesWithLock().get(i)))
				result.add(process.getIntParNamesWithLock().get(i));
		}
		
		for (int i=0; i<process.getOwnedEnumParNames().size(); i++){
			if (!frameNames.contains(process.getOwnedEnumParNames().get(i)))
				result.add(process.getOwnedEnumParNames().get(i));
		}
				
		for (int i=0; i<process.getEnumPrimParNames().size(); i++){
			if (!frameNames.contains(process.getEnumPrimParNames().get(i)))
				result.add(process.getEnumPrimParNames().get(i));
		}
		
		return result;
	}
	
	
	
	/**
	 * These methods are used for generating the Alloy Template, to improve the Alloy code 
	 * we need one per type...
	 * @return	the collection of local INT vars that are not in the action frame
	 */
	private LinkedList<String> getAllIntFrameComplement(){
		
		LinkedList<String> localVars  = process.getLocalVarsNamesByType(Type.INT); // the local vars
		LinkedList<String> sharedVars  = process.getSharedVarsNamesByType(Type.INT); // the shared vars 
		LinkedList<String> frameNames = this.getFrame();
		//vars.addAll(process.getLocalVarsNamesByType(Type.BOOL)); // adds the local vars
		//vars.addAll(process.getSharedVarsNamesByType(Type.BOOL)); // adds the shared vars
		LinkedList<String> result = new LinkedList<String>(); // the list with the result
		
		sharedVars.addAll(process.getSharedVarsNamesByType(Type.PRIMINT)); // we add the PRIMINTS
		
		// we deal with the local vars
		for (int i=0; i<localVars.size(); i++){
			String current = localVars.get(i);
			if (!frameNames.contains(current)){
				result.add(current);
			}
		}
		
		// we deal with the shared vars
		for (int i=0; i<sharedVars.size(); i++){
			String current = sharedVars.get(i);
			if (!frameNames.contains(current) && process.usesSharedVar(current)){ // we need to check if the shared var is used
				result.add(current);
			}
		}
		

		// and the parameters:
		for (int i=0; i<process.getIntParNamesWithLock().size(); i++){
			if (!frameNames.contains(process.getIntParNamesWithLock().get(i)))
				result.add(process.getIntParNamesWithLock().get(i));
		}
		
		for (int i=0; i<process.getOwnedIntParNames().size(); i++){
			if (!frameNames.contains(process.getOwnedIntParNames().get(i)))
				result.add(process.getOwnedIntParNames().get(i));
		}
				
		for (int i=0; i<process.getIntPrimParNames().size(); i++){
			if (!frameNames.contains(process.getIntPrimParNames().get(i)))
				result.add(process.getIntPrimParNames().get(i));
		}
		
		return result;
	}
	
	/**
	 * This method is mainly useful for improving the efficiency of alloy searching
	 * @return	The linked list containing the complement of the frame, when it is a singleton, 
	 * 			otherwise it returns the empty list 
	 */
	public LinkedList<String> getSingletonFrameComplement(){
		LinkedList<String> result = getAllFrameComplement();
		LinkedList<String> lockList = getLockFrameComplement();
		if (result.size() == 1 && lockList.size()==0)
			return result;
		else
			return new LinkedList<String>(); // else we return the empty list
	}
	
	/**
	 * Similar as above but restricted to BOOL
	 * @return The linked list containing the complement of the frame, when it is a singleton,
	 * 		   otherwise it returns the empty list 
	 */
	public LinkedList<String> getSingletonBooleanFrameComplement(){
		LinkedList<String> result = getAllBooleanFrameComplement();
		LinkedList<String> lockList = getLockFrameComplement();
		if (result.size() == 1 && lockList.size()==0)
			return result;
		else
			return new LinkedList<String>(); // else we return the empty list
	}
	
	/**
	 * Similar as above but restricted to INT
	 * @return The linked list containing the complement of the frame, when it is a singleton,
	 * 		   otherwise it returns the empty list 
	 */
	public LinkedList<String> getSingletonIntFrameComplement(){
		LinkedList<String> result = getAllIntFrameComplement();
		LinkedList<String> lockList = getLockFrameComplement();
		if (result.size() == 1 && lockList.size()==0)
			return result;
		else
			return new LinkedList<String>(); // else we return the empty list
	}
	
	/**
	 * Similar as above but restricted to ENUM
	 * @return The linked list containing the complement of the frame, when it is a singleton,
	 * 		   otherwise it returns the empty list 
	 */
	public LinkedList<String> getSingletonEnumFrameComplement(){
		LinkedList<String> result = getAllEnumFrameComplement();
		LinkedList<String> lockList = getLockFrameComplement();
		if (result.size() == 1 && lockList.size()==0)
			return result;
		else
			return new LinkedList<String>(); // else we return the empty list
	}
	
	/**
	 * 
	 * @return the list of variables not mentioned in the frame, if this set has size 1 we use 
	 *         the function getSingletonFrameComplement(), this improves the use of StringTemplate
	 * DEPRECATED		   
	 */
	public LinkedList<String> getFrameComplement(){
		LinkedList<String> result = getAllFrameComplement();
		if (result.size() > 1 || getLockFrameComplement().size()>0)
			return result;
		else
			return new LinkedList<String>(); // emptylist
	}
	
	/**
	 * 
	 * @return similar as above but restricted to BOOL variables
	 */
	public LinkedList<String> getBooleanFrameComplement(){
		LinkedList<String> result = getAllBooleanFrameComplement();
		if (result.size() > 1 || getLockFrameComplement().size()>0)
			return result;
		else
			return new LinkedList<String>(); // emptylist
	}
	
	/**
	 * 
	 * @return similar as above but restricted to INT variables
	 */
	public LinkedList<String> getIntFrameComplement(){
		LinkedList<String> result = getAllIntFrameComplement();
		if (result.size() > 1 || getLockFrameComplement().size()>0)
			return result;
		else
			return new LinkedList<String>(); // emptylist
	}
	
	/**
	 * 
	 * @return similar as above but restricted to ENUM variables
	 */
	public LinkedList<String> getEnumFrameComplement(){
		LinkedList<String> result = getAllEnumFrameComplement();
		if (result.size() > 1 || getLockFrameComplement().size()>0)
			return result;
		else
			return new LinkedList<String>(); // emptylist
	}
	
	/**
	 * 
	 * @return	the list of variables with locks that are not mentioned in the frame
	 */
	public LinkedList<String> getLockFrameComplement(){
		LinkedList<String> vars  = new LinkedList<String>();
		LinkedList<String> frameNames = this.getFrame();
		//vars.addAll(process.getLocalVarsNames());
		vars.addAll(process.getSharedVarsNamesByTypeWithLock(Type.BOOL)); // only non primitive types
		vars.addAll(process.getSharedVarsNamesByTypeWithLock(Type.INT));
		vars.addAll(process.getSharedVarsNamesByTypeWithLock(Type.ENUM));
		vars.addAll(process.getBoolParNamesWithLock());
		vars.addAll(process.getIntParNamesWithLock());
		vars.addAll(process.getOnlyLocksNames());
		vars.addAll(process.getLockParNames());
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<vars.size(); i++){
			String current = vars.get(i);
			if (!frameNames.contains(current) && process.usesSharedVar(current)){
				result.add(current);
			}
		}
		return result;
	}
	
	public boolean usesVar(String var){
		if (this.pre.usesVar(var))
			return true;
		if (this.post.usesVar(var))
			return true;
		for (int i=0; i<frame.size();i++){
			if (frame.get(i).equals(var))
				return true;
		}
		return false;
	}
	
	
	public String toString(){
		String result = "";
		result += this.name+"()"+"\n";
		result += this.frame.toString() + "\n";
		result += this.pre.toString() + "\n";
		result += this.post.toString() + "\n";
		return result;
	}
	
}
