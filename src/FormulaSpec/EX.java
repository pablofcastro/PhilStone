package FormulaSpec;

public class EX extends TemporalFormula {

	public EX(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		 v.visit(this);			
	}
	
	public String toAlloy(String metaName, String state){
		String result = "EX(" + this.getExpr1().toAlloy(metaName,state) + ")";
		return result;
	}
	
	public String toString(){
		return "EX["+ this.getExpr1().toString() + "]";
	}
}
