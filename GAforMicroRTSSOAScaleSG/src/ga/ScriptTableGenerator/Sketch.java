package ga.ScriptTableGenerator;

import java.util.Random;

import ai.ScriptsGenerator.TableGenerator.FunctionsforGrammar;
import ga.config.ConfigurationsGA;

public class Sketch {
	static Random rand = new Random();
	private FunctionsforGrammar functions;
	ScriptsTable st=new ScriptsTable();
	public Sketch()
	{
		functions=new FunctionsforGrammar();
	}
	public String sketchA(String genotypeScript,int numberComponentsAdded) {
		//basic function
		if(rand.nextInt(2)>0 || numberComponentsAdded>=ConfigurationsGA.MAX_QTD_COMPONENTS)
		{
			genotypeScript=genotypeScript+st.returnBasicFunction(false);

		}
		else
		{
			genotypeScript=genotypeScript+st.returnBasicFunction(false);
			genotypeScript=sketchA(genotypeScript,numberComponentsAdded+1);
		}
		return genotypeScript;
	}
}
