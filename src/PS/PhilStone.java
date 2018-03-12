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
	private static String syntProgram = ""; // the final program will be saved to this attribute
	private static boolean showInfo = false; // true when we want to show info useful for debugging
	private static boolean writePdf = false; // if true a pdf showing each process will be generated
	private static int i=0;
	private static LinkedList<String> instancesList; // the list of instances 
	private static String path = "";
	private static String specName = "";
	private static String smallSpec = ""; // this is used for genSearching, it saves the path of the spec with small processes
	private static boolean lexSearch = false;
	private static boolean cexSearch = true; // by default we use cexSearch
	private static boolean genSearch = false;
	private static boolean herSearch = false;
	private static int scope = 0;
	
	public static void main(String[] args) {
		
		/*if (args.length < 3){ // we check if the inputs has the correct format
			System.out.println("Proper Usage is: java PhilStone -o <OutputPath> filename");
	        System.exit(0);
		}
		if (!args[0].equals("-o")){
			System.out.println("Proper Usage is: java PhilStone -o <OutputPath> filename");
	        System.exit(0);
		}*/
		if (args.length == 0){
			System.out.println("Proper Usage is: java PhilStone [options] filename");
	        System.exit(0);
		}
		if (args.length > 0){
			specName = args[args.length-1];
			for (int i=0; i<args.length-1;i++){
				if (args[i].equals("-pdf")){
					writePdf=true;
					continue;
				}
				if (args[i].equals("-info")){
					showInfo=true;
					continue;
				}
				if (args[i].equals("-lexSearch")){
					lexSearch = true;
					cexSearch = false;	
					herSearch = false;
					genSearch= false;
					continue;
				}
				if (args[i].startsWith("-genSearch=")){
					lexSearch = false;
					cexSearch = false;	
					herSearch = false;
					genSearch= true;
					smallSpec = args[i].replace("-genSearch=","");
					continue;
				}
				if (args[i].equals("-herSearch")){
					lexSearch = false;
					cexSearch = false;	
					genSearch = false;
					herSearch = true;
					continue;
				}
				if (args[i].startsWith("-scope=")){					
					try{
						scope = Integer.parseInt(args[i].replace("-scope=",""));
						continue;
					}
					catch (NumberFormatException e){
						System.out.println("Wrong int parmater after option -scope");
						System.exit(0);
					}
				}
				else{
					System.out.println("Proper Usage is: java PhilStone [options] filename");
					System.out.println("Option: -info (shows debugging info)");
					System.out.println("Option: -pdf (saves pdf)");
			        System.exit(0);
				}
				
			}
		}
		
		if (System.getenv("PhilStone")==null){
			System.out.println("The Environment Variable PhilStone is not set. It must be set to the current root path of the tool.");
	        System.exit(0);
		}
		
		
		// If the args are OK we proceed
		String outputPath = System.getenv("PhilStone")+"output/";
		String templateDir =  System.getenv("PhilStone")+"templates/";
		String pdfDir = System.getenv("PhilStone")+"pdf/";
		SpecAux result = null;
		try{ // we parse the specification
			FileReader specFile = new FileReader(specName);
			parser p = new parser(new Scanner(specFile));
			result = (SpecAux) p.parse().value;

		}
		catch(Exception e){
			System.out.println("Error while parsing the Spec File");
            e.printStackTrace(System.out);		
		}
		SpecAux smalls = null;
		if (genSearch){
			try{ // we parse the specification
				FileReader specFile = new FileReader(smallSpec);
				parser p = new parser(new Scanner(specFile));
				smalls = (SpecAux) p.parse().value;
			}
			catch(Exception e){
				System.out.println("Error while parsing the Small Specification");
	            e.printStackTrace(System.out);		
			}
		}
		
		if (!result.typeCheck()){ // we type check the specification
			System.out.println("The specification has errors");
			System.out.println(result.getErrors());
		}
		else{		
			
			System.out.println("The specification is OK");
			Spec mySpec = result.getSpec();
			
			if (cexSearch){
				CounterExampleSearch cs = new CounterExampleSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope);
				cs.startSearch();
			}
			if (lexSearch){
				LexSearch ls = new LexSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope);
				ls.startLexSearch();
				if (ls.getSyntProgram()!="")
					System.out.println(ls.getSyntProgram());
				else
					System.out.println("program not found.");
			}
			if (genSearch){
				if (!smalls.typeCheck()){
					System.out.println("The specification with less instances contains type errors");
					System.out.println(smalls.getErrors());
				}
				else{
					Spec mySmallSpec = smalls.getSpec();
					GenSearch gs = new GenSearch(mySpec, mySmallSpec, outputPath, templateDir, showInfo, writePdf, scope);
					gs.startGenSearch();
					if (gs.getSyntProgram()!="")
						System.out.println(gs.getSyntProgram());
					else
						System.out.println("program not found.");
				}
			}
			if (herSearch){
				HerSearch gs = new HerSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope);
				gs.startSearch();
				if (gs.getSyntProgram()!="")
					System.out.println(gs.getSyntProgram());
				else
					System.out.println("program not found.");
			}
			
		}
	}
	
	
	/**
	 * It uses a lexicographic search for synthesizing a program satisfying the specification
	 * @return	A string with the synthesized program
	 *//*
	public static String startLexSearch(Spec mySpec, String outputPath, String templateDir){
		System.out.println("Using Exhaustive Search for Synthesis...");
		String result = "";
		
		// we get the instances with their respective types
		HashMap<String,String> instances = mySpec.getInstanceTypes(); 
		instancesList = new LinkedList<String>(instances.keySet());
		
		// a hashmap to keep track of the laxest LTS for each process
		LinkedList<String> processes = mySpec.getProcessesNames();
		HashMap<String, LTS> laxModels = new HashMap<String, LTS>(); 
		// a hashmap for the laxest models for each instance
		HashMap<String, LTS>  insLaxModels = new HashMap<String, LTS>();
		
		
		// STEP 1: We generate the laxest model for each instance
		for (int i=0; i<processes.size(); i++){
			String currentProcess = processes.get(i);
			//String currentInstance = 
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			//String myProcess = instances.get(currentProcess);
			String metamodel = mySpec.metamodelToString(currentProcess, templateDir);
			try{			
				// we write the specification to a file
			    PrintWriter writer = new PrintWriter(outputPath+currentProcess+"Template.als", "UTF-8");
			    writer.print(metamodel);
			    writer.close();
			} catch (IOException e) {
				System.out.println("Error trying to write the alloy specifications for the processes.");
				System.out.println(e.getStackTrace());
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
				//System.out.println("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				//lts.toDot("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				
			}catch(Exception e){
				System.out.println("Input-Output Error trying to write Alloy files.");
				System.out.println(e);
			}	
			// we store the laxest model for each process
			laxModels.put(currentProcess, lts);
			// we store the laxest model for each instance, at the beginning they coincide with those of the processes
			for (int j=0; j<instancesList.size();j++){
				if (instances.get(instancesList.get(j)).equals(currentProcess))
					insLaxModels.put(instancesList.get(j), lts); // if the instance has as type the current process then the lts is set
			}
			
		}
		
		// a hashmap to keep track of which candidates have been changed
		HashMap<String, Boolean> changed = new HashMap<String,Boolean>();
		for (int i=0; i<instancesList.size();i++){
			changed.put(instancesList.get(i), new Boolean(false));
		}
		// an iterator for the laxest models
		//Iterator<String> itModels = insLaxModels.keySet().iterator();
		boolean found = lexSearch(insLaxModels, laxModels, changed, 0, mySpec, 14, outputPath);
		if (found){
			System.out.println("Program Synthesized, saved to output folder.."); 
			for (int k=0; k<instancesList.size();k++){
				insLaxModels.get(instancesList.get(k)).toDot(outputPath+instancesList.get(k)+".dot");
			}
			
			// the program is written to the output folder
			try{
				PrintWriter writer = new PrintWriter(outputPath+mySpec.getName()+".imp", "UTF-8");
				writer.print(syntProgram);
				writer.close();
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		else
			System.out.println("Program not found.."); 
		// STEP 3: We start inspecting the submodels of the laxest models for each process
		//searchSubmodel(laxModels, laxModels.keySet().iterator(), outputPath);
		return result;
	}*/
	
	/**
	 * It implements a backtracking search over the different instances
	 * @param map 			it maps each instance to its candidate LTS
	 * @param processMap	it maps each PROCESS to its candidate
	 * @param changed		a mapping to indicate which LTS was modified
	 * @return	true if a method can be synthesized
	 *//*
	private static boolean lexSearch(HashMap<String, LTS> map, HashMap<String, LTS> processMap, HashMap<String, Boolean> changed, int it, Spec mySpec, int scope, String outputPath){
		//String currentInstance = it.next(); // we assume the iterator is not empty!
		String currentInstance = instancesList.get(it);
		LTS originalModel = map.get(currentInstance);
		//if (!it.hasNext()){// base case
		if (it == instancesList.size()-1){
			if (modelCheck(map, processMap, changed, mySpec)){
					return true;
			}else{ // we search for another instance
				A4Reporter rep = new A4Reporter();
				Module world = null;
				LTS lts = new LTS();
				try{
					//LTS actualModel = map.get(currentProcess);
					PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
					map.get(currentInstance).getAlloyInstancesSpec(writer,scope, new LinkedList<LinkedList<String>>());				
					world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
					A4Options opt = new A4Options();
					opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
					opt.solver = A4Options.SatSolver.SAT4J;
					Command cmd = world.getAllCommands().get(0);
					A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
					//assert sol.satisfiable();
					boolean found = false;
					while (sol.satisfiable() && !found){ // while not found try all the instances
									
						// we obtain a first candidate for the coarsest model
						sol.writeXML(outputPath+"temp.xml"); 				
						// we read the LTS
						
						lts.fromAlloyXML(outputPath+"temp.xml");
						lts.toDot(outputPath+"instance"+i+".dot");
						System.out.println("Instance Number:"+i);
						i++;
						map.put(currentInstance, lts);
						changed.put(currentInstance, new Boolean(true));
						found = modelCheck(map, processMap, changed, mySpec);
						changed.put(currentInstance, new Boolean(false));
						if (found){
							// we have found an instance and indicate the change in changed map
							changed.put(currentInstance, new Boolean(true));
							return true;
						}
						sol = sol.next();// dont know about this line
					}
					// if not program found we reestablished the original model to continue with the model checking
					map.put(currentInstance, originalModel);// the original model is reestablished
					return found;
			
				}catch(Exception e){
					System.out.println("Input-Output Error trying to write Alloy files.");
					e.printStackTrace();//System.out.println(e);
				}	
			}
		}else{	//recursive case
			if (lexSearch(map, processMap, changed, it+1, mySpec, scope, outputPath)){
				return true; // model found
			}
			else{ // otherwise we do a backtracking
				A4Reporter rep = new A4Reporter();
				Module world = null;
				LTS lts = new LTS();
				try{
					//LTS actualModel = map.get(currentProcess);
					PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
					map.get(currentInstance).getAlloyInstancesSpec(writer,scope,new LinkedList<LinkedList<String>>());
					world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
					A4Options opt = new A4Options();
					opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
					opt.solver = A4Options.SatSolver.SAT4J;
					Command cmd = world.getAllCommands().get(0);
					A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
					//assert sol.satisfiable();
					boolean found = false;
					while (sol.satisfiable() && !found){ // while not found try all the instances
						// we obtain a first candidate for the coarsest model
						sol.writeXML(outputPath+"temp.xml"); 
						
						// we read the LTS
						lts.fromAlloyXML(outputPath+"temp.xml");
						lts.toDot(outputPath+"second.dot");
						map.put(currentInstance, lts);
						// we refresh changed
						changed.put(currentInstance, new Boolean(true));
						//found = modelCheck(map, processMap, changed, mySpec);
						found = lexSearch(map, processMap, changed, it+1, mySpec, scope, outputPath);
						sol = sol.next();
					}
					map.put(currentInstance, originalModel);// the original model is reestablished
					changed.put(currentInstance, new Boolean(false)); // we undo changed
					return found;
				}catch(Exception e){
					System.out.println("Input-Output Error trying to write Alloy files.");
					//System.out.println(e);
					e.printStackTrace();
				}	
				
			}
			
		}
		return false; // if we reach here we return false
	}
	
	*//**
	 * 
	 * @return
	 *//*
	public static boolean startSearchByLevels(Spec mySpec, String outputPath, String templateDir, int scope){
		System.out.println("Using Search By Levels for Synthesis...");
				
		// we get the instances with their respective types
		HashMap<String,String> instances = mySpec.getInstanceTypes(); 
		LinkedList<String> instancesList = new LinkedList<String>(instances.keySet());
		
		// a hashmap to keep track of the laxest LTS for each process
		LinkedList<String> processes = mySpec.getProcessesNames();
		HashMap<String, LTS> laxModels = new HashMap<String, LTS>(); 
		
		// a hashmap for the laxest models for each instance
		HashMap<String, LTS>  insLaxModels = new HashMap<String, LTS>();
		
		
		// STEP 1: We generate the laxest model for each instance
		for (int i=0; i<processes.size(); i++){
			String currentProcess = processes.get(i);
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			String metamodel = mySpec.metamodelToString(currentProcess, templateDir);
			try{			
				// we write the specification to a file
			    PrintWriter writer = new PrintWriter(outputPath+currentProcess+"Template.als", "UTF-8");
			    writer.print(metamodel);
			    writer.close();
			} catch (IOException e) {
				System.out.println("Error trying to write the alloy specifications for the processes.");
				System.out.println(e.getStackTrace());
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
				//System.out.println("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				//lts.toDot("/Users/Pablo/University/my-papers/drafts/Alloy.Synt/Tool/local/output/"+currentProcess+"Template.dot");
				
			}catch(Exception e){
				System.out.println("Input-Output Error trying to write Alloy files.");
				System.out.println(e);
			}	
			// we store the laxest model for each process
			laxModels.put(currentProcess, lts);
			// we store the laxest model for each instance, at the beginning they coincide with those of the processes
			for (int j=0; j<instancesList.size();j++){
				if (instances.get(instancesList.get(j)).equals(currentProcess))
					insLaxModels.put(instancesList.get(j), lts); // if the instance has as type the current process then the lts is set
			}
			
		}
		// a hashmap to keep track of which candidates have been changed
		HashMap<String, Boolean> changed = new HashMap<String,Boolean>();
		for (int i=0; i<instancesList.size();i++){
				changed.put(instancesList.get(i), new Boolean(false));
		}
			
		// STEP 2: We model check the laxest models 
		if (modelCheck(insLaxModels, laxModels, changed, mySpec))
			return true;
		else{ // otherwise we search for other instances
			
			// we create an order to be able of cycling throughout the instances
			HashMap<Integer, String> order = new HashMap<Integer, String>(); 
			int max = 0;
			Iterator<String> it = insLaxModels.keySet().iterator();
			while (it.hasNext()){
				order.put(new Integer(max), it.next());
				max++; // it has one more that the number of instances
			}
			HashMap<String, A4Solution> solvers = new HashMap<String, A4Solution>();
			boolean found = false;
			boolean hasNext = true;
			Integer next = new Integer(0);
			int noSat = 0; // number of instances with no more alloy instances
			// while program not found and we have next, we search in a circular way
			while (!found && hasNext){
				String currentIns = order.get(next);
				if (!changed.get(next)){ //if the instance still has a laxest model we changed it
					A4Reporter rep = new A4Reporter();
					Module world = null;
					LTS lts = new LTS();		
					try{
						PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");	// a file for saving the alloy description of the instance
						insLaxModels.get(currentIns).getAlloyInstancesSpec(writer, scope, new LinkedList<LinkedList<String>>());
						world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
						A4Options opt = new A4Options();
						opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
						opt.solver = A4Options.SatSolver.SAT4J;
						Command cmd = world.getAllCommands().get(0);
						A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
						
						// we put the solver into the hashmap
						solvers.put(currentIns, sol);
						
						// we save the instance to a xml
						sol.writeXML(outputPath+"temp.xml"); 
						
						// we read the LTS
						lts.fromAlloyXML(outputPath+"temp.xml");
						insLaxModels.put(currentIns, lts);
						
						// we refresh changed
						changed.put(currentIns, new Boolean(true));
						found = modelCheck(insLaxModels, laxModels, changed, mySpec);
					}
					catch(Exception e){
						System.out.println("Input-Output Error while generating instances");
					}
				}
				else{ // otherwise we already have a solver for each instance
					if (!solvers.get(currentIns).satisfiable()){
						noSat++;
						next = (next + 1) % max; // we update next in a circular way
						hasNext = (noSat == max - 1);
					}
					else{
						try{
							// we model check the actual instances and, if no successful, we update the solver
							A4Solution currentSolver = solvers.get(currentIns);
							// we save the instance to a xml
							currentSolver.writeXML(outputPath+"temp.xml"); 
						
							// we read the LTS
							LTS lts = new LTS();
							lts.fromAlloyXML(outputPath+"temp.xml");
							insLaxModels.put(currentIns, lts);
							
							// we model check the current instance
							found = modelCheck(insLaxModels, laxModels, changed, mySpec);	
							
							// we update the solver for finding the next instance in the case it is needed
							solvers.put(currentIns, currentSolver.next());
						}
						catch(Exception e){
							System.out.println("Error during hte generation of Alloy instances");
						}
					}
						
				}
			}	
			return found;
		}	
	}
	
	*//**
	 * A private method to model check a collection of instances and a set of global vars
	 * @param processIns	a hash mapping each instance to its process description
	 * @param processes		the processes lax models
	 * @param globalVars	the global vars of the specification
	 * @param changed		a hashmap that indicates which parameter has changed
	 * @return	t
	 *//*
	private static boolean modelCheck(HashMap<String, LTS> processesIns,HashMap<String,LTS> processes, HashMap<String, Boolean> changed, Spec mySpec){
			
		// WE CONSTRUCT THE PROGRAM
		String program = "";
		
		// the hashmap instances records the type of each instance
		//HashMap<String, String> instances = new HashMap<String,String>();
		//Iterator<String> it = processesIns.keySet().iterator();
		//while(it.hasNext()){
		//	String currentIns = it.next();
		//	if (changed.get(currentIns)){
		//		instances.put(currentIns, currentIns+"Process"); // if changed a new process for it must be created	
		//	}
		//	else{
		//		instances.put(currentIns, mySpec.getInstanceTypes().get(currentIns)); // otherwise the instance is of the declared type	
		//	}
		//}
		LinkedList<String> definedProcesses = new LinkedList<String>(); // a list to save the processes that must be defined in the program
		Iterator<String> it = processesIns.keySet().iterator();
		while(it.hasNext()){
			String currentIns = it.next();
			if (!changed.get(currentIns) && !definedProcesses.contains(mySpec.getInstanceTypes().get(currentIns))) // if not changed and the process is not in the list
				definedProcesses.add(mySpec.getInstanceTypes().get(currentIns));
			if (changed.get(currentIns)) // if changed we add it
				definedProcesses.add(currentIns);
		}
		
		HashMap<String, String> globalVars = mySpec.getGlobalVarsTypes();
		Formula prop =  mySpec.getGlobalProperty();
		LinkedList<String> writtenProcesses = new LinkedList<String>(); // a list to keep track of the written processes until now to avoid repetitions
		
		// first we declare the enum types, an enum type for each process
		//Iterator<String> it1 = processes.keySet().iterator();
		Iterator<String> it1 = definedProcesses.iterator();
		while (it1.hasNext()){
			String currentProcess = it1.next();
			LTS currentLTS = null;
			if (processes.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = processes.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = processesIns.get(currentProcess);
			program += "Enum state"+currentProcess +" = {";
			LinkedList<String> nodes = currentLTS.getNodeNames();
			for (int i=0; i<nodes.size(); i++){
				program += (i==0)? nodes.get(i) : ","+nodes.get(i);
			}
			program += "};\n";
		}
		
		// now for those 
		// now the global vars
		Iterator<String> it2 = globalVars.keySet().iterator();
		//program += "Global ";
		while (it2.hasNext()){
			String currentVar = it2.next();
			//if (it2.hasNext())
			//	program += currentVar+" : "+ globalVars.get(currentVar)+",";
			//else
				program += "Global "+currentVar+" : "+ globalVars.get(currentVar)+";\n";
				program += "Global "+"Av_"+currentVar+" : BOOL;\n"; // for each global var we have a lock
		}
		program += "\n";
		
		// the processes are written down
		//Iterator<String> it3 = processes.keySet().iterator();
		Iterator<String> it3 = definedProcesses.iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			String currentProcess = it3.next();
			if (processes.containsKey(currentProcess)){
				LinkedList<String> processPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += processes.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += processesIns.get(currentProcess).toMCProcess(pars, currentProcess+"Process", currentProcess);
			}
		}
		
		program += "\n";
		// and the main program
		program += "Main(){\n";
		//Iterator<String> it4 = instances.keySet().iterator();
		Iterator<String> it4 = processesIns.keySet().iterator();
		while (it4.hasNext()){
			String currentInstance = it4.next();
			//program  += currentInstance +":"+"NoName;\n"; //+instances.get(currentInstance)+";\n";
			if (changed.get(currentInstance))
				program  += currentInstance +":"+currentInstance+"Process"+";\n";
			else
				program  += currentInstance +":"+ mySpec.getInstanceTypes().get(currentInstance)+";\n";
		}
		
		// we run the instances
		//Iterator<String> it5 = instances.keySet().iterator();
		Iterator<String> it5 = processesIns.keySet().iterator();
		while (it5.hasNext()){
			String currentInstance = it5.next();
			//program  += "run " + currentInstance +"();\n"; // change this for process with parameters
			program += "run " + currentInstance + "(";
			LinkedList<String> parameters = mySpec.getActualPars(currentInstance);
			for (int i=0; i<parameters.size();i++){
				if (i==0)
					program+=parameters.get(i) + ", Av_"+parameters.get(i);
				else
					program+=","+parameters.get(i)+ ", Av_"+parameters.get(i);
			}
			program += ");";
		}
		program += "}";
		//System.out.println(program);
		
		// we parse the program with the model checker
		ProgramParser pparser = new ProgramParser();
		Program model = pparser.parseFromString(program);
		//System.out.println(program);
		
		
		// we get the global property and parse it
		String formString = prop.toString()+";";
		
		//System.out.println(formString);
		FormulaParser formulaParser = new FormulaParser(pparser.getSymbolsTable(), model);
	    FormulaElement form = formulaParser.parseFromString(formString);
	    //model.buildModel();
	   
	    
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{
	    	Program.myFactory.done();
	    	return false; // otherwise it is false
	    }
	   
	    //syntProgram = program;
	    //return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}*/
	

}
