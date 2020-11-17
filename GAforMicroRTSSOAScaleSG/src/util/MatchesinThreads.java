package util;

import java.util.ArrayList;
import java.util.HashSet;

import ai.synthesis.DslLeague.Runner.SettingsAlphaDSL;
import ai.synthesis.dslForScriptGenerator.DSLCommandInterfaces.ICommand;
import ai.synthesis.grammar.dslTree.interfacesDSL.iDSL;
import ai.synthesis.grammar.dslTree.utils.ReduceDSLController;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.UnitTypeTable;


public class MatchesinThreads {
	private static BuilderGrammars builder;
	private static boolean typePlayout=false;

	public static void main(String[] args) {
		
		String map = SettingsAlphaDSL.get_map();
		UnitTypeTable utt = new UnitTypeTable();
		PhysicalGameState pgs=new PhysicalGameState(30, 30);
		try {
			pgs = PhysicalGameState.load(map, utt);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GameState gs = new GameState(pgs, utt);
		
		long startTime=System.currentTimeMillis();
		
		for(int i=0;i<50;i++)
		{
			// TODO Auto-generated method stub
			builder = BuilderGrammars.getInstance();
			iDSL iSc1 = builder.buildS1Grammar();
			iDSL iSc2 = builder.buildS1Grammar();

			//Enable this block to run playouts in threads
			if(typePlayout)
			{

				evaluate_thread_playouts(iSc1, iSc2, gs, pgs, utt);
			}
			else
			{
				evaluate_thread_scripts(iSc1, iSc2, gs, pgs, utt);
			}

		}
		long endTime=System.currentTimeMillis();
		long duration =(endTime - startTime);
		System.out.println("Total duration "+duration);
	}

	private static float evaluate_thread_scripts(iDSL script1, iDSL script2, GameState gs, PhysicalGameState pgs, UnitTypeTable utt) {
		//System.out.println("Runnable Simulated Annealing Version");

		TestSingleMatch runner1 = new TestSingleMatch(script1, script2, gs, pgs, utt);
		TestSingleMatch runner2 = new TestSingleMatch(script1, script2, gs, pgs, utt);
		TestSingleMatch runner3 = new TestSingleMatch(script1, script2, gs, pgs, utt);
		TestSingleMatch runner4 = new TestSingleMatch(script1, script2, gs, pgs, utt);




		//		TestSinglePlayout runner1 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		//		TestSinglePlayout runner2 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		//		TestSinglePlayout runner3 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		//		TestSinglePlayout runner4 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		try {


			runner1.start();
			runner2.start();
			runner3.start();
			runner4.start();

			runner1.join();
			runner2.join();
			runner3.join();
			runner4.join();

			float totalScript2 = 0.0f;
			if (runner1.getWinner() == 1) {
				totalScript2 += runner1.getResult();
			} else if (runner1.getWinner() == -1) {
				totalScript2 += runner1.getResult();
			}
			if (runner2.getWinner() == 1) {
				totalScript2 += runner2.getResult();
			} else if (runner2.getWinner() == -1) {
				totalScript2 += runner2.getResult();
			}

			if (runner3.getWinner() == 0) {
				totalScript2 += runner3.getResult();
			} else if (runner3.getWinner() == -1) {
				totalScript2 += runner3.getResult();
			}
			if (runner4.getWinner() == 0) {
				totalScript2 += runner4.getResult();
			} else if (runner4.getWinner() == -1) {
				totalScript2 += runner4.getResult();
			}

			HashSet<ICommand> uniqueCommands = new HashSet<>();
			uniqueCommands.addAll(runner1.getAllCommandIA2());
			uniqueCommands.addAll(runner2.getAllCommandIA2());
			uniqueCommands.addAll(runner3.getAllCommandIA1());
			uniqueCommands.addAll(runner4.getAllCommandIA1());
			ReduceDSLController.removeUnactivatedParts(script2, new ArrayList<>(uniqueCommands));
			System.out.println("score second script "+totalScript2);
			return totalScript2;
		} catch (Exception e) {
			System.err.println("ai.synthesis.localsearch.DoubleProgramSynthesis.processMatch() " + e.getMessage());
			return -5.0f;
		}
	}

	private static float evaluate_thread_playouts(iDSL script1, iDSL script2, GameState gs, PhysicalGameState pgs, UnitTypeTable utt) {
		//System.out.println("Runnable Simulated Annealing Version");


		TestSinglePlayout runner1 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		TestSinglePlayout runner2 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		TestSinglePlayout runner3 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		TestSinglePlayout runner4 = new TestSinglePlayout(script1, script2, gs, pgs, utt);
		try {


			runner1.start();
			runner2.start();
			runner3.start();
			runner4.start();

			runner1.join();
			runner2.join();
			runner3.join();
			runner4.join();

			float totalScript2 = 0.0f;
			if (runner1.getWinner() == 1) {
				totalScript2 += runner1.getResult();
			} else if (runner1.getWinner() == -1) {
				totalScript2 += runner1.getResult();
			}
			if (runner2.getWinner() == 1) {
				totalScript2 += runner2.getResult();
			} else if (runner2.getWinner() == -1) {
				totalScript2 += runner2.getResult();
			}

			if (runner3.getWinner() == 0) {
				totalScript2 += runner3.getResult();
			} else if (runner3.getWinner() == -1) {
				totalScript2 += runner3.getResult();
			}
			if (runner4.getWinner() == 0) {
				totalScript2 += runner4.getResult();
			} else if (runner4.getWinner() == -1) {
				totalScript2 += runner4.getResult();
			}

			HashSet<ICommand> uniqueCommands = new HashSet<>();
			uniqueCommands.addAll(runner1.getAllCommandIA2());
			uniqueCommands.addAll(runner2.getAllCommandIA2());
			uniqueCommands.addAll(runner3.getAllCommandIA1());
			uniqueCommands.addAll(runner4.getAllCommandIA1());
			ReduceDSLController.removeUnactivatedParts(script2, new ArrayList<>(uniqueCommands));
			System.out.println("score second script "+totalScript2);
			return totalScript2;
		} catch (Exception e) {
			System.err.println("ai.synthesis.localsearch.DoubleProgramSynthesis.processMatch() " + e.getMessage());
			return -5.0f;
		}
	}
}
