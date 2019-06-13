package PS;
import java.util.*;

import FormulaSpec.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

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
	private static int pathBound = 0;
	private static LinkedList<String> instancesList; // the list of instances 
	private static String path = "";
	private static String specName = "";
	private static String smallSpec = ""; // this is used for genSearching, it saves the path of the spec with small processes
	private static boolean lexSearch = false;
	private static boolean cexSearch = true; // by default we use cexSearch
	private static boolean genSearch = false;
	private static boolean herSearch = false;
	private static boolean BMC = false;
	private static boolean Electrum = false;
	private static boolean NuXMV = false;
	private static boolean NuSMV = false;
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
				if (args[i].equals("-NuXMV")){
					lexSearch = false;
					cexSearch = false;	
					genSearch = false;
					herSearch = false;
					NuXMV = true;
					BMC = false;
					Electrum = false;
					NuSMV = false;
					continue;
				}
				if (args[i].equals("-NuSMV")){
					lexSearch = false;
					cexSearch = false;	
					genSearch = false;
					herSearch = false;
					NuXMV = false;
					NuSMV = true;
					BMC = false;
					Electrum = false;
					continue;
				}
				if(args[i].startsWith("-BMC=")){
					lexSearch = false;
					cexSearch = false;	
					genSearch = false;
					herSearch = false;
					BMC = true;
					NuXMV = false;
					Electrum = false;
					NuSMV = false;
					try{
						pathBound = Integer.parseInt(args[i].replace("-BMC=",""));
						continue;
					}
					catch (NumberFormatException e){
						System.out.println("Wrong int parameter after option -BMC");
						System.exit(0);
					}
				}
				if(args[i].startsWith("-Electrum=")){
					lexSearch = false;
					cexSearch = false;	
					genSearch = false;
					herSearch = false;
					BMC = false;
					NuXMV = false;
					NuSMV = false;
					Electrum = true;
					try{
						pathBound = Integer.parseInt(args[i].replace("-Electrum=",""));
						continue;
					}
					catch (NumberFormatException e){
						System.out.println("Wrong int parameter after option -BMC");
						System.exit(0);
					}
				}
				if (args[i].startsWith("-scope=")){					
					try{
						scope = Integer.parseInt(args[i].replace("-scope=",""));
						continue;
					}
					catch (NumberFormatException e){
						System.out.println("Wrong int paramater after option -scope");
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
			setEnv("PhilStone", "./");
//			System.out.println("The Environment Variable PhilStone is not set. It must be set to the current root path of the tool.");
//	        System.exit(0);
		}
		
		
		// If the args are OK we proceed
		String outputPath = System.getenv("PhilStone")+"/output/";
		String templateDir =  System.getenv("PhilStone")+"/templates/";
		String pdfDir = System.getenv("PhilStone")+"/pdf/";
		SpecAux result = null;
		try{ // we parse the specification
			FileReader specFile = new FileReader(specName);
			parser p = new parser(new Scanner(specFile));
			result = (SpecAux) p.parse().value;

		}
		catch(Exception e){
			System.out.println("Error while parsing the Spec File");
//            e.printStackTrace(System.out);
            System.exit(0);
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
			if (BMC){ // this is for using alloy model checking
				CounterExampleSearch cs = new CounterExampleSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope, true, pathBound);
				cs.startSearch();
			}
			if (Electrum){
				CounterExampleSearch cs = new CounterExampleSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope, false, pathBound);
				cs.setElectrumBMC(pathBound);
				cs.startSearch();
			}
			if (NuXMV){
				CounterExampleSearch cs = new CounterExampleSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope, false, pathBound);
				cs.setNuXMVSearch(pathBound); // FIX ME:nuxmv does not need a pathbound! 
				cs.startSearch();
			}
			if (NuSMV){
				CounterExampleSearch cs = new CounterExampleSearch(mySpec, outputPath, templateDir, showInfo, writePdf, scope, false, pathBound);
				cs.setNuSMVBMC(pathBound);
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
	
	public static void setEnv(String key, String value) {
	    try {
	        Map<String, String> env = System.getenv();
	        Class<?> cl = env.getClass();
	        Field field = cl.getDeclaredField("m");
	        field.setAccessible(true);
	        Map<String, String> writableEnv = (Map<String, String>) field.get(env);
	        writableEnv.put(key, value);
	    } catch (Exception e) {
	        throw new IllegalStateException("Failed to set environment variable", e);
	    }
	}

}
