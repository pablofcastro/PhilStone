package Utils;

/**
 * This is a class packaging useful methods for reading XML produced by alloy
 * @author Pablo
 *
 */

public class XMLAlloy {
	
	
	
	/**
	 * extract the info of an instance from an XML alloy document
	 * @param alloyDocument
	 * @return
	 */
	public static org.w3c.dom.Node getInstance(org.w3c.dom.Document alloyDocument){
		// this is the root node
		org.w3c.dom.NodeList root = alloyDocument.getChildNodes();
		org.w3c.dom.NodeList items = root.item(0).getChildNodes();
		
		// the first item is the instance
		return items.item(1);
	}
	
	/**
	 * Given a node of a XML document and the label of an attribute it returns the corresponding
	 * node
	 * @param xmlnode
	 * @param attr
	 * @return
	 */
	public static org.w3c.dom.Node getItemFromAttr(org.w3c.dom.Node xmlnode, String attr){
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
	public static org.w3c.dom.Node getIthFromTuple(org.w3c.dom.Node xmlnode, int j){
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
	 * 
	 * @param str	the string to be modifed
	 * @return	the string removing the $ character in a convenient way
	 */
	public static String removeDollarSign(String str){
		if (str.equals("Node$0"))
			return "Node0";
		if (str.endsWith("$0")){
			return str.replaceAll("\\$0", "");
		}
		return str.replace("$", "");
	}
	
	
}
