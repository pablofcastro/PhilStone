package FormulaSpec;
/**
 * A class for representing a temporal or propositional formula
 * @author Pablo
 *
 */
public interface Formula extends Expression {
	 	
	 public void accept(FormulaVisitor visitor);
     //public String toString();
	 /**
	  * Generates Alloy code
	  * @param metaName	the metamodel needed for the code
	  * @param state	a given state needed for the code too
	  * @return
	  */
	 public String toAlloy(String metaName, String state);
	 public boolean usesVar(String var);
}
