package LTS;
import java.io.*;

/**
 * A simple class to test the classes
 * @author Pablo
 *
 */

public class testLTS {

	public static void main(String[] args) {
		
		LTS test = new LTS();
		/**
		Node node1 = new Node("N1");
		Node node2 = new Node("N2");
		Node node3 = new Node("N3");
		Edge edge1 = new Edge(node1, node2, true, "E1");
		Edge edge2 = new Edge(node1, node3, false, "E2");
		node1.addEdge(edge1);
		node1.addEdge(edge2);
		test.addNode(node1, "N1");
		test.addNode(node2, "N2");
		test.addNode(node3, "N3");
		test.toDot("sample.dot");
		*/
		test.fromAlloyXML("model1.xml");
		try{
			PrintWriter writer = new PrintWriter("Metamodel.als", "UTF-8");
			test.toAlloyMetaModel(writer);
			writer.close();
		}
		catch(IOException e){
			System.out.println("I/O Error");
		}
	}

}
