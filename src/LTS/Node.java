package LTS;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import Utils.*;
//import org.w3c.dom.Document;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Node;
//import org.w3c.dom.Element;


/**
 * A Simple class for modelling the nodes of an Alloy counterexample
 * @author Pablo
 *
 */
public class Node {
	private String name; // the name of the node
	private LinkedList<Edge> adj; // the adjacents 
	private LinkedList<String> properties;
	private LinkedList<String> globalProperties; // it is used for keeping track of the global properties
	private LTS myLTS; // the LTS where this node resides
	
	/**
	 * A simple constructor
	 * @param name
	 */
	public Node(String name) {
		super();
		this.name = name;
		this.adj = new LinkedList<Edge>();
		this.properties = new LinkedList<String>();
		this.globalProperties = new LinkedList<String>();
	}

	/**
	 * Getter for name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param name	the boolean var's name
	 * @return	true when the var holds in the node false otherwise
	 */
	public boolean getGlobalBooleanVarValue(String name){
		return this.properties.contains(name);
	}
	
	/**
	 * Setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for adj
	 * @return
	 */
	public LinkedList<Edge> getAdj() {
		return adj;
	}

	/**
	 * Setter for adj
	 * @param adj
	 */
	public void setAdj(LinkedList<Edge> adj) {
		this.adj = adj;
	}

	public void setLTS(LTS lts){
		this.myLTS = lts;
	}
	
	/**
	 * Setter for properties
	 * @return
	 */
	public LinkedList<String> getProperties() {
		return properties;
	}

	/**
	 * Setter for properties
	 * @param properties
	 */
	public void setProperties(LinkedList<String> properties) {
		this.properties = properties;
	}
		
	/**
	 * Method for adding an edge
	 * @param e	the edge to be added
	 */
	public void addEdge(Edge e){
		adj.add(e);
	}
	
	/**
	 * Method for add a property to a node
	 * @param name	the name of the property
	 */
	public void addProperty(String name){
		properties.add(name);
		if (name.contains("Av") || myLTS.getProcessSpec().getSharedVarsNames().contains(name.replace("Prop_", ""))) // these keywords are used for global vars
			this.globalProperties.add(name);
	}
	
	/**
	 * Returns all the edges with the given name
	 * @param name
	 * @return
	 */
	public LinkedList<Edge> getEdgesWithName(String name){
		LinkedList<Edge> result = new LinkedList<Edge>();
		for (int i=0; i<adj.size(); i++){
			if (adj.get(i).getName().equals(name))
				result.add(adj.get(i));
		}
		return result;
	}
	
	public LinkedList<Edge> searchEdgesByEqClass(Node target){
		LinkedList<Edge> result = new LinkedList<Edge>();
		for (int i=0;i<this.adj.size();i++){
			if (this.myLTS.getUnionFind().find(this.adj.get(i).getTarget()) == this.myLTS.getUnionFind().find(target))
				result.add(this.adj.get(i));
		}
		return result; 
	}
	
	
	/**
	 * It outputs a description of the node's adjancent in Dot format
	 * @param writer	the place where the dot will be written
	 */
	public void toDot(PrintWriter writer){
		// a simple output, to be improved in the future...
		for (int i=0; i<adj.size(); i++){
			adj.get(i).toDot(writer);
		}
		//if (adj.size()==0)
		//	writer.println(this.name+";");
		writer.print(this.name + " [label = \""+this.name);
		for (int i=0; i<this.properties.size(); i++){
			writer.print("\\n"+properties.get(i));
		}
		writer.println("\"];");
	}
	
	/**
	 * @return	a simple description of the node with its properties, to be used
	 * 			to create the dot
	 */
	public String getDescription(){
		String result = this.name ;
		for (int i=0; i<this.properties.size(); i++){
			result = result+" \n "+properties.get(i);
		}
		return result;
	}
	
	/**
	 * 
	 * @param uf	the union find structures representing the equivalence class of the states
	 * @param var	the variable
	 * @param type	the type for the var
	 * @return		a
	 */
	public String getNuSMVCommandForVar(UnionFind uf, String var, String type){
		String result = "";
		String space = "    ";
		
		for (int i=0; i<this.adj.size();i++){
			if (uf.find(this) != uf.find(this.adj.get(i).getTarget())){
				result += space + space + "state = "+ uf.find(this).getName();
				if (!type.equals("State")){
					result += " & ";
					result += "next(state) = "+uf.find(this.adj.get(i).getTarget()).getName();				
				}
				LinkedList<String> allGlobalProps = this.myLTS.getGlobalProps();
				// for the guards only the global properties are important, the others are ensured by the actual state
				for (int j=0; j<allGlobalProps.size(); j++){
					if (this.globalProperties.contains(allGlobalProps.get(j)))
						result += " & " + allGlobalProps.get(j);
					else
						result += " & !" + allGlobalProps.get(j);
				}
			
				result += " : {";
				Node target = this.adj.get(i).getTarget();
				if (type.equals("State")){
					result += uf.find(target).getName();
				}
				if (type.equals("Bool")){
					result += target.getGlobalBooleanVarValue(var)?"TRUE":"FALSE";
				}
				if (type.equals("Int")){
					// TBD
				}
				result += " };\n";
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return	a guard corresponding to the node
	 */
	public String getGuard(UnionFind uf){
		// LST is null
		String result = "";
		result += "state == "+ uf.find(this).getName();
		LinkedList<String> allGlobalProps = this.myLTS.getGlobalProps();
		
		// for the guards only the global properties are important, the others are ensured by the actual state
		for (int i=0; i<allGlobalProps.size(); i++){
			if (this.globalProperties.contains(allGlobalProps.get(i)))
				result += " && " + allGlobalProps.get(i);
			else
				result += " && !" + allGlobalProps.get(i);
		}
		return result;
	}
	
	/**
	 * 
	 * @return	A command corresponding to the node, properties in the state are set to true, others to false
	 */
	public String getCommand(UnionFind uf){
		//LinkedList<String> locks = new LinkedList<String>();
		LinkedList<String> allProps = this.myLTS.getProps();
		// change this for dealing with integers
		String result = "state="+uf.find(this).getName()+","; // we use the representative of the equiv. class of the current node
		for (int i=0; i<allProps.size(); i++){
			// properties in this node are set to true
			if (this.properties.contains(allProps.get(i))) // if belongs to the current node then it is set to true
				result += (i==0) ? allProps.get(i)+"=true" : ","+ allProps.get(i)+"=true"; 
			else // else it is set to false
				result += (i==0) ? allProps.get(i)+"=false" : ","+ allProps.get(i)+"=false";
		}
		result += ";";
		return result;
		
	}
	
	/**
	 * it inspect the collection of edges, an environmental edge increases the equivalence class of the current node
	 * @param uf	the union-find object
	 */
	public void computeEqClasses(UnionFind uf){
		for (int i=0; i<this.adj.size(); i++){
			if (this.adj.get(i).isEnv())
				uf.union(this, this.adj.get(i).getTarget());
		}
	}
	
	/**
	 * 
	 * @param uf	the equivalence classes of the LTS wrt the environmental transitions
	 * @return		the branches corresponding to this node
	 */
	public String getBranches(UnionFind uf){
		String result = "";
		for (int i=0; i<this.adj.size(); i++){
			Edge currentEdge = this.adj.get(i);
			//if ((currentEdge.getOrigin() == currentEdge.getTarget()) ||  (uf.find(currentEdge.getOrigin()) != uf.find(currentEdge.getTarget()))){ // origin and target must be in different equivalence classes
			if (!currentEdge.isEnv()){
				result += this.adj.get(i).getOrigin().getGuard(uf) + " -> " + this.adj.get(i).getTarget().getCommand(uf) + "\n";				  // or the edge must be a loop
			}
		}
		return result;		
	}
	
	/**
	 * Useful for initial nodes, which equivalence class may contain nodes holding different (global) properties
	 * initially we assume that resources are free
	 * @param node	a given node
	 * @return	All the properties true in the equivalence class of the given node
	 */
	public LinkedList<String> getPropertiesOfEquivClass(String node){
		return null; //TBD
	}
}

