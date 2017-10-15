package PS;
import java.util.*;

import FormulaSpec.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import mc.*;

import Parser.Scanner;
import Parser.SpecAux;
import Parser.parser;
import Spec.Spec;
import LTS.*;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.XMLNode;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.A4SolutionReader;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import faulty.Program;
import formula.FormulaElement;
/**
 * This is the main class for the synthesizer
 * @author Pablo
 *
 */
public class PhilStone {

	public static void main(String[] args) {
		if (args.length < 3){ // we check if the inputs has the correct format
			System.out.println("Proper Usage is: java PhilStone -o <OutputPath> filename");
	        System.exit(0);
		}
		if (!args[0].equals("-o")){
			System.out.println("Proper Usage is: java PhilStone -o <OutputPath> filename");
	        System.exit(0);
		}
		
		// If the args are OK we proceed
		String outputPath = args[1];
		SpecAux result = null;
		try{ // we parse the specification
			FileReader specFile = new FileReader(args[2]);
			parser p = new parser(new Scanner(specFile));
			result = (SpecAux) p.parse().value;

		}
		catch(Exception e){
			System.out.println("Parsing error");
            e.printStackTrace(System.out);		
		}
		if (!result.typeCheck()){ // we type check the specification
			System.out.println("The specification has errors");
			System.out.println(result.getErrors());
		}
		else{		
			
			// we write the specification to the corresponding path
			String templateDir = "/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/templates/";	// change for a $ variable	
			System.out.println("The specification is OK");
			Spec mySpec = result.getSpec();
			LinkedList<String> processes = mySpec.getProcessesNames();
			
			// a lexicographic search
			startLexSearch(mySpec, outputPath, templateDir);
			
		}
	}
	
	/**
	 * It uses a lexicographic search for synthesizing a program satisfying the specification
	 * @return	A string with the synthesized program
	 */
	public static String startLexSearch(Spec mySpec, String outputPath, String templateDir){
		System.out.println("Using Exhaustive Search for Synthesis...");
		String result = "";
		String[] candidates = new String[mySpec.getInstanceTypes().keySet().size()]; // an array for keeping the 
																					 // candidates of each instance
		LinkedList<String> processes = mySpec.getProcessesNames();
		HashMap<String, LTS> laxModels = new HashMap<String, LTS>(); // a hashmap to keep track of the laxest LTS for each process
		
		// STEP 1: We generate the coarsest model for each candidate
		for (int i=0; i<processes.size(); i++){
			String currentProcess = processes.get(i);
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			String metamodel = mySpec.metamodelToString(processes.get(i), templateDir);
			try{			
				// we write the specification to a file
			    PrintWriter writer = new PrintWriter(outputPath+currentProcess+"Template.als", "UTF-8");
			    writer.print(metamodel);
			    writer.close();
			} catch (IOException e) {
				System.out.println("Error trying to write the alloy specifications for the processes.");
			}
		
			A4Reporter rep = new A4Reporter();
			Module world = null;
			LTS lts = new LTS();
			try{
				world = CompUtil.parseEverything_fromFile(rep, null, outputPath+currentProcess+"Template.als");
				A4Options opt = new A4Options();
				opt.originalFilename = outputPath+currentProcess+"Template.als"; // the specification metamodel
				opt.solver = A4Options.SatSolver.SAT4J;
				Command cmd = world.getAllCommands().get(0);
				A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
				assert sol.satisfiable();
				
				// we obtain a first candidate for the coarsest model
				sol.writeXML(outputfilename); 
				
				// we read the LTS
				lts.fromAlloyXML(outputfilename);
				//test.toDot("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				
			}catch(Exception e){
				System.out.println("Input-Output Error trying to write Alloy files.");
			}
			//HashMap<String, String> parameters = new HashMap<String, String>();
			
			// we store the laxest model for each process
			laxModels.put(processes.get(i), lts);
			
		}
		
		// STEP 2: We verify if the laxest model is OK
		if (modelCheck(laxModels, mySpec.getInstanceTypes(), mySpec.getGlobalVarsTypes(), mySpec.getGlobalProperty())){
			// if it is OK we are done
			System.out.println("Program Synthesized, saved to output folder.."); 
			return result;
		}
		
		// STEP 3: We start inspecting the submodels of the laxest models for each process
		return result;
	}
	
	/**
	 * It implements a batracking search over the given process
	 * @param map
	 * @param processNumber
	 * @return
	 */
	private boolean lexSearch(HashMap<String, LTS> map, int processNumber){
		if (processNumber == map.size()-1){// base case
			if (modelCheck(laxModels, mySpec.getInstanceTypes(), mySpec.getGlobalVarsTypes(), mySpec.getGlobalProperty()))
					return true;
		}else{	//recursive case
			if (lexSearch(map, processNumber++))
				return true;
		}
		// otherwise we try we other models...
	}
	
	/**
	 * A private method to model check a collection of instances and a set of global vars
	 * @param instances		a hash mapping each instance to its process description
	 * @param globalVars	the global vars of the specification
	 * @return	t
	 */
	private static boolean modelCheck(HashMap<String, LTS> processes, HashMap<String, String> instances, HashMap<String, String> globalVars, Formula prop){
		//boolean result = false;
		String program = "";
		// we construct the program
		
		// first we declare the enum types
		Iterator<String> it1 = processes.keySet().iterator();
		while (it1.hasNext()){
			String currentProcess = it1.next();
			LTS currentLTS = processes.get(currentProcess);
			program += "Enum stateNoName" +" = {";
			LinkedList<String> nodes = currentLTS.getNodeNames();
			for (int i=0; i<nodes.size(); i++){
				program += (i==0)? nodes.get(i) : ","+nodes.get(i);
			}
			program += "};\n";
		}
		
		// now the global vars
		Iterator<String> it2 = globalVars.keySet().iterator();
		program += "Global ";
		while (it2.hasNext()){
			String currentVar = it2.next();
			if (it2.hasNext())
				program += currentVar+" : "+ globalVars.get(currentVar)+",";
			else
				program += currentVar+" : "+ globalVars.get(currentVar)+";";
		}
		program += "\n";
		// the processes are written down
		Iterator<String> it3 = processes.keySet().iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			program += processes.get(it3.next()).toMCProcess(pars); // no parameters by now
		}
		program += "\n";
		// and the main program
		program += "Main(){\n";
		Iterator<String> it4 = instances.keySet().iterator();
		while (it4.hasNext()){
			String currentInstance = it4.next();
			program  += currentInstance +":"+"NoName;\n"; //+instances.get(currentInstance)+";\n";
		}
		Iterator<String> it5 = instances.keySet().iterator();
		while (it5.hasNext()){
			String currentInstance = it5.next();
			program  += "run " + currentInstance +"();\n"; // change this for process with parameters
		}
		program += "}";
		
		// we parse the program with the model checker
		ProgramParser pparser = new ProgramParser();
		Program model = pparser.parseFromString(program);
		//System.out.println(program);
		
		// we get the global property and parse it
		//String formString = prop.toString();
	    String formString = "A(!(p1.Prop_cs & p2.Prop_cs) W false);";
		FormulaParser formulaParser = new FormulaParser(pparser.getSymbolsTable(), model);
	    FormulaElement form = formulaParser.parseFromString(formString);
	    //model.buildModel();
	    // we model check the specification together with the formula
	    return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}
	

}
