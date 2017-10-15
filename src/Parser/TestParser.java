package Parser;
import Spec.*;

import java.io.FileReader;

public class TestParser {
	private static parser p;

	public static void main(String[] args) {
		SpecAux result = null;
		try{
			FileReader specFile = new FileReader(args[0]);
			p = new parser(new Scanner(specFile));
			result = (SpecAux) p.parse().value;
			//Spec specification = result.getSpec();
			//Spec specification = result.getSpec();
		}
		catch(Exception e){
			System.out.println("Parsing error");
            //e.printStackTrace(System.out);		
		}
		if (!result.typeCheck()){
			System.out.println("The specification has errors");
			System.out.println(result.getErrors());
		}
		else{			
			System.out.println("The specification is OK");
			Spec mySpec = result.getSpec();
			//Process p = mySpec.generateMetamodels
			mySpec.generateMetamodels(null, "/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/templates/");
			//System.out.println(mySpec.toString());
		}
		
	}

}
