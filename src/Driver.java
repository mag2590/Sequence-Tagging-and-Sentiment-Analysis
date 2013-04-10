import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Driver {

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
//					words_array = s.split(" ");
					words_list = lemmatizer.lemmatize(s);
					swRemoved = preproc.removeStopwords(words_list);
					grpProc.train_getOrCreateGroupID(swRemoved, sentiScore, fw1);
//					currentGroupID = GroupProcessing.getOrCreateGroupIDFromSentence(s);
//					GroupProcessing.addTransition(prevGroupID, currentGroupID);
//					words_list = lemmatizer.lemmatize(s);

//					Preprocessor.addWordToSentiDistro(words_array, sentiScore);
//					Preprocessor.addWordToSentiDistro(words_list, sentiScore);
//					curr_s = sentiScore + 2;
					totalSentiDistro[sentiScore+2]++ ; 
//					Preprocessor.addsentiTransition(prev_s, curr_s);
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
		}
		catch(Exception e)
		{e.printStackTrace();}
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
		return index;
	}
	
	public ArrayList<Integer> predictSequence(ArrayList<Integer> groupID_list){
		
		ArrayList<Integer> prediction = new ArrayList<Integer>();
		int[][] backLinkGroupID = new int[5][groupID_list.size()];
		
		emissionProb = preproc.setEmissionProbability(groupID_list, grpProc);
		
		double[] prevProb = new double[5], currentProb = new double[5], tempProb = new double[5];
		GroupMetadata grpMeta;
		int currentGrpID, maxValuedSS=-1;
		double sentiCountForGivenGroup = Double.MIN_VALUE;
		
		for(int i = 0 ; i < groupID_list.size(); i++){
			
			if(i==0){
			
				currentGrpID = groupID_list.get(0);
				if(currentGrpID ==-1){
					// substitute distribution
					
					for(int j = 0; j < 5 ; j++){

						sentiCountForGivenGroup = unknownGroupDistro[j]*allReviewCount;
						
						currentProb[j] = Math.log(startProb[j]) 
								+ Math.log(sentiCountForGivenGroup)-Math.log(totalSentiDistro[j]);
						
						backLinkGroupID[j][0] = -5;
					}
					maxValuedSS = getIndexOfMaxValuedSS(currentProb);
					prevProb = currentProb;
				}
				else{
					grpMeta = grpProc.groupMap.get(currentGrpID);
					for(int j = 0; j < 5 ; j++){

						if(grpMeta.sentiFV.containsKey(j-2))
							sentiCountForGivenGroup = grpMeta.sentiFV.get(j-2);
						else
							sentiCountForGivenGroup = Double.MIN_VALUE;
						
						currentProb[j] = Math.log(startProb[j]) 
								+ Math.log(sentiCountForGivenGroup)-Math.log(totalSentiDistro[j]);
						
						backLinkGroupID[j][0] = -5;
					}
					
					maxValuedSS = getIndexOfMaxValuedSS(currentProb);
					prevProb = currentProb;
				}
			}// if i==0
			
			else{	// if i!=0
				
				currentGrpID = groupID_list.get(i);
				if(currentGrpID ==-1){
					// substitute distribution
					
					for(int j = 0; j < 5 ; j++){
					
						sentiCountForGivenGroup = unknownGroupDistro[j]*allReviewCount;
							
						for(int k = 0; k < 5; k++){
							
							tempProb[k] = Math.log(prevProb[k])
									+ Math.log(transProb[k][j]) +
									+ Math.log(sentiCountForGivenGroup) - 2*Math.log(totalSentiDistro[j]);
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
							sentiCountForGivenGroup = Double.MIN_VALUE;
							
						for(int k = 0; k < 5; k++){
							
							tempProb[k] = Math.log(prevProb[k])
									+ Math.log(transProb[k][j]) +
									+ Math.log(sentiCountForGivenGroup) - 2*Math.log(totalSentiDistro[j]);
						}
						
						maxValuedSS = getIndexOfMaxValuedSS(tempProb);
						backLinkGroupID[j][i] = maxValuedSS;
						currentProb[j] = tempProb[maxValuedSS];
					} // for j ends
					prevProb = currentProb;
				} // known group block ends
			} 
		} // all groups processed
		
		ArrayList<Integer>reverseList = new ArrayList<Integer>();

		int nextMax;
		for(int c = groupID_list.size()-1; c >= 0 ; c--){
			
			nextMax = backLinkGroupID[maxValuedSS][c];
			reverseList.add(nextMax-2);
			maxValuedSS = nextMax;
		}
		
		int temp;
		while(!groupID_list.isEmpty()){
		
			temp = groupID_list.get(groupID_list.size()-1);
			prediction.add(temp);
			groupID_list.remove(groupID_list.size()-1);
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
//					tempGroupIDList = new ArrayList<Integer>();
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
					if(current_groupID != -1){
						unseenSentences++;
					}
//					currentGroupID = GroupProcessing.getOrCreateGroupIDFromSentence(s);
//					words_list = lemmatizer.lemmatize(s);
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
		
		driver.readTrainingFile("hmm_train.txt");
		driver.readTestFileAndProcess("hmm_test.txt");

		System.out.println("\n");
		
		driver.grpProc.printGroupMap();

//		Preprocessor.printSentiDistroByWord();
//		Preprocessor.printSentiTransition();
//		Preprocessor.printLengthDistroBySentiment();
//		Preprocessor.printPOSDistroBySentiment();
//		Preprocessor.printSentiDistroByRating();
//		driver.readTestFileAndProcess("DennisSchwartz_test.txt");
	}

}
