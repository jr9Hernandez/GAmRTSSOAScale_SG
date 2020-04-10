package setcoverCalculation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ga.ScriptTableGenerator.ScriptsTable;

public class RunSetCoverCalculation {
	private final static String pathTableScripts = System.getProperty("user.dir").concat("/Table/");
	List<Integer>  setCover;
	
	public RunSetCoverCalculation(HashMap<String, List<Integer>> data) {
		
		ScriptsTable st=new ScriptsTable(pathTableScripts);
		ArrayList<String> basicFunctions= st.allBasicFunctions();
		Data objData=new Data(basicFunctions.size());
//		
//		HashMap<String, List<Integer>> data=new HashMap<String, List<Integer>>();
//		try {
//			data=objData.loadDataFromSampling();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		objData.printDataMap(data);
//		System.out.println(data.size());
		DataFormatting sc=new DataFormatting(data,basicFunctions.size());
		sc.fillMatrix();
		//sc.printMatrix();		
		
		/*File[] files = new File("samplings").listFiles();		
		List<List<String>> data=objData.loadDataFromSampling(files);
		objData.printDataList(data);*/
		setCover=new ArrayList<Integer>();
		HillClimbing hc=new HillClimbing(sc.matrixCovering, sc.idsActions, basicFunctions.size());
		setCover=hc.doHillCLimbing();
//		System.out.println("All commands");
//		
//		for(String basicFunction: basicFunctions)
//		{
//			System.out.println(basicFunction);
//		}
		System.out.println(setCover);

	}

	/**
	 * @return the setCover
	 */
	public List<Integer> getSetCover() {
		return setCover;
	}
	
	public List<Integer> getRandomSetCoverOfSameSize() {
		
		List<Integer>  setCoverRandom=new ArrayList();
		ScriptsTable st=new ScriptsTable(pathTableScripts);
		ArrayList<String> OriginalAllbasicFunctions= st.allBasicFunctions();
		
		System.out.println("All basic functions size"+OriginalAllbasicFunctions.size());

		System.out.println("original setcover scripts size "+setCover.size());
		System.out.println("original setcover scripts "+setCover);

		Random rnd=new Random();
		
		for(int i=0; i<setCover.size();i++)
		{
			setCoverRandom.add(rnd.nextInt(OriginalAllbasicFunctions.size()));
		}
		
		System.out.println("New Random setcover scripts size "+setCoverRandom.size());
		System.out.println("New Random setcover scripts "+setCoverRandom);
		
		return setCoverRandom;
	}

	/**
	 * @param setCover the setCover to set
	 */
	public void setSetCover(List<Integer> setCover) {
		this.setCover = setCover;
	}

}
