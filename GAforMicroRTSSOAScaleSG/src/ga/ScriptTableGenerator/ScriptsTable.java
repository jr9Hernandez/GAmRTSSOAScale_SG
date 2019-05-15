package ga.ScriptTableGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import ai.ScriptsGenerator.TableGenerator.FunctionsforGrammar;
import ai.ScriptsGenerator.TableGenerator.Parameter;
import ai.ScriptsGenerator.TableGenerator.TableCommandsGenerator;
import ga.config.ConfigurationsGA;
import ga.model.Chromosome;
import rts.units.UnitTypeTable;


public class ScriptsTable {
	
	static Random rand = new Random();
	private int currentSizeTable;

	/**
	 * @return the currentSizeTable
	 */


	private HashMap<String, BigDecimal> scriptsTable ;
	private int numberOfTypes;
	private TableCommandsGenerator tcg;
	private FunctionsforGrammar functions;

	private String pathTableScripts;
	
	public ScriptsTable(String pathTableScripts){
		this.scriptsTable = new HashMap<>();
		this.pathTableScripts=pathTableScripts;
		this.tcg=TableCommandsGenerator.getInstance(new UnitTypeTable());
		this.numberOfTypes=tcg.getNumberTypes();
		functions=new FunctionsforGrammar();
	}
	

	public ScriptsTable(HashMap<String, BigDecimal> scriptsTable,String pathTableScripts) {
		super();
		this.scriptsTable = scriptsTable;
		this.pathTableScripts=pathTableScripts;
		this.tcg=TableCommandsGenerator.getInstance(new UnitTypeTable());
		this.numberOfTypes=tcg.getNumberTypes();
		functions=new FunctionsforGrammar();
	}



	public HashMap<String, BigDecimal> getScriptTable() {
		return scriptsTable;
	}


	public void addScript(String chromosomeScript){
		this.scriptsTable.put(chromosomeScript, BigDecimal.ZERO);
	}	
	
	public void print(){
		System.out.println("-- Table Scripts --");
		for(String c : scriptsTable.keySet()){
			//c.print();
			System.out.print(c);
		}
		System.out.println("-- Table Scripts --");
	}
	
	public void printWithValue(){
		System.out.println("-- Table Script --");
		for(String c : scriptsTable.keySet()){
			System.out.println(c);
			System.out.println("Value = "+ this.scriptsTable.get(c));
		}
		System.out.println("-- Table Scripts --");
	}

	
	//static methods
	
	public ScriptsTable generateScriptsTable(int size){
		
		HashMap<String, BigDecimal> newChromosomes = new HashMap<>();
		String tChom;
		PrintWriter f0;
		try {
			f0 = new PrintWriter(new FileWriter(pathTableScripts+"ScriptsTable.txt"));
			
			int i=0;
			while(i<size)
			{
				//tChom = new ChromosomeScript();				
				//int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME_SCRIPT)+1;
				int sizeCh=rand.nextInt(ConfigurationsGA.MAX_QTD_COMPONENTS)+1;
				tChom=buildScriptGenotype(sizeCh);
				
//				for (int j = 0; j < sizeCh; j++) {
//					int typeSelected=rand.nextInt(numberOfTypes);
//					int sizeRulesofType=tcg.getBagofTypes().get(typeSelected).size();
//					int idRuleSelected=tcg.getBagofTypes().get(typeSelected).get(rand.nextInt(sizeRulesofType));
//					tChom.addGene(idRuleSelected);
//				}
				
				if(!newChromosomes.containsKey(tChom))
				{
				newChromosomes.put(tChom, BigDecimal.valueOf(i));
				f0.println(i+" "+tChom);
				i++;
				
				}
			    
			}
			f0.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		for (int i = 0; i < size; i++) {

		}
		ScriptsTable st = new ScriptsTable(newChromosomes,pathTableScripts);
		return st;
	}
	
	public String buildScriptGenotype(int sizeGenotypeScript )
	{
		String genotypeScript = "";
		int numberComponentsAdded=0;
		int openParenthesis=0;
		boolean canCloseParenthesis=false;
		boolean isOpen=false;
		int canOpenParenthesis=0;

		while(numberComponentsAdded<sizeGenotypeScript)
		{
			int typeComponent = rand.nextInt(2);

			//basic function
			if(typeComponent==0)
			{
				genotypeScript=genotypeScript+returnBasicFunction();
				numberComponentsAdded++;
				canCloseParenthesis=true;
				if(isOpen==false)
				{
					canOpenParenthesis=0;
				}
			}
			//conditional
			else if(typeComponent==1 && numberComponentsAdded<sizeGenotypeScript-1)
			{
				genotypeScript=genotypeScript+returnConditional();
				genotypeScript=genotypeScript+"(";
				numberComponentsAdded++;
				openParenthesis++;
				canOpenParenthesis=1;
				canCloseParenthesis=false;
				isOpen=true;

			}

			//close parenthesis
			if(rand.nextInt(2)>0 && openParenthesis>0 && canCloseParenthesis==true)
			{
				genotypeScript=genotypeScript.substring(0, genotypeScript.length() - 1);
				genotypeScript=genotypeScript+") ";
				openParenthesis--;
				isOpen=false;
			}

			//open parenthesis
			if(rand.nextInt(2)>0 && canOpenParenthesis>0 && isOpen==false && numberComponentsAdded<sizeGenotypeScript)
			{
				genotypeScript=genotypeScript+"(";
				openParenthesis++;
				canOpenParenthesis--;
				isOpen=true;
				canCloseParenthesis=false;
			}
			
			//ensure close open parenthesis
			if(numberComponentsAdded==sizeGenotypeScript && openParenthesis>0)
			{
				while(openParenthesis>0)
				{
					genotypeScript=genotypeScript.substring(0, genotypeScript.length() - 1);
					genotypeScript=genotypeScript+") ";
					openParenthesis--;	
				}
			
			}
		}
		//

		return genotypeScript;

	}
	
	public String returnBasicFunction()
	{
		String basicFunction="";
		int limitInferior;
		int limitSuperior;
		String discreteValue;
		//int id=rand.nextInt(ConfigurationsGA.QTD_RULES_BASIC_FUNCTIONS);
		int idBasicActionSelected=rand.nextInt(functions.getBasicFunctionsForGrammar().size());
		FunctionsforGrammar functionChosen=functions.getBasicFunctionsForGrammar().get(idBasicActionSelected);
		basicFunction=basicFunction+functionChosen.getNameFunction()+"(";
		for(Parameter parameter:functionChosen.getParameters())
		{
			if(parameter.getDiscreteSpecificValues()==null)
			{
				limitInferior=(int)parameter.getInferiorLimit();
				limitSuperior=(int)parameter.getSuperiorLimit();
				int parametherValueChosen = rand.nextInt(limitSuperior-limitInferior) + limitInferior;
				basicFunction=basicFunction+parametherValueChosen+",";
			}
			else
			{
				int idChosen=rand.nextInt(parameter.getDiscreteSpecificValues().size());
				discreteValue=parameter.getDiscreteSpecificValues().get(idChosen);
				basicFunction=basicFunction+discreteValue+",";
			}
		}
		basicFunction=basicFunction.substring(0, basicFunction.length() - 1);
		basicFunction=basicFunction+") ";
		return basicFunction;
	}
	
	public String returnConditional()
	{
		
		String conditional="";
		int limitInferior;
		int limitSuperior;
		String discreteValue;
		//int id=rand.nextInt(ConfigurationsGA.QTD_RULES_BASIC_FUNCTIONS);
		int idconditionalSelected=rand.nextInt(functions.getConditionalsForGrammar().size());
		FunctionsforGrammar functionChosen=functions.getConditionalsForGrammar().get(idconditionalSelected);
		conditional=conditional+functionChosen.getNameFunction()+"(";
		for(Parameter parameter:functionChosen.getParameters())
		{
			if(parameter.getDiscreteSpecificValues()==null)
			{
				
				limitInferior=(int)parameter.getInferiorLimit();
				limitSuperior=(int)parameter.getSuperiorLimit();
				int parametherValueChosen = rand.nextInt(limitSuperior-limitInferior) + limitInferior;
				conditional=conditional+parametherValueChosen+",";
			}
			else
			{
				int idChosen=rand.nextInt(parameter.getDiscreteSpecificValues().size());
				discreteValue=parameter.getDiscreteSpecificValues().get(idChosen);
				conditional=conditional+discreteValue+",";
			}
		}
		conditional=conditional.substring(0, conditional.length() - 1);
		conditional="if("+conditional+")) ";
		return conditional;
	}
	
	public String returnBasicFunctionClean()
	{
		String basicFunction="";
		int limitInferior;
		int limitSuperior;
		String discreteValue;
		//int id=rand.nextInt(ConfigurationsGA.QTD_RULES_BASIC_FUNCTIONS);
		int idBasicActionSelected=rand.nextInt(functions.getBasicFunctionsForGrammar().size());
		FunctionsforGrammar functionChosen=functions.getBasicFunctionsForGrammar().get(idBasicActionSelected);
		basicFunction=basicFunction+functionChosen.getNameFunction()+"(";
		for(Parameter parameter:functionChosen.getParameters())
		{
			if(parameter.getDiscreteSpecificValues()==null)
			{
				limitInferior=(int)parameter.getInferiorLimit();
				limitSuperior=(int)parameter.getSuperiorLimit();
				int parametherValueChosen = rand.nextInt(limitSuperior-limitInferior) + limitInferior;
				basicFunction=basicFunction+parametherValueChosen+",";
			}
			else
			{
				int idChosen=rand.nextInt(parameter.getDiscreteSpecificValues().size());
				discreteValue=parameter.getDiscreteSpecificValues().get(idChosen);
				basicFunction=basicFunction+discreteValue+",";
			}
		}
		basicFunction=basicFunction.substring(0, basicFunction.length() - 1);
		//basicFunction=basicFunction+") ";
		return basicFunction+")";
	}
	
	public String returnConditionalClean()
	{
		
		String conditional="";
		int limitInferior;
		int limitSuperior;
		String discreteValue;
		//int id=rand.nextInt(ConfigurationsGA.QTD_RULES_BASIC_FUNCTIONS);
		int idconditionalSelected=rand.nextInt(functions.getConditionalsForGrammar().size());
		FunctionsforGrammar functionChosen=functions.getConditionalsForGrammar().get(idconditionalSelected);
		conditional=conditional+functionChosen.getNameFunction()+"(";
		for(Parameter parameter:functionChosen.getParameters())
		{
			if(parameter.getDiscreteSpecificValues()==null)
			{
				
				limitInferior=(int)parameter.getInferiorLimit();
				limitSuperior=(int)parameter.getSuperiorLimit();
				int parametherValueChosen = rand.nextInt(limitSuperior-limitInferior) + limitInferior;
				conditional=conditional+parametherValueChosen+",";
			}
			else
			{
				int idChosen=rand.nextInt(parameter.getDiscreteSpecificValues().size());
				discreteValue=parameter.getDiscreteSpecificValues().get(idChosen);
				conditional=conditional+discreteValue+",";
			}
		}
		conditional=conditional.substring(0, conditional.length() - 1);
		//conditional="if("+conditional+")) ";
		return conditional+")";
	}
	
	//THis method uses a preexistent table of scripts instead of create a new one
	public ScriptsTable generateScriptsTableCurriculumVersion(){
		
		HashMap<String, BigDecimal> newChromosomes = new HashMap<>();
		ChromosomeScript tChom;
        try (BufferedReader br = new BufferedReader(new FileReader(pathTableScripts + "/ScriptsTable.txt"))) {
            String line;            
            while ((line = br.readLine()) != null) {
                String[] strArray = line.split(" ");
                int[] intArray = new int[strArray.length];
                for (int i = 0; i < strArray.length; i++) {
                    intArray[i] = Integer.parseInt(strArray[i]);
                }
                int idScript = intArray[0];
                int[] rules = Arrays.copyOfRange(intArray, 1, intArray.length);

                tChom = new ChromosomeScript();
                for (int i : rules) {
                	tChom.addGene(i);
                }
                newChromosomes.put("", BigDecimal.valueOf(idScript));;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ScriptsTable st = new ScriptsTable(newChromosomes,pathTableScripts);
        //st.print();
		return st;
	}
	
	public int getCurrentSizeTable() {
		return currentSizeTable;
	}

	public void setCurrentSizeTable(int currentSizeTabler) {
		currentSizeTable = currentSizeTabler;
		PrintWriter f0;
		try {
			f0 = new PrintWriter(new FileWriter(pathTableScripts+"SizeTable.txt"));
			f0.println(currentSizeTable);
			f0.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
//	public boolean checkDiversityofTypes() {
//		
//		HashSet<Integer> diferentTypes =  new HashSet<Integer>();
//		for(String c : scriptsTable.keySet()){
//			for (Integer gene : c.getGenes()) {
//				
//				diferentTypes.add(tcg.getCorrespondenceofTypes().get(gene));
//			}
//		}
//		if(diferentTypes.size()==numberOfTypes) {
//			return false;
//		}
//		else {
//			return true;
//		}		
//	}
	
}
