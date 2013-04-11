import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import edu.stanford.nlp.parser.lexparser.TrainOptions;

public class Driver {

	/**
	 * 		1. Merge groups and see
	 * 		2. Sort for longest and then do same stuff
	 * 		3. Normalization?
	 * 		4. Sentiment score for each word - based hacks?!
	 * 		5. Distribute NP over others
	 */
	
	/*		-2 : 359	0.0893257
	 * 		-1 : 1080	0.26872
	 * 		 0 : 1853	0.46106
	 * 		 1 : 585	0.14555
	 * 		 2 : 142	0.035332
	 * 
	 * 		Total = 4019
	 */
	
	FeatureGenerator featureGen;
	GroupMetadata grpMeta;
	GroupProcessing grpProc;
	Preprocessor preproc;
	StanfordLemmatizer lemmatizer;
	POSTagging posTagger;
	
	int[] totalSentiDistro = new int[6];
	double[] startProb = new double[5];
	double[][] transProb, emissionProb;
	
	double[] unknownGroupDistro = new double[]{0.0228,0.0952,0.72148,0.1119,0.04895};
		
	boolean isNewPara = false;
	boolean isNewReview = false;
	boolean isFirstInRev = false;
	FileReader fr;
	BufferedReader br;
	int allReviewCount = 6050;
	
	public Driver(){
		
		preproc = new Preprocessor();
		lemmatizer = new StanfordLemmatizer();
//		posTagger = new POSTagging();
		grpProc = new GroupProcessing();
	}

	public void stereo_scott(String filename){

		TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
		
		map.put(542, -2);
		map.put(656, -2);
		map.put(574, -2);
		map.put(730, -2);
		map.put(738, -2);
		
		map.put(561, -1);
		map.put(707, -1);
		map.put(1162, -1);
		map.put(1199, -1);
		map.put(342, -1);
		map.put(527, -1);
		map.put(835, -1);
		map.put(109, -1);
		map.put(137, -1);
		map.put(288, -1);
		map.put(598, -1);
		map.put(1079, -1);
		map.put(443, -1);
		map.put(729, -1);
		map.put(1086, -1);
		map.put(844, -1);
		map.put(1139, -1);
		map.put(1063, -1);
		map.put(27, -1);
		map.put(160, -1);
		map.put(163, -1);
		map.put(283, -1);
		
		map.put(1094,1);
		map.put(1191,1);
		map.put(284,1);
		map.put(566,1);
		map.put(78,1);
		map.put(58,1);
		
		map.put(794,2);
		
		FileWriter fw1;
		
		try{
			fw1 = new FileWriter(filename);
			for(int i = 1; i < 1283; i++){ 
				
				if(map.containsKey(i))
					{
						fw1.write(map.get(i)+"\n");
						System.out.println(map.get(i));
					}
				else
					{
						fw1.write(0+"\n");
						System.out.println(0);
					}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	public void stereo_dennis(String filename){

		TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
//		map.put(1334, 1);
//		map.put(319, 1);
//		map.put(871, 1);
//		map.put(731, 1);
//		map.put(130, 1);
//		map.put(656, 1);
//		map.put(1342, 1);
//		map.put(1353, 1);
//		
//		map.put(21, -1);
//		map.put(297, -1);
//		map.put(664, -1);
//		map.put(667, -1);
//		map.put(668, -1);
//		map.put(984, -1);
//		map.put(1064, -1);
//		map.put(1352, -1);
//		map.put(1400, -1);
//		map.put(1490, -1);
//		
//		map.put(1247, -2);
//		map.put(132, -2);
//		map.put(308, -2);
//		
//		map.put(314, 2);
//		map.put(178, 2);
		
		//////////////////////////////////////////////////////////////////////////////////////////
		
		
		map.put(346, -2);
		map.put(133, -2);
		map.put(768, -2);
		map.put(875, -2);
		
		
		map.put(53, -1);
		map.put(1018, -1);
		
		map.put(318, 1);
		map.put(1260, 1);
		map.put(1263, 1);
		map.put(186, 1);
		map.put(1274, 1);
		map.put(12, 1);
		map.put(196, 1);
		map.put(289, 1);
		map.put(294, 1);
		map.put(462, 1);

		map.put(391, 2);
		map.put(1275, 2);
		map.put(162, 2);
		map.put(486, 2);
		
		FileWriter fw1;
		
		try{
			fw1 = new FileWriter(filename);
			for(int i = 1; i < 1492; i++){ 
				
				if(map.containsKey(i))
					{
						fw1.write(map.get(i)+"\n");
						System.out.println(map.get(i));
					}
				else
					{
						fw1.write(0+"\n");
						System.out.println(0);
					}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void lastLoopTransform(String filename){
		
		FileWriter fw1;
		String s, ts; int lastStmtID=1;
		try{
			fw1 = new FileWriter("transformedScott_test.txt");
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			while((s = br.readLine()) != null){ 
				
				if(s.length()==0) continue;
				if(s.contains("[")) continue;
				ts = lastStmtID + "\t" + s;
				System.out.println(lastStmtID);
				lastStmtID++;
				fw1.write(ts+"\n");
			}
			fw1.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void readTrainingFile(String filename){
	
		String s;
		int prevGroupID = -1, currentGroupID = -1;
		int prev_s = -1, curr_s = -1;
		int sentiScore = -5;
		String rating = null;
		List<String> words_list;
		String[] words_array;
		HashSet<String> swRemoved;
		HashMap<String,Integer> posFV;
		ArrayList<Integer> scores= new ArrayList<Integer>();
		FileWriter fw1;
		
		try{
			
			fw1 = new FileWriter("Overlap_list.txt");
			fr = new FileReader(filename);
			br = new BufferedReader(fr); 
			
			while((s = br.readLine()) != null){ 
				
				if(s.length()==0){
					Preprocessor.addRatingToSentiDistro(rating, scores);
					continue;
				}
				
				if(s.charAt(0)=='[')
					isNewReview = true;
				else if(s.charAt(0)=='{')
					isNewPara = true;
				
				if(isNewReview){
					currentGroupID = 0;
					curr_s = 6;
					Preprocessor.addsentiTransition(prev_s, curr_s);
					prev_s = 6;
					sentiScore = -5;
					rating = s.substring(s.indexOf('/') + 1 ,s.indexOf(']'));
					scores = new ArrayList<Integer>();
					isNewReview = false;
					isFirstInRev = true;
					continue;
				}
				else {
					sentiScore = Integer.parseInt(s.substring(s.indexOf('<') + 1 ,s.indexOf('>')));
					scores.add(sentiScore);
				}
				
				
				if(isNewPara){
					s = s.substring(3);
//					GroupProcessing.addTransition(prevGroupID, 1);
					curr_s = 5;
					if(prev_s!=6)
					Preprocessor.addsentiTransition(prev_s, curr_s);
					prev_s = 5;
					prevGroupID = 1;
					totalSentiDistro[5]++;
					isNewPara = false;
				}
				

				if(!isNewReview && s.length()>4)
				{
					s = s.substring(0, s.length()-4);
					s = s.trim();
//					posFV = posTagger.getPOSFeatureVector(s);
					if(isFirstInRev){
						
						Preprocessor.addsentiTransition(6, sentiScore + 2);
						isFirstInRev = false;
					}
					words_array = s.split(" ");
					words_list = lemmatizer.lemmatize(s);
					swRemoved = preproc.removeStopwords(words_list);
					grpProc.train_getOrCreateGroupID(swRemoved, sentiScore, fw1);
//					currentGroupID = GroupProcessing.getOrCreateGroupIDFromSentence(s);
//					GroupProcessing.addTransition(prevGroupID, currentGroupID);
//					words_list = lemmatizer.lemmatize(s);

					Preprocessor.addWordToSentiDistro(words_array, sentiScore);
//					Preprocessor.addWordToSentiDistro(words_list, sentiScore);
					curr_s = sentiScore + 2;
					totalSentiDistro[sentiScore+2]++ ; 
					Preprocessor.addsentiTransition(prev_s, curr_s);
//					Preprocessor.addLengthToSentiDistro(sentiScore, words_array.length);
//					Preprocessor.addPosmapToSentiDistro(sentiScore, posFV);
//					System.out.println(s);
				}
							
//				prevGroupID = currentGroupID;
				prev_s = curr_s;
			}
			fw1.close();
			startProb = preproc.setStartProbabilities();
			transProb = preproc.setTransitionProbability();
			System.out.println("Start probabilities : ");
			printArray(startProb);
			
		}
		catch(Exception e)
		{e.printStackTrace();}
	}

	public void printProb(double[] arr){
		
		StringBuffer sb = new StringBuffer();
		NumberFormat formatter = new DecimalFormat("#0.0000000000000");
		for(double d : arr)
			sb.append(Math.pow(Math.E, d) + "\t");
		
//		System.out.println(sb.toString());
	}
	
	public void printArray(double[] arr){
		
		StringBuffer sb = new StringBuffer();
		for(double d : arr)
			sb.append(d + ", ");
		
//		System.out.println(sb.toString());
	}
	
	public int getIndexOfMaxValuedSS(double[] array){
		int index = -1;
		
		double currentMax = Double.MIN_VALUE;	//what if top N values are same?
		
		for(int i = 0; i < array.length; i++){
			if(array[i] > currentMax){
				currentMax = array[i];
				index = i;
			}
		}
		
		if(index==-1){
			return 0;
		}
	
		return index;
	}
	
	public ArrayList<Integer> predictSequence(ArrayList<Integer> groupID_list){
		
//		System.out.println("groupID_list : " + groupID_list);
		
		ArrayList<Integer> prediction = new ArrayList<Integer>();
		int[][] backLinkGroupID = new int[5][groupID_list.size()];
		
		emissionProb = preproc.setEmissionProbability(groupID_list, grpProc);
		
		double[] prevProb = new double[5], currentProb = new double[5], tempProb = new double[5];
		GroupMetadata grpMeta;
		int currentGrpID, maxValuedSS=-1;
		double sentiCountForGivenGroup = 1, tempTransLog, tempLog;
		
		for(int i = 0 ; i < groupID_list.size(); i++){
			
			if(i==0){
			
				currentGrpID = groupID_list.get(0);
				if(currentGrpID ==-1){
					// substitute distribution
					
					for(int j = 0; j < 5 ; j++){

//						sentiCountForGivenGroup = unknownGroupDistro[j]*allReviewCount;
						sentiCountForGivenGroup = 0.0001;
						
						currentProb[j] = Math.log(startProb[j]) 
								+ Math.log(sentiCountForGivenGroup)-Math.log(totalSentiDistro[j]);

//						System.out.println("currentProb["+j+"]" + Math.pow(Math.E,currentProb[j]));
						backLinkGroupID[j][0] = -5;
					}
					maxValuedSS = getIndexOfMaxValuedSS(currentProb);
					prevProb = currentProb;
//					System.out.println("Just out of START with unknown");
				}
				else{
					grpMeta = grpProc.groupMap.get(currentGrpID);
					for(int j = 0; j < 5 ; j++){

						if(grpMeta.sentiFV.containsKey(j-2))
							sentiCountForGivenGroup = grpMeta.sentiFV.get(j-2);
						else
							sentiCountForGivenGroup = 0.0001;
						
						currentProb[j] = Math.log(startProb[j]) 
								+ Math.log(sentiCountForGivenGroup)-Math.log(totalSentiDistro[j]);
						
						backLinkGroupID[j][0] = -5;
					}
					
					maxValuedSS = getIndexOfMaxValuedSS(currentProb);
					prevProb = currentProb;
//					System.out.println("Just out of START with known");
				}
			}// if i==0
			
			else{	// if i!=0
				
				currentGrpID = groupID_list.get(i);
				if(currentGrpID ==-1){
					// substitute distribution
					
					for(int j = 0; j < 5 ; j++){
					
//						sentiCountForGivenGroup = unknownGroupDistro[j]*allReviewCount;
						sentiCountForGivenGroup = 0.0001;
						
//						System.out.println("prevProb found is " ); printProb(prevProb);
						
						for(int k = 0; k < 5; k++){
							
							tempTransLog = (transProb[k][j]==0?Math.log(0.0001):Math.log(transProb[k][j]));
//							System.out.println("transProb["+k+"]["+j+"]" + tempTransLog);
							
							tempProb[k] = prevProb[k]
									+ tempTransLog 
									+ Math.log(sentiCountForGivenGroup)
									- 2*Math.log(totalSentiDistro[j]);
							
//							System.out.println( k+"\t"+ prevProb[k] + "\t" + tempTransLog +
//									"\t" + Math.log(sentiCountForGivenGroup) + "\t" + Math.log(totalSentiDistro[j]));
//							System.out.println("@@@@@prevProb["+k+"] : " + prevProb[k]);
//							
//							if(tempProb[k]==Double.NaN)
//								System.out.println("k-" + k + "\tj-" + j);
						}
						
						maxValuedSS = getIndexOfMaxValuedSS(tempProb);
						backLinkGroupID[j][i] = maxValuedSS;
						currentProb[j] = tempProb[maxValuedSS];
					} // for j ends
					prevProb = currentProb;
					
				}
				else{
					grpMeta = grpProc.groupMap.get(currentGrpID);
					for(int j = 0; j < 5 ; j++){
						
						if(grpMeta.sentiFV.containsKey(j-2))
							sentiCountForGivenGroup = grpMeta.sentiFV.get(j-2);
						else
							sentiCountForGivenGroup = 0.0001;
							
//						System.out.println("prevProb found is " ); printArray(prevProb);
						
						for(int k = 0; k < 5; k++){
							
							tempTransLog = (transProb[k][j]==0?Math.log(0.0001):Math.log(transProb[k][j]));
//							System.out.println("transProb["+k+"]["+j+"]" + tempTransLog);
							tempProb[k] = prevProb[k]
									+ tempTransLog 
									+ Math.log(sentiCountForGivenGroup)
									- 2*Math.log(totalSentiDistro[j]);
							
//							System.out.println( k+"\t"+ prevProb[k] + "\t" + tempTransLog +
//									"\t" + Math.log(sentiCountForGivenGroup) + "\t" + Math.log(totalSentiDistro[j]));
//							System.out.println("-----prevProb["+k+"] : " + prevProb[k]);
//							
//							if(tempProb[k]==Double.NaN)
//								System.out.println("k-" + k + "\tj-" + j);
						}
						
						maxValuedSS = getIndexOfMaxValuedSS(tempProb);
						backLinkGroupID[j][i] = maxValuedSS;
						currentProb[j] = tempProb[maxValuedSS];
					} // for j ends
					prevProb = currentProb;
				} // known group block ends
			}
//			System.out.println("prevProb is as follows!");
//			printProb(prevProb);
		} // all groups processed
		
		ArrayList<Integer>reverseList = new ArrayList<Integer>();

		int nextMax;
		for(int c = groupID_list.size()-1; c >= 0 ; c--){
			
			nextMax = backLinkGroupID[maxValuedSS][c];
//			System.out.println("nextMax" + nextMax);
			if(nextMax==-5) nextMax = 0;
			reverseList.add(nextMax);
			maxValuedSS = nextMax;
		}
		
		int temp;
		while(!reverseList.isEmpty()){
		
			temp = reverseList.get(reverseList.size()-1);
			prediction.add(temp);
			reverseList.remove(reverseList.size()-1);
		}
		
		System.out.println("Prediction.size() : " + prediction.size());
		return prediction;
	}
	
	public void readTestFileAndProcess(String test_filename){
		
		ArrayList<Integer> allPredictions = new ArrayList<Integer>();
		List<String> words_list;
		HashSet<String> swRemoved;
		int current_groupID = -1;
		int totalSentences = 0, unseenSentences = 0;
		int prevGroupID = -1, currentGroupID = -1;
		ArrayList<Integer> tempGroupIDList = new ArrayList<Integer>();
		ArrayList<Integer> tempPredictionList =  new ArrayList<Integer>();
		
		String s;
		
		try{
		
			fr = new FileReader(test_filename);
			br = new BufferedReader(fr); 
			
			while((s = br.readLine()) != null){ 
			
				if(s.length() == 0){
					// predictSequence
					tempPredictionList = predictSequence(tempGroupIDList);
					for(int x : tempPredictionList)
						allPredictions.add(x);
					tempGroupIDList = new ArrayList<Integer>();
					continue;
				}
				
				if(s.charAt(0)=='[')
					isNewReview = true;
				else if(s.charAt(0)=='{')
					isNewPara = true;
				
				if(isNewReview){
					currentGroupID = 0;
					isNewReview = false;
					isFirstInRev = true;
					continue;
				}
				else {
				}
				
				
				if(isNewPara){
					s = s.substring(3);
//					GroupProcessing.addTransition(prevGroupID, 1);
					isNewPara = false;
					prevGroupID = 1;
				}
				

				if(!isNewReview && s.length()>4)
				{
					s = s.substring(0, s.length()-4);
					s = s.trim();
					if(isFirstInRev){
						
						isFirstInRev = false;
					}
					
					totalSentences++;
					words_list = lemmatizer.lemmatize(s);
					swRemoved = preproc.removeStopwords(words_list);
					current_groupID = grpProc.test_getGroupID(swRemoved);
					if(current_groupID == -1){
						unseenSentences++;
					}
					tempGroupIDList.add(current_groupID);
				}
			}
			
			//Write whole allPredictions to o/p file 
			FileWriter fw = new FileWriter("hompm.txt");
			for(int p : allPredictions)
				fw.write(p + "\n");
			fw.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("unseenSentences : " + unseenSentences);
		System.out.println("totalSentences : " + totalSentences);
	}
	
	
	public ArrayList<Integer> addMinorToMajorList(ArrayList<Integer> minor, ArrayList<Integer> major){
		
		for(int x : minor)
			major.add(x);
		
		return major;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Driver driver = new Driver();
//		driver.readTrainingFile("sample.txt");
//		driver.readTrainingFile("DennisSchwartz_train.txt");
//		driver.readTestFileAndProcess("DennisSchwartz_test.txt");

//		driver.readTrainingFile("ScottRenshaw_train.txt");
//		System.out.println("Read the entire file");
//		
//		driver.readTestFileAndProcess("ScottRenshaw_test.txt");
		
//		driver.readTrainingFile("hmm_train.txt");
//		Preprocessor.printSentiTransition();
//		driver.readTestFileAndProcess("single_test.txt");

//		driver.lastLoopTransform("ScottRenshaw_test.txt");
		driver.stereo_dennis("Dennis_trans_hompm.txt");
		System.out.println("Done");
		
//		driver.grpProc.printGroupMap();

		Preprocessor.printSentiDistroByWord();
//		Preprocessor.printSentiTransition();
//		Preprocessor.printLengthDistroBySentiment();
//		Preprocessor.printPOSDistroBySentiment();
//		Preprocessor.printSentiDistroByRating();
//		driver.readTestFileAndProcess("DennisSchwartz_test.txt");
	}

}
