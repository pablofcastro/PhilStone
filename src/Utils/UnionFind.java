package Utils;
import LTS.*;
import java.util.*;

/** 
 * A simple implementation of Union-Find structure, used for constructing equivalence classes of nodes
 * @author Pablo
 *
 */
public class UnionFind{
	private HashMap<Node,Node> parent; // an array for 
	private HashMap<Node, Integer> rank;

	public UnionFind(LinkedList<Node> nodes) {
		parent = new HashMap<Node, Node>();
	    rank = new HashMap<Node, Integer>();
	    for (int i = 0; i < nodes.size(); i++){
	    	parent.put(nodes.get(i), nodes.get(i)); // at the beginning each node is its own parent
	    	rank.put(nodes.get(i), new Integer(0));
	    }
	}
	
	public UnionFind(Collection<Node> nodes) {
		parent = new HashMap<Node, Node>();
	    rank = new HashMap<Node, Integer>();
	    Iterator<Node> it = nodes.iterator();
	    while (it.hasNext()){
	    	Node currentNode = it.next();
	    	parent.put(currentNode, currentNode);	// at the beginning every node is its own parent
	    	rank.put(currentNode, new Integer(0));	// and the rank is 0
	    }
	}

	  public Node find(Node n) {
		  Node p = parent.get(n);
		  if (n == p) {
			  return n;
		  }
		  Node aux = find(p);
		  parent.put(n, aux);
		  return aux;
	  }

	  public void union(Node n, Node m) {

	    Node root1 = find(n);
	    Node root2 = find(m);

	    if (root2 == root1) return;

	    if (rank.get(root1).intValue() > rank.get(root2).intValue()) {
	    	parent.put(root2, root1);
	    	//parent[root2] = root1;
	    //} else if (_rank[root2] > _rank[root1]) {
	    }else if (rank.get(root2).intValue() > rank.get(root1).intValue()){
	    	parent.put(root1, root2);
	    } else {
	    	parent.put(root2, root1);
	    	rank.put(root1, new Integer(rank.get(root1).intValue()+1));
	    }
	  }	  
}
