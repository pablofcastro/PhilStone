package FormulaSpec;

import java.util.LinkedList;

public interface Var extends Expression {

	public String getName();
	public String getUnqualifiedName();
	public Type getType();
	public boolean usesVar(String name);
	public String getOwner();
	public boolean isPrimType(); // it says whether the current type is a primitive type (no lock associated) or not
	public void setIsPrim(boolean b);
	//public void addOtherVars(LinkedList<Var> vars); // this method is useful for storing in each var the other vars	
	//public LinkedList<String> getOtherPrimsBooleanNames(); // get the names of the other prims boolean vars

}
