import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class Driver {

	FeatureGenerator featureGen;
	GroupMetadata grpMeta;
	GroupProcessing grpProc;
	Preprocessor preproc;
	StanfordLemmatizer lemmatizer;
	
	boolean isNewPara = false;
	boolean isNewReview = false;
	FileReader fr;
	BufferedReader br;
	
	public Driver(){
		
		preproc = new Preprocessor();
		lemmatizer = new StanfordLemmatizer();
	}
	
	public void readTrainingFile(String filename){
	
		String s;
		int prevGroupID = -1, currentGroupID = -1;
		int sentiScore = -5;
		String rating = null;
		List<String> words_list;
		String[] words_array;
		ArrayList<Integer> scores= new ArrayList<Integer>();
		
		try{
			
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
					sentiScore = -5;
					rating = s.substring(s.indexOf('/')+1 ,s.indexOf(']'));
					scores = new ArrayList<Integer>();
					isNewReview = false;
					continue;
				}
				else {
					sentiScore = Integer.parseInt(s.substring(s.indexOf('<')+1 ,s.indexOf('>')));
					scores.add(sentiScore);
				}
				
				
				if(isNewPara){
					s = s.substring(3);
//					GroupProcessing.addTransition(prevGroupID, 1);
					prevGroupID = 1;
					isNewPara = false;
				}
				

				if(!isNewReview && s.length()>4)
				{
					s = s.substring(0, s.length()-4);
					s = s.trim();
//					s = preproc.removeStopwords(s);
//					currentGroupID = GroupProcessing.getOrCreateGroupIDFromSentence(s);
//					GroupProcessing.addTransition(prevGroupID, currentGroupID);
					words_list = lemmatizer.lemmatize(s);
					words_array = s.split(" ");
					Preprocessor.addWordToSentiDistro(words_array, sentiScore);
//					Preprocessor.addWordToSentiDistro(words_list, sentiScore);
//					System.out.println(s);
				}
							
//				prevGroupID = currentGroupID;
			}
		}
		catch(Exception e)
		{e.printStackTrace();}
		
	}
	
	public void readTestFileAndProcess(String filename){
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Driver driver = new Driver();
//		driver.readTrainingFile("sample_train.txt");
		driver.readTrainingFile("DennisSchwartz_train.txt");
		Preprocessor.printSentiDistroByWord();
//		Preprocessor.printSentiDistroByRating();
		//driver.readTestFileAndProcess("DennisSchwartz_test.txt");
	}

}
