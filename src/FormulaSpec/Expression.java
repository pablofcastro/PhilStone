package FormulaSpec;

public interface Expression{
	
	/**
	 * It generates alloy code
	 * @param metaName	the metamodel needed for the code
	 * @param state	the state of the metamodel needed for the code
	 * @return
	 */
	public String toAlloy(String metaName, String state);
	public boolean usesVar(String name);
    //public String toString();
}
