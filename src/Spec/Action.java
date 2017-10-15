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
	
	public LinkedList<String> getFrame(){
		LinkedList<String> result = new LinkedList<String>();
		for (int  i=0; i<this.frame.size(); i++){
			result.add(this.frame.get(i).getName());
		}
		return result;
	}
	
	public LinkedList<String> getFrameComplement(){
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
	
	public LinkedList<String> getLockFrameComplement(){
		LinkedList<String> vars  = new LinkedList<String>();
		LinkedList<String> frameNames = this.getFrame();
		//vars.addAll(process.getLocalVarsNames());
		vars.addAll(process.getSharedVarsNames());
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<vars.size(); i++){
			String current = vars.get(i);
			if (!frameNames.contains(current)){
				result.add(current);
			}
		}
		return result;
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
