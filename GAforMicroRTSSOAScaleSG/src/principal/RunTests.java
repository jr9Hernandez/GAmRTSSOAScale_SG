package principal;

import java.io.IOException;

import SetCoverSampling.DataRecollection;
import SetCoverSampling.RunSampling;
import ga.config.ConfigurationsGA;
import ga.model.Population;
import ga.util.RunGA;
import ga.util.Evaluation.RatePopulation;
import ga.util.Evaluation.RoundRobinEliteandSampleEval;
import ga.util.Evaluation.RoundRobinEliteandSampleIterativeEval;
import ga.util.Evaluation.RoundRobinEval;
import ga.util.Evaluation.RoundRobinIterativeEval;
import ga.util.Evaluation.RoundRobinSampleEval;
import ga.util.Evaluation.FixedScriptedEval;

public class RunTests {

	public static void main(String[] args) {
		//classe com as configurações = ConfigurationsGA
		
		//teste de inicialização de população
		//Population p = Population.getInitialPopulation(ConfigurationsGA.SIZE_POPULATION);
		//p.print();
		//p.printWithValue();
		//p = Population.getInitialPopulation(new Integer(100));
		//p.print();
		
		//################################################################33
		//sandBOX
		//Population p = Population.getInitialPopulation(ConfigurationsGA.SIZE_POPULATION);
		//p.print();
		//RatePopulation fEval = new RoundRobinEval();
		
		
		//p = fEval.evalPopulation(p,1);
		
		//System.out.println("\n Pos avaliação");
		//p.printWithValue();
		
		//################################################################33
	
		
		for(int i=0;i<ConfigurationsGA.LOOPS_SELFPLAY;i++)
		{
			//Here we play with a search-based algorithm and save the path
			try {
				RunSampling sampling=new RunSampling(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
			
		//* 
		//aplicando o Algoritmo Genético
		//criei uma classe para controlar a execução do GA.
		RunGA ga = new RunGA();
		
		//escolhemos uma função de avaliação
		//RatePopulation fEval = new RoundRobinEval();
		//RatePopulation fEval = new RoundRobinSampleEval();
		//RatePopulation fEval = new RoundRobinEliteandSampleEval();
		//RatePopulation fEval = new RoundRobinIterativeEval();
		RatePopulation fEval = new RoundRobinEliteandSampleIterativeEval();
		
		//rodamos o GA
		Population popFinal = ga.run(fEval);
		
		//popFinal.printWithValue();
		
		//Fase 6 - mostrar os mais aptos na população final
		
		//Fase 7 - finalizar 
		fEval.finishProcess();
		
		}
		
		
		 
	}

}
