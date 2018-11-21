package ga.ScriptTableGenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import ai.ScriptsGenerator.TableGenerator.TableCommandsGenerator;
import ga.config.ConfigurationsGA;
import rts.units.UnitTypeTable;


public class ScriptsTable {
	
	static Random rand = new Random();
	private int currentSizeTable;

	/**
	 * @return the currentSizeTable
	 */


	private HashMap<ChromosomeScript, BigDecimal> scriptsTable ;
	private int numberOfTypes;

	private String pathTableScripts;
	
	public ScriptsTable(String pathTableScripts){
		this.scriptsTable = new HashMap<>();
		this.pathTableScripts=pathTableScripts;
	}
	

	public ScriptsTable(HashMap<ChromosomeScript, BigDecimal> scriptsTable,String pathTableScripts) {
		super();
		this.scriptsTable = scriptsTable;
		this.pathTableScripts=pathTableScripts;
	}



	public HashMap<ChromosomeScript, BigDecimal> getScriptTable() {
		return scriptsTable;
	}


	public void addScript(ChromosomeScript chromosomeScript){
		this.scriptsTable.put(chromosomeScript, BigDecimal.ZERO);
	}	
	
	public void print(){
		System.out.println("-- Table Scripts --");
		for(ChromosomeScript c : scriptsTable.keySet()){
			c.print();
		}
		System.out.println("-- Table Scripts --");
	}
	
	public void printWithValue(){
		System.out.println("-- Table Script --");
		for(ChromosomeScript c : scriptsTable.keySet()){
			c.print();
			System.out.println("Value = "+ this.scriptsTable.get(c));
		}
		System.out.println("-- Table Scripts --");
	}

	
	//static methods
	
	public ScriptsTable generateScriptsTable(int size){
		
		TableCommandsGenerator tcg=TableCommandsGenerator.getInstance(new UnitTypeTable());
		numberOfTypes=tcg.getNumberTypes();
		
		HashMap<ChromosomeScript, BigDecimal> newChromosomes = new HashMap<>();
		ChromosomeScript tChom;
		PrintWriter f0;
		try {
			f0 = new PrintWriter(new FileWriter(pathTableScripts+"ScriptsTable.txt"));
			
			for(int i=0;i<size;i++)
			{
				tChom = new ChromosomeScript();
				int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME_SCRIPT)+1;
				for (int j = 0; j < sizeCh; j++) {
					int typeSelected=rand.nextInt(numberOfTypes);
					int sizeRulesofType=tcg.getBagofTypes().get(typeSelected).size();
					int idRuleSelected=tcg.getBagofTypes().get(typeSelected).get(rand.nextInt(sizeRulesofType));
					tChom.addGene(rand.nextInt(ConfigurationsGA.QTD_RULES));
				}
				newChromosomes.put(tChom, BigDecimal.valueOf(i));
			    f0.println(i+tChom.print());
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
	
	public boolean checkDiversityofTypes() {
		HashSet diferentTypes =  new HashSet<Integer>();
		for(ChromosomeScript c : scriptsTable.keySet()){
			diferentTypes.add(this.scriptsTable.get(c));
		}
		if(diferentTypes.size()==numberOfTypes) {
			return false;
		}
		else {
			return true;
		}
	}
	
}
