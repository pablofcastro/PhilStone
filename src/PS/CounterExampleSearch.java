package PS;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import FormulaSpec.Formula;
import FormulaSpec.Type;
import JFlex.Out;
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
	HashMap<String, Boolean> disjointCexFound; // says if a new coutnerexample was found for the corresponding instance
	Spec mySpec;			// the specification
	String outputPath;		// the output path for the synthesized program
	String templatePath;	// the path to the template,
	int numberIns; 			// number of the running processes
	HashMap<String, Integer> currentCex; // used for synthesis, the maps points out the current cex used for each process, -1 indicates no counterexample
	boolean showInfo = false; // when true the methods will show the info of the search
	boolean printPDF;
	boolean cexFound = false;
	boolean cexRefined = false; // a boolean 
	int scope;
	
	/**
	 * A basic constructor
	 * @param mySpec
	 * @param outputPath
	 * @param templatePath
	 */
	public CounterExampleSearch(Spec mySpec, String outputPath, String templatePath, boolean showInfo, boolean printPDF, int scope){
		this.syntProgram = "";
		this.mySpec = mySpec;
		this.showInfo = showInfo;
		this.printPDF = printPDF;
		this.scope = scope;
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
		
		// we initialized all the elements of 
		this.disjointCexFound = new HashMap<String, Boolean>();
		for (int i=0; i<instancesList.size(); i++){
			this.disjointCexFound.put(instancesList.get(i), new Boolean(false));
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
			long startTime = System.currentTimeMillis();    

			String currentProcess = processes.get(i);
			//String currentInstance = 
			
			// the output file for the Alloy model
			String outputfilename = outputPath+currentProcess+".xml";
			
			// we obtain the alloy specification for the laxest model
			//String myProcess = instances.get(currentProcess);
			String metamodel = mySpec.metamodelToString(currentProcess, templatePath, scope);
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
			LTS lts = new LTS(mySpec.getProcessByName(currentProcess));
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
				if (instances.get(instancesList.get(j)).equals(currentProcess)){
					mapInsModels.put(instancesList.get(j), lts); // if the instance has as type the current process 
					if (this.printPDF)
						lts.toDot(outputPath+"lax"+instancesList.get(j)+".dot");
				}
			}

			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println(currentProcess+" time:" + estimatedTime);
		}
		
		System.out.println("Local Models Generated...");
		
		// the map containing the collection of counterexamples for each instance is initialized
		for (int j=0; j<instancesList.size();j++){
			this.cexActualRun.put(instancesList.get(j), new LinkedList<LinkedList<String>>());
		}
		
		// an iterator for the laxest models
		//Iterator<String> itModels = insLaxModels.keySet().iterator();
		boolean found = counterExampleSearch(0, scope);
		if (found){
			System.out.println("Program Synthesized, saved to output folder.."); 
			
			if (this.printPDF){  // we print the dots			
				for (int i=0; i<this.instancesList.size(); i++)
					this.mapInsModels.get(instancesList.get(i)).toDot(outputPath+instancesList.get(i)+"final.dot");
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
	}
	
	public boolean counterExampleSearch(int insNumber, int scope){
		boolean result = false; // an attribute to save the result
		String currentIns = instancesList.get(insNumber);
		LTS originalModel = mapInsModels.get(currentIns);
		if (showInfo){
			System.out.println("Inspecting instance: "+currentIns);
		}
		
		if (insNumber == this.numberIns - 1){ // if so we are in the base case
			if (modelCheck(currentIns, new LinkedList<LinkedList<String>>())){ // if false, we add the counterexamples to the corresponding queues
				return true;
			}
			else{
				this.cexFound = false; 
				// we try to get a program for all the cex
				int cexNumber = 0; // we use -1 for the case when no counterexample is analyzed
				// we use a collection of actual counterexamples, the idea is to add new counterexample found during hte execution of the algorithm
				LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
				this.disjointCexFound.put(currentIns, new Boolean(false));
				
				while (cexNumber < this.cexForInstance.get(this.instancesList.get(insNumber)).size() & !this.disjointCexFound.get(currentIns)){
				//while (cexNumber < this.cexForInstance.get(this.instancesList.get(insNumber)).size()){	
					if (cexNumber > -1){
						actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cex
					}
					// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
					try{
						int j = 0;
						cexRefined = false;
						A4Reporter rep = new A4Reporter();
						Module world = null;
						LTS lts = new LTS(mySpec.getProcessSpec(currentIns));
						LTS formerLTS = mapInsModels.get(currentIns);
						PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
						mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);	
						A4Options opt = new A4Options();
						opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
						opt.solver = A4Options.SatSolver.SAT4J;
						world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
						Command cmd = world.getAllCommands().get(0);
						A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
						while (sol.satisfiable()  &  !disjointCexFound.get(currentIns) & !cexRefined){ //add refined & !disjointCexFound.get(currentIns)
							if (cexRefined){
								actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber));
								mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);
								world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
								cmd = world.getAllCommands().get(0);
								sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
								cexRefined = false;
							}								
							sol.writeXML(outputPath+"temp.xml");
							lts.fromAlloyXML(outputPath+"temp.xml");
							lts.toDot(outputPath+currentIns+".dot");
							if (showInfo)
								System.out.println("Instance "+ currentIns + ", Iteration Number:"+j);
							j++;
							mapInsModels.put(currentIns, lts);
							changed.put(currentIns, new Boolean(true));
							// if we generate a correct program then output true
							if (modelCheck(currentIns, actualCexs)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
								return true;
						
							// else continue with the search, and restore the previous values
							mapInsModels.put(currentIns, formerLTS);
							changed.put(currentIns, new Boolean(false));
							sol = sol.next();
						}
						//else{
						//	break;
						//}
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
					if (!cexRefined)
						cexNumber++;
					//j++;
				}
				// after this while we know that no program was found for this instance of the search
				return false;
			}
		}// end of base case
		else{ // recursive case
			int cexNumber = -1; // at the beginning we start with no counterexample
			LinkedList<LinkedList<String>> actualCexs = new LinkedList<LinkedList<String>>();
			// while there are counterexamples in the queue try...
			int k =0;
			
			while (cexNumber < this.cexForInstance.get(currentIns).size()){
						
				if (cexNumber == -1){ // if -1 then no counterexample will take into account
					if (counterExampleSearch(insNumber+1, scope))
						return true;
				}
				else{
					if (showInfo){
						System.out.println("Instance:"+currentIns);
						System.out.println(actualCexs.size());
						System.out.println(this.cexActualRun.get(currentIns).size());
						
					}
					actualCexs.add(this.cexForInstance.get(currentIns).get(cexNumber)); // we add the current cexs	
					int p = 0;
					// while we can get more counterexamples using the current counterexamples we try to find a program
					//while (cexNumber < this.cexForInstance.get(currentIns).size()){//actualCexs.size() < this.cexActualRun.get(currentIns).size()){
						
						// WE GET A POSSIBLE PROGRAM AND MODEL CHECK IT
						try{
							A4Reporter rep = new A4Reporter();
							Module world = null;
							LTS lts = new LTS(mySpec.getProcessSpec(currentIns));;
							LTS formerLTS = mapInsModels.get(currentIns);
							PrintWriter writer = new PrintWriter(outputPath+"Instances.als", "UTF-8");
							mapInsModels.get(currentIns).getAlloyInstancesSpec(writer,scope, actualCexs);		
							A4Options opt = new A4Options();
							opt.originalFilename = outputPath+"Instances.als"; // the specification metamodel
							opt.solver = A4Options.SatSolver.SAT4J;
							world = CompUtil.parseEverything_fromFile(rep, null, outputPath+"Instances.als");
							Command cmd = world.getAllCommands().get(0);
							A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
							while (sol.satisfiable()){  // !disjointCexFound.get(currentIns)??
								if (showInfo)
									System.out.println("Instance "+ currentIns + ", Iteration Number:"+p);
								sol.writeXML(outputPath+"temp.xml");
								lts.fromAlloyXML(outputPath+"temp.xml");
								lts.toDot(outputPath+"instance"+p+".dot");
								//System.out.println("Instance Number:"+i);
								p++;
								mapInsModels.put(currentIns, lts);
								changed.put(currentIns, new Boolean(true));
								
								// we try with this model recursively
								if (counterExampleSearch(insNumber+1, scope)) // model check generates new counterexamples, TBD: we need to add any found instance to actualCexs
									return true;
					
								// else continue with the search, and restore the previous values
								changed.put(currentIns, new Boolean(false));
								mapInsModels.put(currentIns, formerLTS);
								// and with add more counterexamples to the bag
								//actualCexs.addLast(this.cexActualRun.get(currentIns).get(actualCexs.size()));
								sol = sol.next();
							}
							
							System.out.println("trying...."+k);
							k++;
					
						}// end of try
						catch(Exception e){
							System.out.println("Input-Output Error trying to write Alloy files.");
							e.printStackTrace();//System.out.println(e);
						}
					//}
					// if we did not find anything the current cex, we reset the bag  of counter examples and try with the next one
					actualCexs.clear();
					this.cexActualRun.get(currentIns).clear();
				}
				cexNumber++;
			}
			return false; // if no program found then we return false	
		}				
	}
	
	private void addCounterExToProcess(String process, LinkedList<String> cex){
		if (showInfo){
			System.out.println("Cex to add:");
			System.out.println(cex);
			System.out.println("Cex in the Queue:");
			System.out.println(this.cexForInstance.get(process));
		}
		if (cex.size() < 2 ){ // For ACTL!: if size is less than 2 then the current process do not participate in the counterexample and all the branch of the backtracking can be pruned
			this.disjointCexFound.put(process, new Boolean(true));
			return;
		}
		boolean inCexList = false;
		LinkedList<LinkedList<String>> cexList = cexForInstance.get(process);
		for (int i=0; i<cexList.size(); i++){
			if (lInclusion(cexList.get(i), cex)){ // if the counterexample is already in the queue, cexList[i] is included in cex
				inCexList = true; // we do not need to add it
				if (showInfo)
					System.out.println("rinclusion");
			}
			if (rInclusion(cexList.get(i), cex)){
				cexList.remove(i);
				cexList.add(i, cex); // we swap the former cex for the new one
									    // since the left inclusion says that the right one is more refined		
				inCexList = true;
				if (showInfo)
					System.out.println("linclusion");
			}
			//if (disjoint(cexList.get(i), cex)){
			//	this.disjointCexFound.put(process, new Boolean(true)); // a disjoint cex was found
			//	cexList.addLast(cex);
			//}
				
		}
		if (!inCexList){
				cexList.addLast(cex);
				this.disjointCexFound.put(process, new Boolean(true));
		}
		boolean inRunningList = false;
		for (int i=0; i<this.cexActualRun.get(process).size(); i++){
			if (rInclusion(cexActualRun.get(process).get(i), cex)) // if the counterexample is already in the queue
				inRunningList = true; // we do not need to add it
			if (lInclusion(cexActualRun.get(process).get(i), cex)){
				cexActualRun.get(process).remove(i);
				cexActualRun.get(process).add(i, cex); // we swap the former cex for the new one
									    // since the left inculsion says that the right one is more refined		
				//cexActualRun.get(process).add(cex);
				inRunningList = true;
				cexRefined = true;
			}
			//if (disjoint(cexActualRun.get(process).get(i), cex)){
			//	this.disjointCexFound.put(process, new Boolean(true)); // a disjoint cex was found
			//	cexActualRun.get(process).addLast(cex);
			//}
		}
		if (!inRunningList){
			cexActualRun.get(process).addLast(cex);
			this.disjointCexFound.put(process, new Boolean(true));
		}
	}
	
	private void processCounterExample(CounterExample c){	
		if (this.counterExamples.contains(c)){
			this.cexFound = false;
		}
		else{
			this.cexFound = true;
			//System.out.println(this.counterExamples);
			//System.out.println("new counter example");
			if (showInfo){
				System.out.println("CounterExample found:");
				System.out.println(c);
			}
			this.counterExamples.add(c);
			for (int i=0; i<this.instancesList.size(); i++){
				addCounterExToProcess(this.instancesList.get(i), c.getRuns(this.instancesList.get(i)));
			}
		}
	}
	
	public LTS getAssociatedLTS(String ins){
		return this.mapInsModels.get(ins);
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
			LinkedList<String> nodes = currentLTS.getEqClassesNames();
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
				//program += "Global "+currentVar+" : "+ globalVars.get(currentVar)+";\n"; // this has to be added when we have monitors			
				if (!mySpec.isPrimVar(currentVar))
					program += "Global "+"Av_"+currentVar+" : BOOL;\n"; // for each global var we have a lock
				if (globalVars.get(currentVar).equals("BOOL") || globalVars.get(currentVar).equals("PRIMBOOL"))
					program += "Global "+ "Prop_"+currentVar+" : BOOL;\n"; // and the corresponding current var
				// TO DO: ADD INTEGERS
				// we need to distinguish between locks, ints and bools
		}
		program += "\n";
		
		// the processes are written down
		//Iterator<String> it3 = processes.keySet().iterator();
		Iterator<String> it3 = definedProcesses.iterator();
		while (it3.hasNext()){
			HashMap<String, String> pars = new HashMap<String, String>();
			String currentProcess = it3.next();
			if (mapProcessModels.containsKey(currentProcess)){
				LinkedList<String> processBoolPars = mySpec.getProcessByName(currentProcess).getBoolParNames();
				for (int i=0; i<processBoolPars.size();i++){
					pars.put(processBoolPars.get(i), "BOOL");
				}
				LinkedList<String> processPrimBoolPars = mySpec.getProcessByName(currentProcess).getBoolPrimParNames();
				for (int i=0; i<processPrimBoolPars.size();i++){
					pars.put(processPrimBoolPars.get(i), "PRIMBOOL");
				}
				
				LinkedList<String> processLockPars = mySpec.getProcessByName(currentProcess).getLockParNames();
				for (int i=0; i<processLockPars.size();i++){
					pars.put(processLockPars.get(i), "LOCK");
				}
				program += mapProcessModels.get(currentProcess).toMCProcess(pars, currentProcess, currentProcess); // no parameters by now
				
			}
			else{
				LinkedList<String> processPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolParNames();
				for (int i=0; i<processPars.size();i++){
					pars.put(processPars.get(i), "BOOL");
				}
				LinkedList<String> processPrimBoolPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getBoolPrimParNames();
				for (int i=0; i<processPrimBoolPars.size();i++){
					pars.put(processPrimBoolPars.get(i), "PRIMBOOL");
				}
				
				LinkedList<String> processLockPars = mySpec.getProcessByName(mySpec.getInstanceTypes().get(currentProcess)).getLockParNames();
				for (int i=0; i<processLockPars.size();i++){
					pars.put(processLockPars.get(i), "LOCK");
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
			for (int i=parameters.size()-1; i>=0;i--){
				if (i==parameters.size()-1 && parameters.size()>1){
					//program+=parameters.get(i) + ", Av_"+parameters.get(i); // this must be changed for monitors
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.BOOL)
						program+= "Prop_"+parameters.get(i)+", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.PRIMBOOL)
						program+= "Prop_"+parameters.get(i)+", ";
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.LOCK)
						program+= "Av_"+parameters.get(i)+", ";
				}
				else{
					//program+=","+parameters.get(i)+ ", Av_"+parameters.get(i);
					//program+=", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.BOOL)
						program+= ","+"Prop_"+parameters.get(i)+", Av_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.PRIMBOOL)
						program+= ","+"Prop_"+parameters.get(i);
					if (mySpec.getGlobalVarType(parameters.get(i)) == Type.LOCK)
						program+= "Av_"+parameters.get(i);
				}
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
	    //System.out.println(formString);
	    
	    
	    if (showInfo){
	    	System.out.println(program);
	    	//System.out.println(form);
	    }
	    //it coDCTL_MC.printMess();
	    // we model check the specification together with the formula
	    if (DCTL_MC.mc_algorithm_eq(form, model)){
	    	syntProgram = program; // if true we save the program
	    	return true;
	    }
	    else{ // if the model checking is not successful we search for counterexamples
	    	FormulaElement negForm = new formula.Negation("!", form);
	    	CounterExample c = new CounterExample();
	    	if (DCTL_MC.getWitnessAsMaps(negForm, model, ins).isEmpty()){ // NO SENSE A EMPTY COUNTEREXAMPLE!
	    		syntProgram = program;
	    		return true;
	    	}
	    	if (showInfo)
	    		System.out.println("Cex from model checker: "+DCTL_MC.getWitnessAsMaps(negForm, model, ins));
	    	c.addRuns(DCTL_MC.getWitnessAsMaps(negForm, model, ins));
	    	//System.out.println(c);
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
	
	/**
	 * @param ins
	 * @return whether a given instance was changed or not
	 */
	public boolean changed(String ins){
		if (changed.containsKey(ins)){
			return changed.get(ins);
		}
		else{
			return false;
		}
	}
	
	public LTS getLTSForInstance(String ins){
		return this.mapInsModels.get(ins);
	}
	
	public LTS getLTSForProcess(String process){
		return this.mapProcessModels.get(process);
	}
	
	/**
	 * s1 IS INCLUDE IN s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean lInclusion(LinkedList<String> s1, LinkedList<String> s2){
		if (s1.size()>1){
			if (s2.size() >= s1.size()){
				int i=0;
				int j=0;
				while (j<s2.size()-1){
					if (s1.get(i).equals(s2.get(j)) && s1.get(i+1).equals(s2.get(j+1))){
						i++; // if found we search the next element
						j++;
						if (i == s1.size()-1)
							break;
					}
					else{
						j++; // otherwise we continue searching
					}
				}
				return (i == s1.size()-1);
			}
			else{
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	/**
	 * s2 IS INCLUDED IN s1
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean rInclusion(LinkedList<String> s1, LinkedList<String> s2){
		return lInclusion(s2,s1);
	}
	
	private boolean someInclusion(LinkedList<String> s1, LinkedList<String> s2){
		return lInclusion(s1, s2) || rInclusion(s1, s2);
	}
	
	private boolean disjoint(LinkedList<String> s1, LinkedList<String> s2){
		return !someInclusion(s1,s2);
	}
	
}
