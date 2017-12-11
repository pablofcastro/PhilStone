package LTS;
import java.io.*;
import java.util.*;

/**
 * A Simple class to represent a node of a LTS coming form an ALloy counter example
 * @author Pablo
 *
 */
public class Edge {
	private Node origin; // the origin of the edge
	private Node target; // the target of the edegr
	private boolean isEnv; // true when the arc correspond to the environment,
	             		   // otherwise, it corresponds to a local action
	private String name; // the name of the arc
	
	/**
	 * A simple constructor
	 * @param origin
	 * @param target
	 * @param isEnv
	 * @param name
	 */
	public Edge(Node origin, Node target, boolean isEnv, String name) {
		this.origin = origin;
		this.target = target;
		this.isEnv = isEnv;
		this.name = name;
	}

	/**
	 * Getter for Origin
	 * @return
	 */
	public Node getOrigin() {
		return origin;
	}

	/**
	 * Setter for origin
	 * @param origin
	 */
	public void setOrigin(Node origin) {
		this.origin = origin;
	}

	/**
	 * getter for target
	 * @return
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * setter for target
	 * @param target
	 */
	public void setTarget(Node target) {
		this.target = target;
	}

	/**
	 * getter for isEnv
	 * @return
	 */
	public boolean isEnv() {
		return isEnv;
	}

	/**
	 * setter for env
	 * @param isEnv
	 */
	public void setEnv(boolean isEnv) {
		this.isEnv = isEnv;
	}

	/**
	 * get for name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * It writes a Dot representation of the edge
	 * @param writer	the output where the description will be written
	 */
	public void toDot(PrintWriter writer){
		if (!isEnv){
			writer.println(origin.getName() + "->" + target.getName() + ";");
		
		}
		else{ 
			writer.println(origin.getName() + "->" + target.getName() + "[style=dotted];");
	
		}
	}
		
}
