package LTS;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
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
	private String name; // the name of the model
	private String associatedProcess;
	private UnionFind eqClasses; // used for storing equivalence classes of nodes wrt environmental actions
	private String initialNode;
		
	/**
	 * A simple constructor for the class
	 */
	public LTS() {
		this.nodes = new HashMap<String, Node>(); // the nodes of the model		
		this.props = new LinkedList<String>(); // the proposition in the model
		this.actions = new LinkedList<String>(); // the actions of the model
		this.env = new LinkedList<String>(); // the environmental actions of the model
		this.globalProps = new LinkedList<String>();
		this.name = "NoName";
	}
	
	/**
	 * Another constructor
	 * @param name	the name of the model
	 */
	public LTS(String name) {
		this.nodes = new HashMap<String, Node>(); // the nodes of the model		
		this.props = new LinkedList<String>(); // the proposition in the model
		this.actions = new LinkedList<String>(); // the actions of the model
		this.env = new LinkedList<String>(); // the environmental actions of the model
		this.globalProps = new LinkedList<String>();
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
			if (prop.contains("Av") || prop.contains("global"))
				globalProps.add(prop);
		}
	}
	
	
	public LinkedList<String> getProps(){
		return this.props;
	}
	
	public LinkedList<String> getGlobalProps(){
		return this.globalProps;
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
	 * Creates an alloy metamodel from the LTS
	 * @param output
	 */
	public void toAlloyMetaModel(PrintWriter writer){
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
			}
			
			// writes down the metamodel of the signature
			writer.println("abstract sig Meta_"+ name +"{"  );
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
				writer.print(space + actions.get(i)+" = ");
				for (int j=0; j<edgeList.size();j++){
					if (j==0)
						writer.print(edgeList.get(j).getOrigin().getName()+"->"+edgeList.get(j).getTarget().getName());
					else
						writer.print(" + " + edgeList.get(j).getOrigin().getName()+"->"+edgeList.get(j).getTarget().getName());
				}
				writer.println("");
			}
			writer.println("");
			
			// the propositions
			writer.print(space + "val = ");
			for (int i=0; i<listNodes.size();i++){	
				LinkedList<String> propList = nodes.get(listNodes.get(i)).getProperties();	
				for (int j=0; j<propList.size(); j++){
					if (j==0 && i==0)
						writer.print(listNodes.get(i)+"->"+propList.get(j));
					else
						writer.print(" + "+listNodes.get(i)+"->"+propList.get(j));
				}
			}
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
			writer.print(space+"env =");
			for (int i=0;i<env.size();i++){
				if (i==0)
					writer.print(env.get(i));
				else
					writer.print("+"+env.get(i));
			}
			writer.println("");
			
			// the local actions
			writer.println(space+"local = succs - env");
			writer.println("}");
			
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
			
			this.toDot("sample.dot");
			
			// we obtain the actions
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param parameters	the parameters of the process
	 * @return	A description of the LTS for Model Checking, using the FAULTY language
	 */
	public String toMCProcess(HashMap<String,String> parameters){
		String result = "";
		// by now without parameters
		//result += "Process " + this.name + "(";
		//Set<String> pars = parameters.keySet();
		//Iterator<String> it = pars.iterator();
		//while (it.hasNext()){
		//	String current = it.next();
		//	result += current +":"+parameters.get(current);
		//}
		//result+="){\n";
		result += "Process " + this.name + "{\n";
		for (int i=0; i < this.props.size(); i++){
			if (i==0)
				result += props.get(i);
			else
				result += ", "+props.get(i);
		}
		result += " : BOOL;\n"; // until now we deal only with booleans 
		result += "state : " + "state"+this.name+";\n";
		// we compute the equivalence classes
		this.computeEqClasses();
	
		// the get equivalence class of the initial node
		Node init = this.eqClasses.find(this.nodes.get(initialNode)); 
		
		// we set the initial condition
		result += "Initial : state == "+init.getName();
		LinkedList<String> ps = init.getProperties();
		for (int i=0; i< this.props.size(); i++){
			if (ps.contains(this.props.get(i)))
				result += " && " +this.props.get(i);
			else
				result += " && !" +this.props.get(i);
		}
		result += ";\n";
		
		// the normative contiion is true
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
			    	result = list.item(j).getChildNodes().item(1).getAttributes().item(0).getNodeValue().replace('$', '0');
			
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
				    	Node myNode = new Node(list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue().replace('$', '0'));
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
					result.add(new Edge(nodes.get(origin.replace('$', '0')), nodes.get(target.replace('$', '0')), false, ""));
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
					nodes.get(state.replace('$', '0')).addProperty(prop.replace('$','0').replaceAll("0",""));					
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
						String source = getIthFromTuple(items.item(j), 2).getAttributes().item(0).getNodeValue().replace('$', '0');
						String target = getIthFromTuple(items.item(j), 3).getAttributes().item(0).getNodeValue().replace('$', '0');
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
					String source = getIthFromTuple(items.item(i), 2).getAttributes().item(0).getNodeValue().replace('$', '0');
					String target = getIthFromTuple(items.item(i), 3).getAttributes().item(0).getNodeValue().replace('$', '0');
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
}
