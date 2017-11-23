package PS;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import FormulaSpec.Formula;
import LTS.*;
import Spec.*;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import faulty.Program;
import formula.FormulaElement;
import mc.DCTL_MC;
import mc.FormulaParser;
import mc.ProgramParser;

/**
 * This class implements the algorithm for performing search of program by counterexamples
 * @author Pablo
 *
 */
public class CounterExampleSearch {
	String syntProgram; 								// the program synthesized
	LinkedList<String> processes;						// a list of processes
	LinkedList<String> instancesList; 					// a list of instances
	HashMap<String, String> instances; 
	HashMap<String, Boolean> changed; 					// a hash map to indicate if a new process has been generated for a given instance
	HashMap<String, LTS> mapInsModels; 					// a hashmap mapping each INSTANCE to its candidate model,
	HashMap<String, LTS> mapProcessModels;  			// a hashmap mapping each PROCESS to its laxest model
	LinkedList<CounterExample> counterExamples; 						// a set containing all the counter examples found until now,
	HashMap<String, LinkedList<LinkedList<String>>> cexForInstance; // it contains for each instance a queue contain a collection of counterexamples
	HashMap<String, LinkedList<LinkedList<String>>> cexActualRun; // it keeps the counter examples found in the actual execution of the algorithm
	Spec mySpec;			// the specification
	String outputPath;		// the output path for the synthesized program
	String templatePath;	// the path to the template,
	int numberIns; 			// number of the running processes
	HashMap<String, Integer> currentCex; // used for synthesis, the maps points out the current cex used for each process, -1 indicates no counterexample
	boolean showInfo = false; // when true the methods will show the info of the search
	
	
	/**
	 * A basic constructor
	 * @param mySpec
	 * @param outputPath
	 * @param templatePath
	 */
	public CounterExampleSearch(Spec mySpec, String outputPath, String templatePath){
		this.syntProgram = "";
		this.mySpec = mySpec;
		processes = mySpec.getProcessesNames();
		this.instances = mySpec.getInstanceTypes();
		instancesList = new LinkedList<String>(instances.keySet());
		this.changed = new HashMap<String,Boolean>();
		
		// we initialise changed with false for every instance
		for (int i=0; i<instancesList.size();i++){
			changed.put(instancesList.get(i), new Boolean(false));
		}
		this.mapInsModels = new HashMap<String, LTS>(); // insLaxModels
		this.mapProcessModels = new HashMap<String, LTS>(); // laxModels
		this.counterExamples = new LinkedList<CounterExample>();
		this.cexForInstance = new HashMap<String, LinkedList<LinkedList<String>>>();
		for (int i=0; i<instancesList.size(); i++){
			this.cexForInstance.put(instancesList.get(i), new LinkedList<LinkedList<String>>());
		}
		this.cexActualRun = new HashMap<String, LinkedList<LinkedList<String>>>();
		this.outputPath = outputPath;
		this.templatePath = templatePath;
		this.numberIns = this.instancesList.size();
	}
	
	/**
	 * 
	 * @return	the synthesized program
	 */
	public String getSyntProgram(){
		return this.syntProgram;
	}
	
	/**
	 * It starts the synthesis of programs guided by counterexamples
	 */
	public void startSearch(){
		System.out.println("Using Search Guided by Counterexamples for Synthesis...");
		
		// STEP 1: We generate the laxest model for each instance
		for (int i=0; i<processes.size(); i++){
			String currentProcess = processes.get(i);
			//String currentInstance = 
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			//String myProcess = instances.get(currentProcess);
			String metamodel = mySpec.metamodelToString(currentProcess, templatePath);
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
			mapProcessModels.put(currentProcess, lts);
			
			// we store the laxest model for each instance, at the beginning they coincide with those of the processes
			for (int j=0; j<instancesList.size();j++){
				if (instances.get(instancesList.get(j)).equals(currentProcess))
					mapInsModels.put(instancesList.get(j), lts); // if the instance has as type the current process then the lts is set
			}
			
		}
		
		System.out.println("Laxest Models Generated...");
		
		// the map containing the collection of counterexamples for each instance is initialized
		for (int j=0; j<instancesList.size();j++){
			this.cexActualRun.put(instancesList.get(j), new LinkedList<LinkedList<String>>());
		}
		
		// an iterator for the laxest models
		//Iterator<String> itModels = insLaxModels.keySet().iterator();
		boolean found = counterExampleSearch(0, 14);
		if (found){
			System.out.println("Program Synthesized, saved to output folder.."); 
			
			// we print the dots
			for (int i=0; i<this.instancesList.size(); i++)
				this.mapInsModels.get(instancesList.get(i)).toDot(outputPath+instancesList.get(i)+".dot");
			
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
	}
	
	public boolean counterExampleSearch(int insNumber, int scope){
		boolean result = false; // an attribute to save the result
		
		String currentIns = instancesList.get(insNumber);
		LTS originalModel = mapInsModels.get(currentIns);
		
		if (insNumber == this.numberIns - 1){ // if so we are in the base case
			if (modelCheck(currentIns, new LinkedList<LinkedList<String>>())){ // if false, we add the counterexamples to the corresponding queues
				return true;
			}
			else{
				// we try to get a program for all the cex
				int cexNumber = -1; // we use -1 for the case when no counterexample is analyzed
				// we use a collection of actual counterexamples, the idea is to add new counterexample found during hte execution of the algorithm
				LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
				while (cexNumber < this.cexForInstance.get(this.instancesList.get(insNumber)).size()){
					if (cexNumber > 0)
						actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cex	
					// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
					try{
						A4Reporter rep = new A4Reporter();
						Module world = null;
						LTS lts = new LTS();
						PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
						mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);	
						A4Options opt = new A4Options();
						opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
						opt.solver = A4Options.SatSolver.SAT4J;
						world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
						Command cmd = world.getAllCommands().get(0);
						A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
						assert sol.satisfiable();
						sol.writeXML(outputPath+"temp.xml");
						lts.fromAlloyXML(outputPath+"temp.xml");
						//lts.toDot(outputPath+"instance"+i+".dot");
						//System.out.println("Instance Number:"+i);
						//i++;
						mapInsModels.put(currentIns, lts);
						changed.put(currentIns, new Boolean(true));
						// if we generate a correct program then output true
						if (modelCheck(currentIns, actualCexs)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
							return true;
						
						// else continue with the search, and restore the previous values
						changed.put(currentIns, new Boolean(false));
					}// end of try
					catch(Exception e){
						System.out.println("Input-Output Error trying to write Alloy files.");
						try{
							PrintWriter writererror = new PrintWriter(outputPath+"InstancesError.als", "UTF-8");
							mapInsModels.get(currentIns).getAlloyInstancesSpec(writererror,scope, actualCexs);	
						}catch(Exception e1){
						}
						System.out.println(e.getMessage());
						e.printStackTrace();//System.out.println(e);
					}
					cexNumber++;
				}
				// after this while we know that no program was found for this instance of the search
				return false;
			}
		}// end of base case
		else{ // recursive case
			int cexNumber = -1; // at the beginning we start with no counterexample
			LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
			// while there are counterexamples in the queue try...
			while (cexNumber < this.cexForInstance.get(currentIns).size()){
						
				if (cexNumber == -1){ // if -1 then no counterexample will take into account
					if (counterExampleSearch(insNumber+1, scope))
						return true;
				}
				else{
					actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cexs	
			
					// while we can get more counterexamples using the current counterexamples we try to find a program
					while (actualCexs.size() < this.cexActualRun.get(currentIns).size()){
						
						// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
						try{
							A4Reporter rep = new A4Reporter();
							Module world = null;
							LTS lts = new LTS();
							PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
							mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);		
							A4Options opt = new A4Options();
							opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
							opt.solver = A4Options.SatSolver.SAT4J;
							world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
							Command cmd = world.getAllCommands().get(0);
							A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
							assert sol.satisfiable();
							sol.writeXML(outputPath+"temp.xml");
							lts.fromAlloyXML(outputPath+"temp.xml");
							//lts.toDot(outputPath+"instance"+i+".dot");
							//System.out.println("Instance Number:"+i);
							//i++;
							mapInsModels.put(currentIns, lts);
							changed.put(currentIns, new Boolean(true));
							
							// we try with this model recursively
							if (counterExampleSearch(insNumber+1, scope)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
								return true;
					
							// else continue with the search, and restore the previous values
							changed.put(currentIns, new Boolean(false));
						
							// and with add more counterexamples to the bag
							actualCexs.addLast(this.cexActualRun.get(currentIns).get(actualCexs.size()));
					
						}// end of try
						catch(Exception e){
							System.out.println("Input-Output Error trying to write Alloy files.");
							e.printStackTrace();//System.out.println(e);
						}
					}
					// if we did not find anything the current cex, we reset the bag  of counter example and try with the next one
					actualCexs.clear();
					this.cexActualRun.get(currentIns).clear();
				}
				cexNumber++;
			}
			return false; // if no program found then we return false	
		}				
	}
	
	private void addCounterExToProcess(String process, LinkedList<String> cex){
		//System.out.println(cex);
		LinkedList<LinkedList<String>> cexList = cexForInstance.get(process);
		boolean inCexList = false;
		for (int i=0; i<cexList.size(); i++){
			if (cexList.get(i).equals(cex)) // if the counterexample is already in the queue
				inCexList = true;
		}
		// else we add it at the end of the queue
		if (!inCexList)
			cexList.addLast(cex);
		boolean inRunningList = false;
		for (int i=0; i<this.cexActualRun.get(process).size(); i++){
			if (this.cexActualRun.get(process).equals(cex)) // if the counterexample is already in the queue
				inRunningList = true;
		}
		if (!inRunningList)
			this.cexActualRun.get(process).addLast(cex);
	}
	
	private void processCounterExample(CounterExample c){	
		for (int i=0; i<this.instancesList.size(); i++){
			addCounterExToProcess(this.instancesList.get(i), c.getRuns(this.instancesList.get(i)));
		}	
	}
	
	/**
	 * A private method to model check a collection of instances and a set of global vars
	 * @param ins	the current instance that the algorithms is inspecting
	 * @param cexs			a collection of counterexamples, if any counterexample is found it must be added here
	 * @return	true when a program was found
	 */
	private boolean modelCheck(String ins, LinkedList<LinkedList<String>> cexs){
			
		// WE CONSTRUCT THE PROGRAM
		String program = "";
		
		LinkedList<String> definedProcesses = new LinkedList<String>(); // a list to save the processes that must be defined in the program
		Iterator<String> it = mapInsModels.keySet().iterator();
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
			if (this.mapProcessModels.containsKey(currentProcess)) // if it is a process defined in the program
				currentLTS = mapProcessModels.get(currentProcess);
			else // otherwise is an instance with its own process definition
				currentLTS = this.mapInsModels.get(currentProcess);
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
			if (mapProcessModels.containsKey(currentProcess)){
				LinkedList<String> processPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += mapProcessModels.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				program += mapInsModels.get(currentProcess).toMCProcess(pars, currentProcess+"Process", currentProcess);
			}
		}
		
		program += "\n";
		// and the main program
		program += "Main(){\n";
		//Iterator<String> it4 = instances.keySet().iterator();
		Iterator<String> it4 = mapInsModels.keySet().iterator();
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
		Iterator<String> it5 = mapInsModels.keySet().iterator();
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
		
		
		// we get the global property and parse it
		String formString = prop.toString()+";";
		
		//System.out.println(formString);
		FormulaParser formulaParser = new FormulaParser(pparser.getSymbolsTable(), model);
	    FormulaElement form = formulaParser.parseFromString(formString);
	    //model.buildModel();
	   
	    //System.out.println(program);
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{ // if the model checking is not sucessful we search for counterexamples
	    	FormulaElement negForm = new formula.Negation("!", form);
	    	CounterExample c = new CounterExample();
	    	c.addRuns(DCTL_MC.getWitnessAsMaps(negForm, model, ins));
	    	cexs.addLast(c.getRuns(ins)); // we add the counterexample to the collection of counterexamples of the current instance
	    	this.processCounterExample(c);
	    	// TBD add the counterexample to the collection of counterexamples
	    	Program.myFactory.done();
	    	return false; // otherwise it is false
	    }
	   
	    //syntProgram = program;
	    //return DCTL_MC.mc_algorithm_eq(form, model);
	    //return true;
	}
	
}
