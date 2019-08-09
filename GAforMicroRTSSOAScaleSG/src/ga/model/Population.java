package ga.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ga.ScriptTableGenerator.ScriptsTable;
import ga.config.ConfigurationsGA;

public class Population {
	
	static Random rand = new Random();
	private Map<Integer,List<String>> allCommandsperGeneration;
	private Map<Integer,List<String>> usedCommandsperGeneration;
	private HashMap<BigDecimal, String> scriptsAlternativeTable;
	private String pathTableScripts;
	
	
	
	
	/**
	 * A população será representada como um Map visando podermos armazenar como Value 
	 * o valor dado pela avaliação a cada cromossomo da população e como Key o Cromossomo.
	 */
	private HashMap<Chromosome, BigDecimal> Chromosomes ;

	
	
	public Population(){
		this.Chromosomes = new HashMap<>();
	}
	

	public Population(HashMap<Chromosome, BigDecimal> chromosomes) {
		super();
		Chromosomes = chromosomes;
		allCommandsperGeneration= new HashMap<Integer,List<String>>();
		usedCommandsperGeneration=new HashMap<Integer,List<String>>();
	}



	public HashMap<Chromosome, BigDecimal> getChromosomes() {
		return Chromosomes;
	}

	public void setChromosomes(HashMap<Chromosome, BigDecimal> chromosomes) {
		Chromosomes = chromosomes;
	}
	
	public void addChromosome(Chromosome chromosome){
		this.Chromosomes.put(chromosome, BigDecimal.ZERO);
	}	
	
//	public void print(){
//		System.out.println("-- Population --");
//		for(Chromosome c : Chromosomes.keySet()){
//			c.print();
//		}
//		System.out.println("-- Population --");
//	}
	
	public void printWithValue(PrintWriter f0){
		System.out.println("-- Population --");
		f0.println("-- Population --");
		for(Chromosome c : Chromosomes.keySet()){
			c.print(f0);
			System.out.println("Value = "+ this.Chromosomes.get(c));
			f0.println("Value = "+ this.Chromosomes.get(c));
		}
		System.out.println("-- Population --");
		f0.println("-- Population --");
	}
	
	/**
	 * Função que zera os valores das avaliações dos Chromossomos.
	 */
	public void clearValueChromosomes(){
		for(Chromosome chromo : this.Chromosomes.keySet()){
			this.Chromosomes.put(chromo, BigDecimal.ZERO);
		}
	}
	
	//static methods
	
	/**
	 * Cria uma população inicial gerada randomicamente.
	 * @param size Tamanho limite da população
	 * @return uma população com Key = Chromosome e Values = 0
	 */
	public static Population getInitialPopulation(int size, ScriptsTable scrTable){
		HashMap<Chromosome, BigDecimal> newChromosomes = new HashMap<>();
		
		Chromosome tChom;
		for (int i = 0; i < size; i++) {
			//gerar o novo cromossomo com base no tamanho
			tChom = new Chromosome();
			int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
			for (int j = 0; j < sizeCh; j++) {
				tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
			}
			newChromosomes.put(tChom, BigDecimal.ZERO);
		}
		Population pop = new Population(newChromosomes);
		return pop;
	}
	
	public static Population getInitialPopulationCurriculum(int size, ScriptsTable scrTable, String pathInitialPopulation){
		HashMap<Chromosome, BigDecimal> newChromosomes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathInitialPopulation + "/population.txt"))) {
            String line;
            Chromosome tChom;
            while ((line = br.readLine()) != null) {
            	if(line.startsWith("Value"))
            	{
            		continue;
            	}
                String[] strArray = line.split(" ");
                int[] intArray = new int[strArray.length-1];
                for (int i = 0; i < strArray.length-1; i++) {
                    intArray[i] = Integer.parseInt(strArray[i+1]);
                }
                //int[] idsScripts = Arrays.copyOfRange(intArray, 0, intArray.length);

                tChom = new Chromosome();
                for (int i : intArray) {
                	tChom.addGene(i);
                }
                
                newChromosomes.put(tChom, BigDecimal.ZERO);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Population pop = new Population(newChromosomes);
        return pop;
	}
	
	
	/**
	 * Cria uma população inicial com os genes dos cromossomos iguais ao passado por parametros
	 * @param gene Integer que será utilizado como gene dos cromossomos
	 * @return uma população com Key = Chromosome e Values = 0
	 */
	public static Population getInitialPopulation(Integer gene){
		HashMap<Chromosome, BigDecimal> newChromosomes = new HashMap<>();
		
		Chromosome tChom;
		for (int i = 0; i < ConfigurationsGA.SIZE_POPULATION; i++) {
			//gerar o novo cromossomo com base no tamanho
			tChom = new Chromosome();
			for (int j = 0; j < ConfigurationsGA.SIZE_CHROMOSOME; j++) {
				tChom.addGene(gene);
			}
			newChromosomes.put(tChom, BigDecimal.ZERO);
		}
		
		Population pop = new Population(newChromosomes);
		return pop;
	}
	
	public boolean isPopulationValueZero(){
		
		for (BigDecimal value : Chromosomes.values()) {
			if(value.compareTo(BigDecimal.ZERO) == 1 ){
				return false;
			}
		}
		return true;
	}
	
	public void fillAllCommands(String pathscrTable)
	{
		allCommandsperGeneration.clear();
		this.pathTableScripts=pathscrTable;
		buildScriptsAlternativeTable();
	    Iterator it = Chromosomes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int id=((BigDecimal)pair.getValue()).intValue();
	        ArrayList<Integer> scriptsId= ((Chromosome)pair.getKey()).getGenes();
	        String completeGrammars;
	        for(Integer idScript:scriptsId) 
	        {
	        	//System.out.println(scriptsAlternativeTable);
	        	completeGrammars=scriptsAlternativeTable.get(BigDecimal.valueOf(idScript));
	        	getCommandsFromFullScript(idScript,completeGrammars);
	        }
	        
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	public void getCommandsFromFullScript(int id,String script)
	{
		String[] splited = script.split("\\s+");
		for(String s : splited)
		{
			if(!s.contains("for") && !s.contains("if") && s.length()>0)
			{
				if(s.charAt(0) =='(')
				{
					s=s.replaceFirst("\\(", "");
				}
				while(s.charAt(s.length()-1)==')' && s.charAt(s.length()-2)==')')
				{
					s=s.substring(0, s.length() - 1);
				}
				
			
			if(allCommandsperGeneration.containsKey(id))
			{
				List<String> allCommandsStored=allCommandsperGeneration.get(id);
				allCommandsStored.add(s);
				allCommandsperGeneration.put(id, allCommandsStored);
			}
			else
			{	List<String> allCommandsStored=new ArrayList<String>();
				allCommandsStored.add(s);
				allCommandsperGeneration.put(id, allCommandsStored);
			}
		}
			
		}
		
	}


	/**
	 * @return the allCommandsperGeneration
	 */
	public Map<Integer,List<String>> getAllCommandsperGeneration() {
		return allCommandsperGeneration;
	}


	/**
	 * @param allCommandsperGeneration the allCommandsperGeneration to set
	 */
	public void setAllCommandsperGeneration(Map<Integer,List<String>> allCommandsperGeneration) {
		this.allCommandsperGeneration = allCommandsperGeneration;
	}
	
    public void buildScriptsAlternativeTable() {
        scriptsAlternativeTable = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathTableScripts + "/ScriptsTable.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
            	//System.out.println(line);
                String code = line.substring(line.indexOf(" "), line.length());
                String[] strArray = line.split(" ");
                int idScript = Integer.decode(strArray[0]);
                scriptsAlternativeTable.put(BigDecimal.valueOf(idScript), code);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


	public void chooseusedCommands(String pathUsedCommands) {
		// TODO Auto-generated method stub
		
		readUsedCommands(pathUsedCommands);
	}
	
	public void removeCommands(ScriptsTable scrTable) {
		// TODO Auto-generated method stub
		
	    Iterator it = getUsedCommandsperGeneration().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int id=(Integer)pair.getKey();
	        
	        List<String> commandsUsed= (List<String>) pair.getValue();
	        
	        if(getAllCommandsperGeneration().get(id)!=null)
	        {
	        	List<String> commandsAll=getAllCommandsperGeneration().get(id);
		        commandsAll.removeAll(commandsUsed);
		        changeGrammars(scrTable);
	        }
	        
	        
	        
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public void changeGrammars(ScriptsTable scrTable)
	{

	    Iterator it = Chromosomes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int id=((BigDecimal)pair.getValue()).intValue();
	        ArrayList<Integer> scriptsId= ((Chromosome)pair.getKey()).getGenes();
	        String originalcompleteGrammars;
	        for(int i=0;i<scriptsId.size();i++) 
	        {
	        	//System.out.println(scriptsAlternativeTable);
	        	originalcompleteGrammars=scriptsAlternativeTable.get(BigDecimal.valueOf(scriptsId.get(i)));
	        	String newGrammar=replaceCommandsinGrammar(originalcompleteGrammars,scriptsId.get(i));
	        	
	        	if(!originalcompleteGrammars.equals(newGrammar))
	        	{
	    			int newId=scrTable.getScriptTable().size();
	    			scrTable.getScriptTable().put(newGrammar, BigDecimal.valueOf(newId));
	    			scrTable.setCurrentSizeTable(scrTable.getScriptTable().size());
	    			addLineFile(newId+" "+newGrammar);
	    			scriptsId.set(i, newId);
	        	}

	        }
	        
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public void addLineFile(String data) {
	    try{    

	        File file =new File(pathTableScripts+"ScriptsTable.txt");    

	        //if file doesnt exists, then create it    
	        if(!file.exists()){    
	            file.createNewFile();      
	        }    

	        //true = append file    
	            FileWriter fileWritter = new FileWriter(file,true);        
	            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	            bufferWritter.write(data);
	            bufferWritter.newLine();
	            bufferWritter.close();
	            fileWritter.close();  

	    }catch(Exception e){    
	        e.printStackTrace();    
	    } 
		}

	
	public String replaceCommandsinGrammar(String originalcompleteGrammars,int id)
	{
		String newGrammar=originalcompleteGrammars;
		for(String command:allCommandsperGeneration.get(id))
		{
			if(newGrammar.contains(command))
			{
				newGrammar.replaceAll(command, "");
			}
		}
		return newGrammar;
	}
	
	public void readUsedCommands(String pathUsedCommands)
	{
		usedCommandsperGeneration.clear();
		List <String> usedCommands;
		
		File COMMFolder = new File(pathUsedCommands);
		if (COMMFolder != null) {
			
			for (File folder : COMMFolder.listFiles()) {
				try (BufferedReader br = new BufferedReader(new FileReader(folder+"\\logsGrammars.txt"))) {
				    String line;
				    while ((line = br.readLine()) != null) {
				    	String parts[]=line.split(" ");
				    	
				    	if(usedCommandsperGeneration.containsKey(Integer.valueOf(parts[0])))
				    	{
				    		usedCommands=usedCommandsperGeneration.get(Integer.valueOf(parts[0]));
				    	}
				    	else
				    	{
				    		usedCommands=new ArrayList<String>();
				    		usedCommandsperGeneration.put(Integer.valueOf(parts[0]), usedCommands);
				    	}
				    	for(int i=1; i<parts.length;i++)
				    	{
				    		if(!usedCommands.contains(cleaned(parts[i])))
				    			usedCommands.add(cleaned(parts[i]));
				    	}				    	
				       
				    }
				    //folder.delete();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		
		

			
	}


	private String cleaned(String command) {
		
		if(command.charAt(0) =='(')
		{
			command=command.replaceFirst("\\(", "");
		}
		
		while(command.charAt(command.length()-1)==')' && command.charAt(command.length()-2)==')')
		{
			command=command.substring(0, command.length() - 1);
		}
		return command;
	}


	/**
	 * @return the usedCommandsperGeneration
	 */
	public Map<Integer, List<String>> getUsedCommandsperGeneration() {
		return usedCommandsperGeneration;
	}


	/**
	 * @param usedCommandsperGeneration the usedCommandsperGeneration to set
	 */
	public void setUsedCommandsperGeneration(Map<Integer, List<String>> usedCommandsperGeneration) {
		this.usedCommandsperGeneration = usedCommandsperGeneration;
	}
}
