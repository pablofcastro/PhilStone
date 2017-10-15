package FormulaSpec;

/**
 * A elementary negation is the negation of a elementary formula
 * @author Pablo
 *
 */
public class ElemNegation implements ElemFormula{
	ElemFormula f;
	
	public ElemNegation(ElemFormula f){
		this.f = f;
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		
	
	public String toAlloy(String metaName, String state){
		String result =  "not " + f.toAlloy(metaName,state);
		return result;
	}
	
	public String toString(){
		return "! ("+ f.toString()+")";
	}
	
}
