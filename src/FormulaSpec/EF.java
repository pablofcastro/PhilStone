package FormulaSpec;

public class EF extends TemporalFormula{
	public EF(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		String result = ""; //TBD
		return result;
	}
	
	public String toString(){
		return "EF["+ this.getExpr1().toString() + "]";
	}

}
