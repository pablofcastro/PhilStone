package Utils;
/**
 * A simple generic class to model pairs of objects
 * @author Pablo
 *
 */
public class Pair<F, S> {
	private F first; //first member of pair
    private S second; //second member of pair

    /**
     * Basic constructor
     */
    public Pair(){
    	
    }
    
    /**
     * A Simple Constructor
     * @param first	the first argument
     * @param second	the second argument
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Setter for first
     * @param first
     */
    public void setFirst(F first) {
        this.first = first;
    }

    /**
     * Setter for second
     * @param second
     */
    public void setSecond(S second) {
        this.second = second;
    }

    /**
     * Getter for First
     * @return
     */
    public F getFirst() {
        return first;
    }

    /**
     * Getter for second
     * @return
     */
    public S getSecond() {
        return second;
    }
}
