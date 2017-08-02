package LTS;
import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/**
 * A simple class to manipulate and visualize labeled transition systems
 * @author Pablo
 *
 */
public class LTS {
	
	private HashMap<String, Node> nodes; // the nodes of the graph with their edges
	private HashMap<String, Edge> edges; // the edges of the graph

	/**
	 * A simple constructor for the class
	 */
	public LTS() {
		this.nodes = new HashMap<String, Node>();
		this.edges = new HashMap<String, Edge>();
	}
	
	/**
	 * Adds a node to the list
	 * @param n	the node to be added
	 * @param name	the name of the node
	 */
	public void addNode(Node n, String name){
		nodes.put(name,n);
	}
	
	/**
	 * creates a .dot with a graph of the LTS
	 * @param output	the name of the file where the dot will be written
	 */
	public void toDot(String output){
		try{
			PrintWriter writer = new PrintWriter(output, "UTF-8");
			String space = "    ";
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
		   // do something
		}
		
	}
	
	/**
	 * Creates a LTS from an Alloy counterexample in a XML file
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
			
			//org.w3c.dom.NodeList root = doc.getChildNodes();
			//org.w3c.dom.NodeList items = root.item(0).getChildNodes();
			//int i = 1;
			// get the instance form the model
			org.w3c.dom.Node instance = getInstance(doc);
			LinkedList<Node> states = getNodes(instance);
					
			// we add the nodes to the model
			for (int i=0; i< states.size(); i++)
				nodes.put(states.get(i).getName(), states.get(i));
			
			// we add the edges to the model
			LinkedList<Edge> theedges = getEgdes(instance);
			for (int i=0; i< theedges.size(); i++){
				Edge current = theedges.get(i);			
				nodes.get(current.getOrigin().getName()).addEdge(current);
			}
			
			
			// we obtain the properties
			extractProperties(instance);
			this.toDot("sample.dot");
			
			// we obtain the actions
			
		}catch (Exception e) {
			e.printStackTrace();
		}
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
	 * Auxiliar method to get the nodes from a XMLå
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
				    org.w3c.dom.NodeList tuple = list.item(j).getChildNodes();
				    if (list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue().startsWith("Node")){
				    	//System.out.println(list.item(j).getChildNodes().item(3).getAttributes().item(0).getNodeValue());
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
					nodes.get(state.replace('$', '0')).addProperty(prop.replace('$','0').replaceAll("0","").replaceAll("Prop_", ""));					
				}
					
			}
		}
		
	}
	
	/**
	 * Extracts the actions of the instance
	 * @param instance
	 */
	private void extractActions(org.w3c.dom.Node instance){
		
		
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
}
