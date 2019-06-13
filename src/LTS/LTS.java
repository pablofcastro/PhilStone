package LTS;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import Spec.*;

import JFlex.Out;

import javax.xml.parsers.DocumentBuilder;
import Utils.*;

/**
 * A simple class to manipulate and visualize labeled transition systems
 * @author Pablo
 *
 */
public class LTS {
	
	private HashMap<String, Node> nodes; // the nodes of the graph with their edges
	private LinkedList<String> props; // all the propositions in the model	
	private LinkedList<String> globalProps; // only the global propositions
	private LinkedList<String> actions; // the actions of the model
	private LinkedList<String> env; // the environmental actions
	private LinkedList<String> localInvs; // the local properties corresponding to the associated process
	private String name; // the name of the model
	private String associatedProcess;
	private UnionFind eqClasses; // used for storing equivalence classes of nodes wrt environmental actions
	private String initialNode;
	private ProcessSpec processSpec; // the specification corrrsponding to the actual LTS
		
	/**
	 * A simple constructor for the class
	 */
	public LTS() {
		this.nodes = new HashMap<String, Node>(); // the nodes of the model		
		this.props = new LinkedList<String>(); // the proposition in the model
		this.actions = new LinkedList<String>(); // the actions of the model
		this.localInvs = new LinkedList<String>();
		this.env = new LinkedList<String>(); // the environmental actions of the model
		this.globalProps = new LinkedList<String>();
		this.name = "NoName"; // a default name if none is provided
	}
	
	/**
	 * A simple constructor for the class
	 */
	public LTS(ProcessSpec myProcess) {
		this.nodes = new HashMap<String, Node>(); // the nodes of the model		
		this.props = new LinkedList<String>(); // the proposition in the model
		this.actions = new LinkedList<String>(); // the actions of the model
		this.env = new LinkedList<String>(); // the environmental actions of the model
		this.globalProps = new LinkedList<String>();
		this.name = myProcess.getName(); // a default name
		this.processSpec = myProcess;
		this.localInvs = new LinkedList<String>();
		
		for (int i=0; i<myProcess.getInvsAsStrings("Instance"+name).size(); i++)
			this.localInvs.add(myProcess.getInvsAsStrings("Instance"+name).get(i));
		
	}
	
	/**
	 * Another constructor
	 * @param name	the name of the model
	 */
	public LTS(String name) {
		this.nodes = new HashMap<String, Node>(); // the nodes of the model		
		this.props = new LinkedList<String>(); // the proposition in the model
		this.actions = new LinkedList<String>(); // the actions of the model
		this.localInvs = new LinkedList<String>();
		this.env = new LinkedList<String>(); // the environmental actions of the model
		this.globalProps = new LinkedList<String>();
		this.name = name;
	}
	
	/**
	 * 
	 * @param name	the new name of the LTS
	 */
	public void setName(String name){
		this.name = name;
	}
	/**
	 * Adds a node to the list
	 * @param n	the node to be added
	 * @param name	the name of the node
	 */
	public void addNode(Node n, String name){
		nodes.put(name,n);
		n.setLTS(this);
	}
	
	
	/**
	 * Adds a proposition if it is not already in the LTS
	 * @param prop
	 */
	public void addProposition(String prop){
		if (!this.props.contains(prop)){
			props.add(prop);
			if (prop.contains("Av") || this.processSpec.getSharedVarsNames().contains(prop.replace("Prop_", ""))){ //|| prop.contains("Global")) // TO DO: we should differenciate between global and loca properties in the Alloy spec
				globalProps.add(prop);
				//globalProps.add(prop.replace("Av", "Prop")); // we add the corresponding global var
			}
		}
	}
	
	/**
	 * Adds a local property to the collection of properties of the LTS
	 * @param prop	the property to be added
	 */
	public void addLocalProp(String prop){
		localInvs.add(prop);
	}
	
	
	public LinkedList<String> getProps(){
		return this.props;
	}
	
	public LinkedList<String> getGlobalProps(){
		return this.globalProps;
	}
	
	public UnionFind getUnionFind(){
		return this.eqClasses;
	}
	
	public ProcessSpec getProcessSpec(){
		return this.processSpec;
	}
	/**
	 * Adds a proposition if it is not already in the LTS
	 * @param action
	 */
	public void addAction(String action){
		if (!this.actions.contains(action))
			actions.add(action);
	}
	
	/**
	 * Adds an action to the environmental if this is not there
	 * @param action
	 */
	public void addEnv(String action){
		if (!this.env.contains(action))
			env.add(action);
	}
	
	/**
	 * Scans the model and returns a linked list with a list of all the edges with their name 
	 * matching the parameter
	 * @param name
	 * @return
	 */
	public LinkedList<Edge> getEdgesWithName(String name){
		LinkedList<Edge> result = new LinkedList<Edge>();
		LinkedList<String> nodeList = new LinkedList<String>(nodes.keySet());
		for (int i=0; i<nodeList.size(); i++){
			result.addAll(nodes.get(nodeList.get(i)).getEdgesWithName(name));
		}
		return result;
	}
	
	public LinkedList<String> getNodeNames(){
		LinkedList<String> result = new LinkedList<String>();
		Iterator<String> it = this.nodes.keySet().iterator();
		while (it.hasNext()){
			result.add(it.next());
		}
		return result;
	}
	
	/**
	 * 
	 * @return the initial node
	 */
	public String getInitialNode(){
		return this.initialNode;
	}
	public LinkedList<String> getEqClassesNames(){
		LinkedList<String> result = new LinkedList<String>();
		Iterator<String> it = this.nodes.keySet().iterator();
		//if (this.eqClasses == null)
		this.computeEqClasses();
		while (it.hasNext()){
			String current = it.next();
			Node currentNode = this.nodes.get(current);
			if (this.eqClasses.find(currentNode)==currentNode) // if it is a root
				result.add(current);
		}
		return result;
	}
	
	/**
	 * creates a .dot with a graph of the LTS
	 * @param output	the name of the file where the dot will be written
	 */
	public void toDot(String output){
		try{
			PrintWriter writer = new PrintWriter(output, "UTF-8");
			writer.println("digraph G{");			
			Set<String> keys = nodes.keySet();
			Iterator<String> i = keys.iterator();
			while (i.hasNext()){
				String nodeName = i.next();
				nodes.get(nodeName).toDot(writer);
			}
			writer.println("}");
			writer.close();
		}
		catch (IOException e) {
		   System.out.println("Output/Input Error");
		}
		
	}
	
	/**
	 * This method is useful to retrieve the collection of arcs corresponding to an arc between an arc in the equivalence classes
	 * @param origin
	 * @param target
	 * @return	a collection of edge matching the transition between equiv. classes
	 */
	public LinkedList<Edge> getEdges(String origin, String target){
		LinkedList<Edge> result = new LinkedList<Edge>();
		Iterator<String> it = this.nodes.keySet().iterator();
		Node from = nodes.get(origin);
		Node to = nodes.get(target);
		while (it.hasNext()){
			Node current = nodes.get(it.next());
			if (this.eqClasses.find(current) == this.eqClasses.find(from)){
				result.addAll(current.searchEdgesByEqClass(to));
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return the name of the LTS
	 */
	public String getName(){
		return this.name;
	}
	
	
	/**
	 * Creates an alloy specification for  the possible instance from this LTS
	 * Note: the instance only preserves properties of ACTL, this could be extended to all CTL...
	 * @param output	
	 * @param scope		the scope that will passed to Alloy
	 * @param counterexamples a collection of counterexamples that allows us to refine the specification
	 */
	public void getAlloyInstancesSpec(PrintWriter writer, int scope, LinkedList<LinkedList<String>> counterexamples){
			
		if (this.eqClasses == null)
				this.computeEqClasses();
			String space = "    ";
			//PrintWriter writer = new PrintWriter(output, "UTF-8");
			writer.println("abstract sig Node{}");
			
			// get the list of the nodes
			LinkedList<String> listNodes = new LinkedList<String>(nodes.keySet());
			
			for (int i=0; i<listNodes.size(); i++){
				writer.println("one sig "+listNodes.get(i)+ " extends Node{}");
			}
			// write down the propositions
			writer.println("abstract sig Prop{}");		
			for (int i=0; i<props.size(); i++){
				writer.println("one sig "+props.get(i)+ " extends Prop{}");
				writer.println("pred "+props.get(i)+"[m:Instance"+name+",n:Node]{"+props.get(i)+" in m.val[n]}");
			}
			
			LinkedList<String> auxVars = new LinkedList<String>();
			LinkedList<String> auxAxioms = new LinkedList<String>();
			LinkedList<String> auxPreds = new LinkedList<String>();	
			if (this.processSpec != null){
				auxVars = this.processSpec.getAuxVars("Instance"+name);
				auxAxioms = this.processSpec.getAuxAxioms();
				auxPreds = this.processSpec.getAuxPreds("Instance"+name);
				
			}			
			
			for (int i=0; i<auxVars.size(); i++){
				writer.println(auxVars.get(i));
			}
			
			// writes down the metamodel of the signature
			writer.println("abstract sig Instance"+name+"{"  );
			writer.println(space + "nodes : set Node,");
			writer.println(space + "succs : nodes -> nodes,");
			writer.println(space + "val: nodes -> Prop,");
			// print the actions
			for (int i=0; i<actions.size(); i++){
				writer.println(space + actions.get(i)+": nodes -> nodes,");
			}
			writer.println(space + "local: nodes -> nodes,");
			writer.println(space + "env: nodes -> nodes");
			writer.println("}");
			
			// starts the description of the model
			writer.println("{");
			// the nodes
			writer.print(space + "nodes = ");
			for (int i=0; i<listNodes.size();i++){
				if (i == 0)
					writer.print( listNodes.get(i));
				else
					writer.print( "+" + listNodes.get(i));
			}
			writer.println("");
			
			// the actions
			for (int i=0; i<actions.size();i++){
				LinkedList<Edge> edgeList = this.getEdgesWithName(actions.get(i));
				if (edgeList.size() == 0){
					writer.println(space + "no " + actions.get(i));
					continue;
				}
				if (!env.contains(actions.get(i))){
					//if (actions.get(i).equals("ACTgetLeft") || actions.get(i).equals("ACTgetRight"))
						writer.print(space + actions.get(i)+" in "); // NOTE: CHANGE THIS!
					//else
					//	writer.print(space + actions.get(i)+" = ");
				}
				else
					writer.print(space + actions.get(i)+" = ");
				for (int j=0; j<edgeList.size();j++){
					if (j==0)
						writer.print("("+edgeList.get(j).getOrigin().getName()+"->"+edgeList.get(j).getTarget().getName()+")");
					else
						writer.print(" + (" + edgeList.get(j).getOrigin().getName()+"->"+edgeList.get(j).getTarget().getName()+")");
				}
				writer.println("");
			}
			writer.println("");
			
			// the propositions
			writer.print(space + "val = ");
			writer.print(space);
			for (int i=0; i<listNodes.size();i++){	
				LinkedList<String> propList = nodes.get(listNodes.get(i)).getProperties();	
				for (int j=0; j<propList.size(); j++){
					if (j==0 && i==0)
						writer.print(listNodes.get(i)+"->"+propList.get(j));
					else
						writer.print(" + "+listNodes.get(i)+"->"+propList.get(j));
				}
			}
			//writer.print(" in val");
			writer.println("");
			
			// the succs relation
			writer.print(space+"succs = ");
			for (int i=0;i<actions.size();i++){
				if (i==0)
					writer.print( actions.get(i));
				else
					writer.print("+"+actions.get(i));
			}
			writer.println("");
			
			// the env
			if (env.size()>0){
				writer.print(space+"env =");
				for (int i=0;i<env.size();i++){
					if (i==0)
						writer.print(env.get(i));
					else
						writer.print("+"+env.get(i));
				}
				writer.println("");
			}
			else
				writer.println("no env");
			
			// the local actions
			writer.println(space+"local = succs - env");
			
			// we write the additional axioms for fresh variables for CTL formulas
			for (int i=0; i<auxAxioms.size(); i++){
				if (!auxAxioms.get(i).equals(""))
					writer.println(auxAxioms.get(i));
			}
			
			//System.out.println("counter examples:"+counterexamples);
			for (int i=0; i<counterexamples.size();i++){
				LinkedList<String> actualCex = counterexamples.get(i);
				boolean firstTimeOr = true;
				
				for (int j=0; j<actualCex.size()-1;j++){
					if (!actualCex.get(j).equals(actualCex.get(j+1))){	
						LinkedList<Edge> egs = this.getEdges(actualCex.get(j),actualCex.get(j+1));
						//System.out.println(actualCex.get(j));
						//System.out.println(actualCex.get(j+1));
						//System.out.println(this.getEdges(actualCex.get(j),actualCex.get(j+1)));
						if (egs.size() > 0){
							if (firstTimeOr){
								firstTimeOr = false;
							}
							else{
								writer.print(" or ");
							}
							boolean firstTimeAnd = true;
						//LinkedList<Edge> egs = this.getEdges(actualCex.get(j),actualCex.get(j+1));
							for (int h=0; h<egs.size(); h++){
								
								if (firstTimeAnd){
									writer.print("(not ("+egs.get(h).getTarget().getName()+" in succs["+egs.get(h).getOrigin().getName()+"]))");
									firstTimeAnd = false;
								}
								else{
									writer.print("and (not ("+egs.get(h).getTarget().getName()+" in succs["+egs.get(h).getOrigin().getName()+"]))");	
								}
							}
						}
					}
				}
				writer.print("\n");	
			}
		
		
			// we write down the counterexamples
			//for (int i=0; i<counterexamples.size();i++){
			//	LinkedList<String> actualCex = counterexamples.get(i);
			//	boolean firstTime = true;
			//	for (int j=0; j<actualCex.size()-1;j++){
			//		if (!actualCex.get(j).equals(actualCex.get(j+1))){
			//			if (firstTime){
			//				writer.print("(not ("+actualCex.get(j+1)+" in succs["+actualCex.get(j)+"]))");
			//				firstTime = false;
			//			}
			//			else{
			//				writer.print("or (not ("+actualCex.get(j+1)+" in succs["+actualCex.get(j)+"]))");	
			//			}
			//		}
			//	}
			//	writer.print("\n");	
			//}
			writer.println("");
			writer.println("}");
			//writer.println("pred compile[s:Node]{s="+ this.initialNode+"\n all n:Instance"+name+".nodes | some Instance"+name+".local[n] }");
			//writer.println("pred compile[s:Node]{s="+ this.initialNode+"\n 	some n':(*(Instance"+name+".succs))[s] | Prop_eating[InstanceNoName, n'] \n 	all n':(*(Instance"+name+".succs))[s] | some n'':(*(Instance"+name+".succs))[n'] | some InstanceNoName.local[n'']}");
			
			
			// we write the additional predicates
			for (int i=0; i<auxPreds.size(); i++){
				writer.println(auxPreds.get(i));
			}
			
			writer.println("pred compile[s:Node]{s="+ this.initialNode);	
			for (int i=0; i<this.localInvs.size(); i++){
				writer.println(this.localInvs.get(i));
			}
			writer.println("all n':(*(Instance"+name+".succs))[s] | some n'':(*(Instance"+name+".succs))[n'] | some Instance"+name+".local[n'']}");
					
			//		+ "\n all n':(*(Instance"+name+".succs))[s] | some n'':(*(Instance"+name+".succs))[n'] | some InstanceNoName.local[n'']}");
			writer.println("run compile for "+scope + " but 1 Instance"+name);
			writer.close();
	}
	
	/**
	 * This method is useful when performing model checking using Alloy
	 * @param	The name to be given to the signature, passed from the counterexample java
	 * @return ONLY the signature of the LTS, the name of the nodes are renamed to make it possible to conjoin 
	 * 		   different signatures.
	 */
	public String getAlloySign(){
		String result = "";
		if (this.eqClasses == null)
			this.computeEqClasses();
		String space = "    ";
		//PrintWriter writer = new PrintWriter(output, "UTF-8");
		
		// all the names are indexed by the name of the process
		//result = result+"abstract sig "+name+"Node{}"+"\n";
		
		// get the list of the nodes
		LinkedList<String> listNodes = new LinkedList<String>(nodes.keySet());
		
		
		//for (int i=0; i<listNodes.size(); i++){
		//	result = result + "one sig "+name+listNodes.get(i)+" extends Node{}"+"\n";
		//}
		// write down the propositions
		//result = result + "abstract sig"+ name+"Prop{}";		
		//for (int i=0; i<props.size(); i++){
		//	result = result + "one sig "+ name+props.get(i)+" extends"+ name +"Prop{}"+"\n";
		//	result = result + "pred "+ name+props.get(i)+"[m:Instance"+name+",n:"+name+"Node]{"+name+props.get(i)+" in m.val[n]}"+"\n";
		//}
		
		LinkedList<String> auxVars = new LinkedList<String>();
		LinkedList<String> auxAxioms = new LinkedList<String>();
		LinkedList<String> auxPreds = new LinkedList<String>();	
		if (this.processSpec != null){
			auxVars = this.processSpec.getAuxVars("Instance"+name);
			auxAxioms = this.processSpec.getAuxAxioms();
			auxPreds = this.processSpec.getAuxPreds("Instance"+name);
			
		}			
		
		for (int i=0; i<auxVars.size(); i++){
			result = result+name+auxVars.get(i)+"\n";
		}
		
		// writes down the metamodel of the signature
		result = result + "one sig "+name+"Process extends TS{";
		// print the actions
		for (int i=0; i<actions.size(); i++){
			result = result + space + actions.get(i)+": nodes -> nodes,\n";
		}
		//result = result + space + "local: nodes -> nodes,"+"\n";
		//result = result + space + "env: nodes -> nodes"+"\n";
		result = result + "}"+"\n";
		
		// starts the description of the model
		result = result + "{"+"\n";
		// the nodes
		result = result + space + "nodes = ";
		for (int i=0; i<listNodes.size();i++){
			if (i == 0)
				result = result + listNodes.get(i);
			else
				result = result +  "+" + listNodes.get(i);
		}
		result = result + "\n";
		
		// the actions
		for (int i=0; i<actions.size();i++){
			LinkedList<Edge> edgeList = this.getEdgesWithName(actions.get(i));
			if (edgeList.size() == 0){
				result = result + space + "no " + actions.get(i);
				continue;
			}
			if (!env.contains(actions.get(i))){
					 result = result + space + actions.get(i)+" = "; 
			}
			else
				result = result + space + actions.get(i)+" = ";
			for (int j=0; j<edgeList.size();j++){
				if (j==0)
					result = result + "("+edgeList.get(j).getOrigin().getName()+"->"+ edgeList.get(j).getTarget().getName()+")";
				else
					result = result + " + (" +  edgeList.get(j).getOrigin().getName()+"->"+edgeList.get(j).getTarget().getName()+")";
			}
			result = result + "\n";
		}
		result = result + "\n";
		
		// the propositions
		result = result + space + "val = ";
		result = result + space;
		boolean first = true;
		for (int i=0; i<listNodes.size();i++){	
			LinkedList<String> propList = nodes.get(listNodes.get(i)).getProperties();	
			for (int j=0; j<propList.size(); j++){
				if (j==0 && first){
					result = result +  listNodes.get(i)+"->"+propList.get(j);
					first = false;
				}
				else
					result = result + " + "+listNodes.get(i)+"->"+propList.get(j);
			}
		}
		//writer.print(" in val");
		result = result + "\n";
		
		// the succs relation
		result = result + space+"succs = ";
		for (int i=0;i<actions.size();i++){
			if (i==0)
				result = result +  actions.get(i);
			else
				result = result + "+"+actions.get(i);
		}
		result = result + "\n";
		
		// the env
		if (env.size()>0){
			result = result + space+"env =";
			for (int i=0;i<env.size();i++){
				if (i==0)
					result = result + env.get(i);
				else
					result = result + "+"+env.get(i);
			}
			result = result + "\n";
		}
		else
			result = result + "no env" + "\n";
		
		// the local actions
		result = result + space+"local = succs - env" + "\n";
		
		// we write the additional axioms for fresh variables for CTL formulas
		for (int i=0; i<auxAxioms.size(); i++){
			if (!auxAxioms.get(i).equals(""))
				result = result + auxAxioms.get(i) + "n";
		}
		
		result = result + "}" + "\n";
		//writer.println("pred compile[s:Node]{s="+ this.initialNode+"\n all n:Instance"+name+".nodes | some Instance"+name+".local[n] }");
		//writer.println("pred compile[s:Node]{s="+ this.initialNode+"\n 	some n':(*(Instance"+name+".succs))[s] | Prop_eating[InstanceNoName, n'] \n 	all n':(*(Instance"+name+".succs))[s] | some n'':(*(Instance"+name+".succs))[n'] | some InstanceNoName.local[n'']}");
		return result;
	}
	
	/**
	 * 
	 * @return	the initial value in NuSMV notation expressed as a string 
	 */
	public String getNuXMVInitValue(String var){
		this.computeEqClasses();
		if (this.eqClasses.find(this.nodes.get(this.initialNode)).getGlobalBooleanVarValue(var))
			return "TRUE";
		else
			return "FALSE";
	}
	
	
	/**
	 * Creates a LTS from an Alloy instance of the laxest Specification in a XML file
	 * @param file
	 */
	public void fromAlloyXML(String fileName){
		try{
			// vars initialization
			File inputFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			
			// get the instance form the model
			org.w3c.dom.Node instance = getInstance(doc);
			LinkedList<Node> states = getNodes(instance);
					
			// we add the nodes to the model
			for (int i=0; i< states.size(); i++){
				//nodes.put(states.get(i).getName(), states.get(i));
				this.addNode(states.get(i), states.get(i).getName());
			}
			
			// we obtain the properties of the model
			extractProperties(instance);
			
			// we obtain the actions of the model
			extractActions(instance);
			setEnvActions(instance);
			this.initialNode = this.getInitialNode(instance);
			
			//this.toDot("sample.dot");
			
			// we obtain the actions
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param parameters	the parameters of the process
	 * @param processName	the name that the process will have, it has to be passed from the class PhilStone
	 * @param stateProcess	the name of the state space of the process  (an enum type)
	 * @return	A description of the LTS for Model Checking, using the FAULTY language
	 */
	public String toMCProcess(HashMap<String,String> parameters, String processName, String stateProcess){
		String result = "";
		if (!parameters.isEmpty()){
			result += "Process " + processName + "(";
			Set<String> pars = parameters.keySet();
			Iterator<String> it = pars.iterator();
			while (it.hasNext()){
				String current = it.next();
				if (it.hasNext()){
					if (parameters.get(current).equals("BOOL") || parameters.get(current).equals("INT"))
						result += "Prop_"+current +":"+parameters.get(current)+", "+"Av_"+current+":BOOL,"; // ADD THIS WHNE NOT LOCK
					if (parameters.get(current).equals("PRIMBOOL"))
						result += "Prop_"+current +":BOOL";
					if (parameters.get(current).equals("LOCK"))
						result += "Av_"+current+":BOOL,"; // for now we only take into account the Av
				}
				else{ 
					if (parameters.get(current).equals("BOOL") || parameters.get(current).equals("INT"))
						result += "Prop_"+current +":"+parameters.get(current)+", "+"Av_"+current+":BOOL"; // ADD THIS WHNE NOT LOCK
					if (parameters.get(current).equals("PRIMBOOL"))
						result += "Prop_"+current +":BOOL";
					if (parameters.get(current).equals("LOCK"))
						result += "Av_"+current+":BOOL"; // for now we only take into account the Av
					//result += current +":"+parameters.get(current) +", "+"Av_"+current+":BOOL";
					//result += "Av_"+current+":BOOL";
				}
			}
			result+="){\n";
		}
		else{
			result += "Process " + processName + "{\n";
		}
		LinkedList<String> globalVars = new LinkedList<String>();
		for (int i=0;i<this.processSpec.getSharedVarsNames().size();i++)
			globalVars.add("Prop_"+this.processSpec.getSharedVarsNames().get(i)); // we add Prop_ to the shared vars
		boolean ftime = true;
		boolean someVar = false;
		for (int i=0; i < this.props.size(); i++){
			if (!props.get(i).contains("Av") && !globalVars.contains(props.get(i)) && !parameters.containsKey(props.get(i).replace("Prop_", ""))){ // we check that it is not a global var
				if (ftime){
					result += props.get(i);
					ftime = false;
					someVar = true;
				}
				else{
					result += ", "+props.get(i);
					someVar = true;
				}
			}
		}
		if (someVar)
			result += " : BOOL;\n"; // until now we deal only with booleans 
		result += "state : " + "state"+stateProcess+";\n";
		// we compute the equivalence classes
		//if (this.eqClasses == null)
		this.computeEqClasses();
	
		// the get equivalence class of the initial node
		Node init = this.eqClasses.find(this.nodes.get(initialNode)); 
		
		/**
		// we set the initial condition
		result += "Initial : state == "+init.getName();
		//LinkedList<String> ps = init.getProperties(); // test this
		LinkedList<String> ps = this.getPropertiesOfEquivClass(init);
		for (int i=0; i< this.props.size(); i++){
			if (ps.contains(this.props.get(i)))
				result += " && " +this.props.get(i);
			else
				result += " && !" +this.props.get(i);
		}
		result += ";\n";
		*/
		// we set the initial condition
		//result += "Initial : state == " + initialNode;
		result += "Initial : state == " + init.getName();
		//LinkedList<String> ps = init.getProperties(); // test this
		LinkedList<String> ps = this.nodes.get(initialNode).getProperties();
		for (int i=0; i< this.props.size(); i++){
			if (ps.contains(this.props.get(i)))
				result += " && " +this.props.get(i);
			else
				result += " && !" +this.props.get(i);
		}
		result += ";\n";
		
		
		
		// the normative contidion is true
		result += "Normative : true;\n"; // no normative condition
		
		
		
		Iterator<Node> it2 = nodes.values().iterator();
		
		
		
		while (it2.hasNext()){
			Node currentNode = it2.next();
			result += currentNode.getBranches(this.eqClasses); // it returns the branches corresponding to this node
		}
		// we produce the branches of the process
		//for (int i=0; i<nodes.size(); i++){
		//	result += nodes.get(i).getBranches(this.eqClasses); // it returns the branches corresponding to this node
		//}
		result += "}";
		return result;
	}
	
	/**
	 * 
	 * @param parameters	the types for each parameter of this  process
	 * @param parList		a list of the parameters, we need this since the order of the parameters is important
	 * @param processName	the name that the process will have, it has to be passed from the class PhilStone
	 * @param stateProcess	the name of the state space of the process  (an enum type)
	 * @return	A description of the LTS for Model Checking, using the NuSMV language
	 */
	public String toNuSMVProcess(HashMap<String,String> parameters, LinkedList<String> parList, String processName, String stateProcess){
		String result = "";
		String space = "    ";
		result += "MODULE "+processName+"(";
		//Set<String> pars = parameters.keySet();
		//Iterator<String> it = pars.iterator();
		//while (it.hasNext()){
		for (int i=0; i< parList.size(); i++){
			String current = parList.get(i);
			if (i< parList.size()-1){
				if (parameters.get(current).equals("BOOL") || parameters.get(current).equals("INT"))
					result += "Prop_"+current +", "+"Av_"+current+","; // ADD THIS WHNE NOT LOCK
				if (parameters.get(current).equals("PRIMBOOL"))
					result += "Prop_"+current+",";
				if (parameters.get(current).equals("LOCK"))
					result += "Av_"+current+","; // for now we only take into account the Av
			}
			else{ 
				if (parameters.get(current).equals("BOOL") || parameters.get(current).equals("INT"))
					result += "Prop_"+current +", "+"Av_"+current; // ADD THIS WHNE NOT LOCK
				if (parameters.get(current).equals("PRIMBOOL"))
					result += "Prop_"+current;
				if (parameters.get(current).equals("LOCK"))
					result += "Av_"+current; // for now we only take into account the Av
				//result += current +":"+parameters.get(current) +", "+"Av_"+current+":BOOL";
				//result += "Av_"+current+":BOOL";
			}
		}
		result+=")\n";
		
		LinkedList<String> globalVars = new LinkedList<String>();
		for (int i=0;i<this.processSpec.getSharedVarsNames().size();i++)
			globalVars.add("Prop_"+this.processSpec.getSharedVarsNames().get(i)); // we add Prop_ to the shared vars
		boolean ftime = true;
		boolean someVar = false;
		result += "VAR\n";
		for (int i=0; i < this.props.size(); i++){
			if (!props.get(i).contains("Av") && !globalVars.contains(props.get(i)) && !parameters.containsKey(props.get(i).replace("Prop_", ""))){ // we check that it is not a global var
				result += space + props.get(i)+":boolean;\n";
			}
		}
		//if (someVar)
		//	result += " : BOOL;\n"; // until now we deal only with booleans 
		this.computeEqClasses();
		
		result += space + "state : {";
		//program += space + "state"+currentProcess +" : {";
		LinkedList<String> states = this.getEqClassesNames();
		for (int i=0; i<states.size(); i++){
			result += (i==0)? states.get(i) : ","+states.get(i);
		}
		result +="};\n";
		
		// we compute the equivalence classes
		//if (this.eqClasses == null)
		
		
		// we set the initial conditions for the local vars
		//result += "Initial : state == " + initialNode;
		//result += "Initial : state == " + init.getName();
		//LinkedList<String> ps = init.getProperties(); // test this
		result += "ASSIGN\n";
		LinkedList<String> ps = this.nodes.get(initialNode).getProperties();
		for (int i=0; i< this.props.size(); i++){
			if (!this.globalProps.contains(this.props.get(i)) && !parList.contains(this.props.get(i))){
				if (ps.contains(this.props.get(i)))
					result += space + "init("+this.props.get(i)+") := TRUE;\n";
				else
					result += space + "init("+this.props.get(i)+") := FALSE;\n";
			}
		}
		// we set the initial value to pars
		//for (String currentPar:pars){
		//	if (parameters.get(currentPar).equals("BOOL") || parameters.get(currentPar).equals("LOCK"))
		//		result += space + "init(Av_"+currentPar+") := TRUE;\n";
		//	if (parameters.get(currentPar).equals("PRIMBOOL") || parameters.get(currentPar).equals("BOOL")){
		//			if (ps.contains(currentPar))
		//				result += space + "init(Prop_"+currentPar+") := TRUE;\n";
		//			else 
		//				result += space + "init(Prop_"+currentPar+") := FALSE;\n";
		//	}
		//}
		result += space + "init(state) := "+ eqClasses.find(this.nodes.get(initialNode)).getName()+";\n";
		
		// CODE FOR GENERATING ALL THE TRANSITIONS
		for(int j=0;j<props.size();j++){
			result += space + "next("+props.get(j)+"):=case\n";
			for(String nodeName : this.nodes.keySet()){
				result+=nodes.get(nodeName).getNuSMVCommandForVar(this.eqClasses, props.get(j), "Bool");
			}
			result += space + space + "TRUE : {"+props.get(j)+"};\n";
			result += space + "esac;\n";
		}
		result += "next(state):= case\n"; 
		for(String nodeName : this.nodes.keySet()){
			result+=nodes.get(nodeName).getNuSMVCommandForVar(this.eqClasses, "state", "State");
		}
		result += space + space + "TRUE :  { state };\n";
		result += space + "esac;\n";
		return result;
	}
	
	/**
	 * extract the info of an instance from an XML alloy document
	 * @param alloyDocument
	 * @return
	 */
	private org.w3c.dom.Node getInstance(org.w3c.dom.Document alloyDocument){
		
		// this is the root node
		org.w3c.dom.NodeList root = alloyDocument.getChildNodes();
		org.w3c.dom.NodeList items = root.item(0).getChildNodes();
		// the first item is the instance
		return items.item(1);
	}
	
	/**
	 * Auxiliar methods to get the initial node from a XML produced by Alloy
	 * @param instance
	 * @return	the initial node
	 */
	private String getInitialNode(org.w3c.dom.Node instance){
		String result = "";
		org.w3c.dom.NodeList items = instance.getChildNodes();
		int i = 0;
		while(!items.item(i).getNodeName().equals("skolem"))
			i++;
		org.w3c.dom.NodeList list = items.item(i).getChildNodes();
		for (int j=0; j< list.getLength(); j++){
			if (! (list.item(j).getNodeType() == org.w3c.dom.Node.TEXT_NODE)){
			    if (list.item(j).getChildNodes().item(1).getAttributes().item(0).getNodeValue().startsWith("Node")){				    	
			    	//result = list.item(j).getChildNodes().item(1).getAttributes().item(0).getNodeValue().replace('$', '0');
			    	result = removeDollarSign(list.item(j).getChildNodes().item(1).getAttributes().item(0).getNodeValue());
			
			    }
			}
		}		
		return result;
	}
	
	/**
	 * Auxiliar method to get the nodes from a XML representing an Alloy model
	 * @param instance
	 * @return
	 */
	private LinkedList<Node> getNodes(org.w3c.dom.Node instance){
		LinkedList<Node> result = new LinkedList<Node>();
		org.w3c.dom.NodeList items = instance.getChildNodes();
		int i = 0;
		while(!items.item(i).getNodeName().equals("field"))
			i++;
		if(items.item(i).getAttributes().item(1).getNodeValue().equals("nodes")){
			org.w3c.dom.NodeList list = items.item(i).getChildNodes();
			for (int j=0; j< list.getLength(); j++){
				if (! (list.item(j).getNodeType() == org.w3c.dom.Node.TEXT_NODE)){
				    if (list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue().startsWith("Node")){
				    	//Node myNode = new Node(list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue().replace('$', '0'));
				    	//Node myNode = new Node(list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue().replace("$", ""));
				    	Node myNode = new Node(removeDollarSign(list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue()));
				    	result.add(myNode);
				    }
				}
			}
		}
		return result;
	}
	
	/**
	 * Auxiliar method to obtain the edges from a XML
	 * @param instance
	 * @return
	 */
	private LinkedList<Edge> getEgdes(org.w3c.dom.Node instance){
		LinkedList<Edge> result = new LinkedList<Edge>();
		
		org.w3c.dom.Node xmlsuccs = getItemFromAttr(instance, "succs");
		org.w3c.dom.NodeList items = xmlsuccs.getChildNodes();
		for (int i=0; i<items.getLength(); i++){
			if (items.item(i).getNodeType()!= org.w3c.dom.Node.TEXT_NODE){
				if (items.item(i).getNodeName().equals("tuple")){
					String origin = getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue();
					String target = getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue();
					//result.add(new Edge(nodes.get(origin.replace('$', '0')), nodes.get(target.replace('$', '0')), false, ""));
					result.add(new Edge(nodes.get(removeDollarSign(origin)), nodes.get(removeDollarSign(target)), false, ""));
				}
					
			}
		}
		return result;
		
	}

	/**
	 * It extracts the properties for the nodes from a given instance
	 * @param instance
	 */
	private void extractProperties(org.w3c.dom.Node instance){
		org.w3c.dom.Node xmlprops = getItemFromAttr(instance, "val");
		org.w3c.dom.NodeList items = xmlprops.getChildNodes();
		for (int i=0; i<items.getLength(); i++){
			if (items.item(i).getNodeType()!= org.w3c.dom.Node.TEXT_NODE){
				if (items.item(i).getNodeName().equals("tuple")){
					String state = getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue();
					String prop = getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue();
					//nodes.get(state.replace('$', '0')).addProperty(prop.replace('$','0').replaceAll("0",""));		
					nodes.get(removeDollarSign(state)).addProperty(prop.replace('$','0').replaceAll("0",""));	
					this.addProposition(prop.replace('$','0').replaceAll("0",""));
				}
					
			}
		}
		
	}
	
	/**
	 * It extracts the actions from a given instance in a xml node
	 * @param instance
	 */
	private void extractActions(org.w3c.dom.Node instance){
		LinkedList<org.w3c.dom.Node> actions = getListOfActions(instance);
		for (int i=0; i < actions.size(); i++){
			org.w3c.dom.Node action = actions.get(i);
			String name =  action.getAttributes().item(1).getNodeValue();
			this.addAction(name);
			org.w3c.dom.NodeList items = action.getChildNodes();
			for (int j=0; j<items.getLength(); j++){
				if ( items.item(j).getNodeType() != org.w3c.dom.Node.TEXT_NODE){
					if (items.item(j).getNodeName().equals("tuple")){
						//String source = getIthFromTuple(items.item(j), 2).getAttributes().item(0).getNodeValue().replace('$', '0');
						//String target = getIthFromTuple(items.item(j), 3).getAttributes().item(0).getNodeValue().replace('$', '0');
						String source = removeDollarSign(getIthFromTuple(items.item(j), 2).getAttributes().item(0).getNodeValue());
						String target = removeDollarSign(getIthFromTuple(items.item(j), 3).getAttributes().item(0).getNodeValue());
						//this.actions.put(name, new Pair<String, String>(source, target));	
						this.nodes.get(source).addEdge(new Edge(nodes.get(source), nodes.get(target), false, name));
					}
						
				}
			}
			
		}
	}
	
	/**
	 * Locate and set those action that are from de environment in the alloy model
	 * @param instance
	 */
	private void setEnvActions(org.w3c.dom.Node instance){
		org.w3c.dom.Node xmlprops = getItemFromAttr(instance, "env");
		org.w3c.dom.NodeList items = xmlprops.getChildNodes();
		for (int i=0; i<items.getLength(); i++){
			// we check if the items contains information
			if (items.item(i).getNodeType()!= org.w3c.dom.Node.TEXT_NODE){
				// if they are tuples then we get the contain
				if (items.item(i).getNodeName().equals("tuple")){
					//String source = getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue().replace('$', '0');
					//String target = getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue().replace('$', '0');
					//String source = getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue().replace("$", "");
					//String target = getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue().replace("$", "");
					String source = removeDollarSign(getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue());
					String target = removeDollarSign(getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue());
					LinkedList<Edge> edges = nodes.get(source).getAdj();
					// we set the corresponding arcs true
					for (int j=0; j<edges.size();j++){
						if (edges.get(j).getOrigin().getName().equals(source) && edges.get(j).getTarget().getName().equals(target)){
							edges.get(j).setEnv(true);						
							addEnv(edges.get(j).getName());
						}
					}
				}
					
			}
		}
	}
	
	/**
	 * 
	 * @param instance
	 * @return the list of xmlnodes representing actions
	 */
	private LinkedList<org.w3c.dom.Node> getListOfActions(org.w3c.dom.Node instance){
		org.w3c.dom.NodeList items = instance.getChildNodes();
		LinkedList<org.w3c.dom.Node> result = new LinkedList<org.w3c.dom.Node>();		
		for (int i=0; i<items.getLength(); i++){
			if (items.item(i).getNodeType() != org.w3c.dom.Node.TEXT_NODE){
				if(items.item(i).getAttributes().item(1).getNodeValue().contains("ACT")){
					result.add(items.item(i));		
				}
			}
		}
		return result;		
	}
		
	/**
	 * Given a node of a XML document and the label of an attribute it returns the corresponding
	 * node
	 * @param xmlnode
	 * @param attr
	 * @return
	 */
	private org.w3c.dom.Node getItemFromAttr(org.w3c.dom.Node xmlnode, String attr){
		org.w3c.dom.NodeList items = xmlnode.getChildNodes();
		org.w3c.dom.Node result = null;
		for (int i=0; i<items.getLength(); i++){
			if (items.item(i).getNodeType() != org.w3c.dom.Node.TEXT_NODE){
				if(items.item(i).getAttributes().item(1).getNodeValue().equals(attr)){
					result = items.item(i);				
					break;
				}
			}
		}
		return result;		
	}
	
	/**
	 * Given a xmlnode  representing a tuple it returns the ith element
	 * @param xmlnode
	 * @param i
	 * @return it returns the node (may be null)
	 */
	private org.w3c.dom.Node getIthFromTuple(org.w3c.dom.Node xmlnode, int j){
		org.w3c.dom.Node result = null;
		org.w3c.dom.NodeList items = xmlnode.getChildNodes();
		for (int i=0; i<items.getLength(); i++){
			if (j==1 && items.item(i).getNodeType() != org.w3c.dom.Node.TEXT_NODE){
				result = items.item(i);
				break;
			}
			else{
				if (items.item(i).getNodeType() != org.w3c.dom.Node.TEXT_NODE)
					j--;
			}		
		}
		return result;
	}
	
	/**
	 * Computes the equivalence classes of this LTS wrt environmental actions
	 */
	public void computeEqClasses(){
		Set<String> names = this.nodes.keySet();
		this.eqClasses = new UnionFind(this.nodes.values());
		Iterator<String> it = names.iterator();
		while (it.hasNext()){
			Node currentNode = this.nodes.get(it.next());
			currentNode.computeEqClasses(this.eqClasses);
		}
	}
	
	/**
	 * Useful for initial nodes, which equivalence class may contain nodes holding different (global) properties
	 * initially we assume that resources are free
	 * @param nodeName	a given node
	 * @return	All the properties true in the equivalence class of the given node
	 */
	public LinkedList<String> getPropertiesOfEquivClass(Node par){
		LinkedList<String> result = new LinkedList<String>();
		Iterator<Node> it = this.nodes.values().iterator();
		//Node par = this.nodes.get(nodeName);
		result.addAll(par.getProperties());
		while (it.hasNext()){
			Node current = it.next();
			if (this.eqClasses.find(current) == this.eqClasses.find(par)){
				for (int i=0; i<current.getProperties().size();i++){
					if (!result.contains(current.getProperties().get(i)))
						result.add(current.getProperties().get(i));
				}
			}
		}
		return result; 
	}
	
	/**
	 * 
	 * @param str	the string to be modifed
	 * @return	the string removing the $ character in a convenient way
	 */
	private String removeDollarSign(String str){
		if (str.equals("Node$0"))
			return "Node0";
		if (str.endsWith("$0")){
			return str.replaceAll("\\$0", "");
		}
		return str.replace("$", "");
	}
	
}
