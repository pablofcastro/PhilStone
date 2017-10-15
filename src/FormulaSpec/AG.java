package FormulaSpec;

public class AG extends TemporalFormula{
	public AG(Formula e1){
        super(e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
	
	public String toAlloy(String metaName, String state){
		//String result = "all s:"+metaName+".nodes | all s':(*"+metaName+".succs)[s] ";
		String f = this.getExpr1().toAlloy(metaName, "s'");
		String result = "all s':(*("+metaName+".succs)["+state+"]) | " + f;
		return result;
	}
	
	public String getAlloy(){
		String result = "all s:<name>Meta.nodes | all s':(<name>Meta.*succs)[s]";
		return result;
	}
	
	public String toString(){
		return "AG["+ this.getExpr1().toString() + "]";
	}
}
