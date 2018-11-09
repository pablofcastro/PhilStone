package Parser;
import java.util.*;

import FormulaSpec.*;
import Spec.*;

public class ActionAux {
	private String name;	// the name of the action
	private ExprAux pre;	// an expression corresponding to the precondition
	private ExprAux post;	// an expression corresponding to the postcondition
	private LinkedList<String> frame;	// the list of the frame
	private String errors;	// a string for signaling the errors
	private ProcessAux myProcess; // the process where the action is defined
	
	/**
	 * A basic constructor
	 * @param name	the name of the action
	 * @param pre	the precondition
	 * @param post	the postcondition
	 */
	public ActionAux(String name, ExprAux pre, ExprAux post) {
		this.name = name;
		this.pre = pre;
		this.post = post;
		this.frame = new LinkedList<String>();
		this.errors = "";
	}
	
	public ActionAux(String name) {
		this.name = name;
		//this.pre = pre;
		//this.post = post;
		this.frame = new LinkedList<String>();
		this.errors = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExprAux getPre() {
		return pre;
	}

	public void setPre(ExprAux pre) {
		this.pre = pre;
	}

	public ExprAux getPost() {
		return post;
	}

	public void setPost(ExprAux post) {
		this.post = post;
	}
	
	public void addError(String error){
		this.errors = this.errors + error + "\n";
	}
	
	public void setMyProcess(ProcessAux myProcess){
		this.myProcess = myProcess; 
	}
	
	public ProcessAux getMyProcess(){
		return this.myProcess;
	}
	
	public String getErrors(){
		return this.errors;
	}
	
	public void addFrame(String name){
		frame.add(name);
	}
	
	public void addAllFrame(LinkedList<String> list){
		frame.addAll(list);
	}
	
	
	public boolean isWellFormed(HashMap<String, Type> table, SpecAux mySpec){
		boolean ok = true;
		Type preType = pre.getType(table, mySpec, myProcess.getName());		
		Type postType = post.getType(table, mySpec, myProcess.getName());
		for (int i=0; i<frame.size(); i++){
			if (!table.containsKey(frame.get(i)) && !this.myProcess.containsPar(frame.get(i))){
				this.addError("Variable "+frame.get(i)+" in frame of process "+name+" no declared.");
				ok = false;
			}	
		}
		
		if ((preType==Type.BOOL) && (postType == Type.BOOL)){
			if (!post.isDNF(table, mySpec, myProcess.getName())){
				this.addError("Postcondition is not in DNF, line: "+Integer.toString(post.getLine()));
				ok = false;
			}
			if (!pre.isPropositional()){
				this.addError("The precondition is not a propositional formula, line: "+Integer.toString(pre.getLine()));
				ok = false;
			}
		}
		else{
			ok = false;
			if (preType == Type.ERROR){
				this.addError(pre.getError());
			}
			if (postType == Type.ERROR){
				this.addError(post.getError());
			}
			if (preType == Type.INT){
				this.addError("Precondition is not a boolean formula, line: " + pre.getLine());
			}
			if (postType == Type.INT){
				this.addError("Postcondition is not a boolean formula, line: " + pre.getLine());
			}
		}		
		return ok;	
	}
	
	public Action getAction(HashMap<String, Type> table, SpecAux mySpec){
		Precondition myPre = new Precondition();
		myPre.addAllFormulas(pre.getClauses(table, mySpec, myProcess.getName()));
		Postcondition myPost = new Postcondition();
		myPost.addAllFormulas(post.getClauses(table, mySpec, myProcess.getName()));
		Action result = new Action(this.name, myPre, myPost, true);
		for (int i=0; i<this.frame.size(); i++){
			if (mySpec.checkVarIsGlobal(this.frame.get(i))){
				if (mySpec.getTypeVar(this.frame.get(i), "global") == Type.BOOL){
					BoolVar var = new BoolVar(this.frame.get(i));
					if (mySpec.isPrimTypeVar(this.frame.get(i)) || myProcess.isPrimPar(this.frame.get(i)))
						var.setIsPrim(true);
					result.addVarToFrame(var);
				}
				if (mySpec.getTypeVar(this.frame.get(i), "global") == Type.INT){
					IntVar var = new IntVar(this.frame.get(i));
					if (mySpec.isPrimTypeVar(this.frame.get(i)) || myProcess.isPrimPar(this.frame.get(i)))
						var.setIsPrim(true);
					result.addVarToFrame(var);
				}
				if (mySpec.getTypeVar(this.frame.get(i), "global") == Type.LOCK){
					Lock l = new Lock(this.frame.get(i), true);
					result.addVarToFrame(l);
				}
				// DO SOMETHING WITH LOCKS!!!!
			}
			else{
				if (myProcess.getLocalVarType(this.frame.get(i)) == Type.BOOL){
					BoolVar var = new BoolVar(this.frame.get(i));
					result.addVarToFrame(var);			
				}
				else{
					IntVar var = new IntVar(this.frame.get(i));
					result.addVarToFrame(var);
				}		
			}		
		}		
		return result;
	}

}