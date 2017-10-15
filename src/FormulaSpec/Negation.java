package FormulaSpec;

public class Negation extends TemporalFormula {
	
	public Negation(Formula e1){
		super(e1, null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		

	public String toAlloy(String metaName, String state){
		String result = "not (" + this.getExpr1().toAlloy(metaName,state) + ")";
		return result;
	}
	
	public String toString(){
		return "!" + "(" + this.getExpr1().toString() + ")";
	}
}
