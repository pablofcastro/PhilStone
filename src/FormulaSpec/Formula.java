package FormulaSpec;
import java.util.*;
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
	 
	 /**
	  * @param instances	the set of instances for which we want to remove the vars
	  * @return	A formula where the vars owned by instances in the parameter are changed by constant true, this 
	  * methods is useful for restrict the formula to a set of process instances
	  */
	 public Formula removeVarOwnedBy(LinkedList<String> instances);
}
