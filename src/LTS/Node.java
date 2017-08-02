package LTS;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
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
	
	
	/**
	 * A simple constructor
	 * @param name
	 */
	public Node(String name) {
		super();
		this.name = name;
		this.adj = new LinkedList<Edge>();
		this.properties = new LinkedList<String>();
	}

	/**
	 * Getter for name
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
	 * @return	a simple descriotion of the node with its properties, to be used
	 * 			to create the dot
	 */
	public String getDescription(){
		String result = this.name ;
		for (int i=0; i<this.properties.size(); i++){
			result = result+" \n "+properties.get(i);
		}
		return result;
	}
	
	
}

