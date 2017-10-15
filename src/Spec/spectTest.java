package Spec;
import FormulaSpec.*;

public class spectTest {

	public static void main(String[] args) {
		ProcessSpec p = new ProcessSpec("Test");
		BoolVar NCS = new BoolVar("NCS");
		Clause c = new Clause();
		c.addPosElem(NCS);
		Precondition pre = new Precondition();
		pre.addFormula(c);
		Action getNCS = new Action(p, "getNCS", pre, null, true);
		
		p.addAction(getNCS);
		p.addLocalVar(NCS);
		p.generateMetamodel(null, "/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/templates");
	}

}
