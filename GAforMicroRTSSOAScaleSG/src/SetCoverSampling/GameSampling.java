/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SetCoverSampling;

import ai.RandomBiasedAI;
import ai.ScriptsGenerator.ChromosomeAI;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.GPCompiler.FunctionGPCompiler;
import ai.ScriptsGenerator.GPCompiler.ICompiler;
import ai.ScriptsGenerator.GPCompiler.MainGPCompiler;
import ai.asymmetric.PGS.*;
import ai.competition.capivara.CmabAssymetricMCTS;
import ai.core.AI;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import gui.PhysicalGameStatePanel;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;

import PVAI.WorkerDefense;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;


public class GameSampling {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;
    UnitTypeTable utt;
    PhysicalGameState pgs;
    int MAXCYCLES;
    int PERIOD;
    static File prod;
    static File move ;
    static File attack;
    static File other;
    static int id;
    
    private HashMap<BigDecimal, String> scriptsTable;
    HashSet<String> usedCommands;
    ICompiler compiler = new MainGPCompiler();
    
    public GameSampling(String pathTableScripts)
    {
    	utt = new UnitTypeTable();
        MAXCYCLES = 8000;
        PERIOD = 20;
        buildScriptsTable(pathTableScripts);
    }

    public void run(String portfolioPlayer1, String portfolioPlayer2, String pathLog) throws Exception {
    	id = 0;
    	//controle de tempo
        Instant timeInicial = Instant.now();
        Duration duracao;

        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        portfolioPlayer1 = portfolioPlayer1.replaceAll("\\s+","");
        System.out.println("port1 "+portfolioPlayer1);
        String[] itens = portfolioPlayer1.replace("[", "").replace("]", "").split(",");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }

        ArrayList<Integer> iScriptsAi2 = new ArrayList<>();
        portfolioPlayer2 = portfolioPlayer2.replaceAll("\\s+","");
        System.out.println("port2 "+portfolioPlayer2);
        itens = portfolioPlayer2.replace("[", "").replace("]", "").split(",");

        for (String element : itens) {
            iScriptsAi2.add(Integer.decode(element));
        }
        
		pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        //pgs = PhysicalGameState.load("maps/32x32/basesWorkers32x32A.xml", utt);
        //pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        //pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);  

        GameState gs = new GameState(pgs, utt);
        boolean gameover = false;

        List<AI> scriptsRun1=decodeScripts(utt, iScriptsAi1);
        List<AI> scriptsRun2=decodeScripts(utt, iScriptsAi2);
        
        //System.out.println("idscriptleader "+idScriptLeader);
        //System.out.println("idscripEnemy "+idScriptEnemy);
        //AI ai1 = new PGSSCriptChoice(utt, decodeScripts(utt,portfolioPlayer1), "--");
        //AI ai2 = new PGSSCriptChoice(utt, decodeScripts(utt, portfolioPlayer2), "--");
//        AI ai1= new PGSSCriptChoiceRandom(utt, decodeScripts(utt, portfolioPlayer1), "PGSR", 2, 200);
//        AI ai2= new PGSSCriptChoiceRandom(utt, decodeScripts(utt, portfolioPlayer2), "PGSR", 2, 200);
        //AI ai1 = new PGSSCriptChoice(utt, decodeScripts(utt, String.valueOf(idScriptLeader).concat(";")), "--");
        //AI ai2 = new PGSSCriptChoice(utt, decodeScripts(utt, String.valueOf(idScriptEnemy).concat(";")), "--");
        AI ai2 = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f,
                0.0f, 0.4f, 0, new RandomBiasedAI(utt),
                new SimpleSqrtEvaluationFunction3(), true, utt,
                "ManagerClosestEnemy", 3, scriptsRun1);
        
        AI ai1 = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f,
                0.0f, 0.4f, 0, new RandomBiasedAI(utt),
                new SimpleSqrtEvaluationFunction3(), true, utt,
                "ManagerClosestEnemy", 3, scriptsRun2);

        
        System.out.println("---------AI's---------");
        System.out.println("AI 1 = "+ai1.toString());
        System.out.println("AI 2 = "+ai2.toString()+"\n");        
        
        
        JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);;

        //File dir = new File("logs_states/log_"+idScriptLeader+"_"+idScriptEnemy+"_"+idSampling);
        String dirPathPlayer0="logs_game/logs_states_"+pathLog+"/log_"+portfolioPlayer1+"_"+portfolioPlayer2+"/player0";
        String dirPathPlayer1="logs_game/logs_states_"+pathLog+"/log_"+portfolioPlayer1+"_"+portfolioPlayer2+"/player1";
        File dirPlayer0 = new File(dirPathPlayer0);
        File dirPlayer1 = new File(dirPathPlayer1);
        dirPlayer0.mkdirs();
        dirPlayer1.mkdirs();
        //create subdiretories 
        //createSubDirs(dir);
        
        
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        int idState=0;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
            	
            	//File subDir = new File("logs_states/log_"+sampleCounter+"_"+idScriptLeader+"_"+idScriptEnemy+"/"+"state_"+gs.getTime());
            	//subDir.mkdir();
                /*
            	if (gs.canExecuteAnyAction(0) ) {
                //alcançamos o estado que desejamos salvar....
        
            		Writer writer = new FileWriter("logs/logs_states_"+pathLog+"/log"+"_"+idScriptLeader+"_"+idScriptEnemy+"/"+"state_"+id+".txt");
            		gs.toJSON(writer); //salva JSon contendo todo o estado no tempo x que escolhido
            		writer.flush();
            		writer.close();
            		id++;
            	}
                */
                PlayerAction pa1 = ai1.getAction(0, gs);  
                //System.out.println("Tempo de execução P1="+(startTime = System.currentTimeMillis() - startTime));
                //System.out.println("Action A1 ="+ pa1.toString());
                
                PlayerAction pa2 = ai2.getAction(1, gs);
                //System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                //System.out.println("Action A2 ="+ pa2.toString());
                
//                if (gs.canExecuteAnyAction(0) && gs.canExecuteAnyAction(1)) {
//                	//verify what kind of action is and save the state in your specified folder
//                	saveStateAll(gs, pa1,dirPathPlayer0,dirPathPlayer1);
//                	//saveStateByType(gs, pa2);
//                }
                
                if (gs.canExecuteAnyAction(0)) {
                	//verify what kind of action is and save the state in your specified folder
                	saveState(gs, pa1,dirPathPlayer0,idState);
                	//saveStateByType(gs, pa2);
                }
                if (gs.canExecuteAnyAction(1)) {
                	//verify what kind of action is and save the state in your specified folder
                	//saveStateByType(gs, pa1);
                	saveState(gs, pa2,dirPathPlayer1,idState);
                }
                
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);
                
                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate += PERIOD;
                idState++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           /* PhysicalGameState physical = gs.getPhysicalGameState();
            System.out.println("---------TIME---------");
            System.out.println(gs.getTime());
            for (Unit u : physical.getUnits()) {
                if (u.getPlayer() == 1) {
                    System.out.println("Player 1: Unity - " + u.toString());
                }
                else if (u.getPlayer() == 0) {
                     System.out.println("Player 0: Unity - " + u.toString());
                } 
            }
            */
          //avaliacao de tempo
            duracao = Duration.between(timeInicial, Instant.now());
        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 20));

        System.out.println("Game Over");
    }

    
    public static void saveState(GameState gs_save, PlayerAction pAction,String path,int id) throws Exception{
    	boolean typeState = getAllNones(pAction);
    	if(typeState){
    		return;
    	}
    	Writer writer;
    	writer = new FileWriter(path+"/"+"state_"+id+".txt");
    	gs_save.toJSON(writer); 
    	
    	writer.flush();
		writer.close();
    }    
    /**
     * This function will save the state by type, using the PlayerAction to define what kind of state is it.
     * @param gs_save
     * @param pAction
     * @throws Exception 
     */
    
    public static void saveStateByType(GameState gs_save, PlayerAction pAction) throws Exception{
    	int typeState = getTypePlayerAction(pAction);
    	if(typeState == 4){
    		return;
    	}
    	Writer writer;
    	
    	if(typeState == 0){
    		writer = new FileWriter(prod.getAbsolutePath()+"/"+"state_"+id+".txt");
    		gs_save.toJSON(writer); 
    	}else if(typeState == 1){
    		writer = new FileWriter(move.getAbsolutePath()+"/"+"state_"+id+".txt");
    		gs_save.toJSON(writer); 
    	} else if(typeState == 2){
    		writer = new FileWriter(attack.getAbsolutePath()+"/"+"state_"+id+".txt");
    		gs_save.toJSON(writer); 
    	}else{
    		writer = new FileWriter(other.getAbsolutePath()+"/"+"state_"+id+".txt");
    		gs_save.toJSON(writer); 
    	}
    	
    	writer.flush();
		writer.close();
		id++;
    }
    /**
     * Classify what type of action was sent with parameter
     * @param pAction
     * @return 0=produce, 1=move, 2= attack, 3 =other and 4 for wait (playerAction Just with wait). 
     */
    public static int getTypePlayerAction(PlayerAction pAction){
    	int totalActions = pAction.getActions().size();
    	int cont = 0;
    	for (Pair<Unit, UnitAction> act : pAction.getActions()) {
    		if(act.m_b.getType() == UnitAction.TYPE_NONE){
    			cont++;
    		}
    	}
    	if(cont==totalActions){
    		return 4;
    	}
    	
    	boolean hasAttack = false;
    	boolean hasMove = false;
    	for (Pair<Unit, UnitAction> act : pAction.getActions()) {
			if(act.m_b.getType() == UnitAction.TYPE_PRODUCE){
				return 0;
			}else if(act.m_b.getType() == UnitAction.TYPE_ATTACK_LOCATION){
				hasAttack=true;
			}else if(act.m_b.getType() == UnitAction.TYPE_MOVE){
				hasMove=true;
			}
		}
    	if(hasAttack){
    		return 2;
    	}
    	if(hasMove){
    		return 1;
    	}
    	
    	return 3;
    }
    
    public static boolean getAllNones(PlayerAction pAction){
    	int totalActions = pAction.getActions().size();
    	int cont = 0;
    	for (Pair<Unit, UnitAction> act : pAction.getActions()) {
    		if(act.m_b.getType() == UnitAction.TYPE_NONE){
    			cont++;
    		}
    	}
    	if(cont==totalActions){
    		return true;
    	}
    	return false;
    }
    
    public static void createSubDirs(File parent){
    	String path = parent.getAbsolutePath();
    	//create produce diretory
    	prod = new File(path+"/produce");
    	prod.mkdirs();
    	//create move diretory
    	move = new File(path+"/move");
    	move.mkdirs();
    	//create Attack diretory
    	attack = new File(path+"/attack");
    	attack.mkdirs();
    	//create other diretory
    	other = new File(path+"/other");
    	other.mkdirs();
    }
    
    public PlayerAction generateActionbyScript(GameState g, int scriptSampling, int player) 
    {
    	//AI ai1 = new PGSSCriptChoice(utt, decodeScripts(utt, String.valueOf(scriptSampling).concat(";")), "--");
    	AI ai1=new WorkerDefense(utt);
        PlayerAction pa=null;
		try {
			pa = ai1.getAction(player, g);
			//pa1.getActions().toString();
			//System.out.println(pa1.getActions());
			//System.out.println("Action A1 ="+ pa1.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
        return pa;
    }
    

    
    public List<AI> decodeScripts(UnitTypeTable utt, ArrayList<Integer> iScripts) {
        List<AI> scriptsAI = new ArrayList<>();

        for (Integer idSc : iScripts) {
            //System.out.println("tam tab"+scriptsTable.size());
            //System.out.println("id "+idSc+" Elems "+scriptsTable.get(BigDecimal.valueOf(idSc)));
            scriptsAI.add(buildCommandsIA(utt, scriptsTable.get(BigDecimal.valueOf(idSc))));
        }

        return scriptsAI;
    }

    public HashMap<BigDecimal, String> buildScriptsTable(String pathTableScripts) {
        scriptsTable = new HashMap<>();
        String line="";
        try (BufferedReader br = new BufferedReader(new FileReader(pathTableScripts + "/ScriptsTable.txt"))) {
            while ((line = br.readLine()) != null) {
                String code = line.substring(line.indexOf(" "), line.length());
                String[] strArray = line.split(" ");
                int idScript = Integer.decode(strArray[0]);
                scriptsTable.put(BigDecimal.valueOf(idScript), code);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block            
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(Exception e){
            System.out.println(line);
            System.out.println(e);
        }

        return scriptsTable;
    }
    
    private AI buildCommandsIA(UnitTypeTable utt, String code) {
    	usedCommands=new HashSet<String> ();
    	FunctionGPCompiler.counterCommands=0;
        List<ICommand> commandsGP = compiler.CompilerCode(code, utt);
        AI aiscript = new ChromosomeAI(utt, commandsGP, "P1", code, usedCommands);
        return aiscript;
    }

}

