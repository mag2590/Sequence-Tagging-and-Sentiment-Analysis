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
	
	int[] totalSentiDistro = new int[5];
	double[] startProb = new double[5];
	double[][] transProb, emissionProb;
	
	boolean isNewPara = false;
	boolean isNewReview = false;
	boolean isFirstInRev = false;
	FileReader fr;
	BufferedReader br;
	
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
	
	public ArrayList<Integer> predictSequence(ArrayList<Integer> groupID_list){
		
		emissionProb = preproc.setEmissionProbability(groupID_list, grpProc);
		
		double[] runningProb = new double[5];
		
		for(int i = 0 ; i < groupID_list.size(); i++){
			
			if(i==0){
				
			}
		}
		
		return new ArrayList<Integer>();
	}
	
	public void readTestFileAndProcess(String filename){
		
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
		
			fr = new FileReader("DennisSchwartz_test.txt");
			br = new BufferedReader(fr); 
			
			while((s = br.readLine()) != null){ 
			
				if(s.length() == 0){
					// predictSequence
//					tempPredictionList = predictSequence(tempGroupIDList);
//					for(int x : tempPredictionList)
//						allPredictions.add(x);
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
					if(current_groupID != -1){
						unseenSentences++;
					}
//					currentGroupID = GroupProcessing.getOrCreateGroupIDFromSentence(s);
//					words_list = lemmatizer.lemmatize(s);
				}
			}
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
		driver.readTrainingFile("DennisSchwartz_train.txt");
		driver.readTestFileAndProcess("DennisSchwartz_test.txt");
		System.out.println("\n");
		driver.grpProc.printGroupMap();

//		Preprocessor.printSentiDistroByWord();
//		Preprocessor.printSentiTransition();
//		Preprocessor.printLengthDistroBySentiment();
//		Preprocessor.printPOSDistroBySentiment();
//		Preprocessor.printSentiDistroByRating();
		//driver.readTestFileAndProcess("DennisSchwartz_test.txt");
	}

}
