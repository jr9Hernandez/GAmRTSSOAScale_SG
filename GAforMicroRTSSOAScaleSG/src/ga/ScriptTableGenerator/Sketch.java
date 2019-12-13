package ga.ScriptTableGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ai.ScriptsGenerator.TableGenerator.FunctionsforGrammar;
import ga.config.ConfigurationsGA;

public class Sketch {
	static Random rand = new Random();
	ScriptsTable st=new ScriptsTable();
	String portfolioFromSetCover="";
	ArrayList<String> allBasicFunctions;
	ArrayList<String> allBasicFunctionsRedefined;
	int maxComponents;
	
	public Sketch()
	{
		allBasicFunctions=st.allBasicFunctions();
		allBasicFunctionsRedefined=st.allBasicFunctions();
		maxComponents=ConfigurationsGA.MAX_QTD_COMPONENTS;
//		System.out.println(Arrays.toString(allBasicFunctions.toArray()));
//		System.out.println(Arrays.toString(allBasicFunctionsRedefined.toArray()));
	}
	public 	Sketch(String portfolioFromSetCover)
	{
		this.portfolioFromSetCover=portfolioFromSetCover;
		allBasicFunctions=st.allBasicFunctions();
		allBasicFunctionsRedefined=redefiningCommandsForScripts();
		System.out.println(Arrays.toString(allBasicFunctions.toArray()));
		System.out.println(Arrays.toString(allBasicFunctionsRedefined.toArray()));
		maxComponents=ConfigurationsGA.MAX_QTD_COMPONENTS;
	}
	public String sketchA(String genotypeScript,int numberComponentsAdded) {
		

		//basic function
		if(rand.nextInt(2)>0 || numberComponentsAdded>=maxComponents-1)
		{
			genotypeScript=genotypeScript+" "+returnBasicFunction();

		}
		else
		{
			genotypeScript=genotypeScript+" "+returnBasicFunction();
			genotypeScript=sketchA(genotypeScript,numberComponentsAdded+1);
		}
		return genotypeScript;
	}
	
	public ArrayList<String> redefiningCommandsForScripts()
	{
		ArrayList<String> commandsRedefined=new ArrayList<>();
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        portfolioFromSetCover = portfolioFromSetCover.replaceAll("\\s+","");
        //System.out.println("port1 "+portfolioFromSetCover);
        String[] itens = portfolioFromSetCover.replace("[", "").replace("]", "").split(",");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }
        
        for (Integer idSc : iScriptsAi1) {
            //System.out.println("tam tab"+scriptsTable.size());
            //System.out.println("id "+idSc+" Elems "+scriptsTable.get(BigDecimal.valueOf(idSc)));
        	commandsRedefined.add(allBasicFunctions.get(idSc));
        }
        return commandsRedefined;
	}
	
	public String returnBasicFunction()
	{
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(allBasicFunctionsRedefined.size());
        String item = allBasicFunctionsRedefined.get(index);
        return item;
	}
}