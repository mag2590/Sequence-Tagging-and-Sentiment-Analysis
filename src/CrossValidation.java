import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
public class CrossValidation 
{
	FeatureGenerator featureGen;
	GroupMetadata grpMeta;
	GroupProcessing grpProc;
	Preprocessor preproc;
	//StanfordLemmatizer lemmatizer;

	boolean isNewPara = false;
	boolean isNewReview = false;
	FileReader fr,fr1;
	BufferedReader br,br1;
	static ArrayList<Integer> scores= new ArrayList<Integer>();
	static ArrayList<Integer> real_scores=new ArrayList<Integer>();
	public CrossValidation(){

		preproc = new Preprocessor();
		//lemmatizer = new StanfordLemmatizer();
	}

	public void readTrainingFile(String filename){

		String s;
		int prevGroupID = -1, currentGroupID = -1;
		int sentiScore = -5;
		String rating = null;
		List<String> words_list;
		String[] words_array;
		

		try{

			fr = new FileReader(filename);
			br = new BufferedReader(fr); 

			while((s = br.readLine()) != null){ 

				if(s.length()==0){
					Preprocessor.addRatingToSentiDistro(rating, scores);
					continue;
				}

				if(s.charAt(0)=='[')
					continue;
				else if(s.charAt(0)=='{')
					isNewPara = true;

					sentiScore = Integer.parseInt(s.substring(s.indexOf('<')+1 ,s.indexOf('>')));
					scores.add(sentiScore);

				if(isNewPara){
					s = s.substring(3);
					//GroupProcessing.addTransition(prevGroupID, 1);
					prevGroupID = 1;
					isNewPara = false;
				}
			}
			//System.out.println(scores);
			System.out.println(scores.size());
		}
		catch(Exception e)
		{e.printStackTrace();}

	}
	public void readTestFile(String filename)
	{
		
		String s;
		int prevGroupID = -1, currentGroupID = -1;
		int sentiScore = -5;
		String rating = null;
		List<String> words_list;
		String[] words_array;
		try{

			fr = new FileReader(filename);
			br = new BufferedReader(fr); 

			while((s = br.readLine()) != null){ 

				if(s.length()==0){
					Preprocessor.addRatingToSentiDistro(rating, real_scores);
					continue;
				}

				if(s.charAt(0)=='[')
					continue;
				else if(s.charAt(0)=='{')
					isNewPara = true;
					sentiScore = Integer.parseInt(s.substring(s.indexOf('<')+1 ,s.indexOf('>')));
					real_scores.add(sentiScore);

				if(isNewPara){
					s = s.substring(3);
					//						GroupProcessing.addTransition(prevGroupID, 1);
					prevGroupID = 1;
					isNewPara = false;
				}

			}
			//System.out.println(scores);
			System.out.println(real_scores.size());
		}
		catch(Exception e)
		{e.printStackTrace();}

	}	
	
	public int[][] matrixOutput(String filename, ArrayList<Integer> sentilist)
	{
		int[][] result=new int[5][5];
		int j,k;
		for(int i=0;i<real_scores.size();i++)
		{
//			if(real_scores.get(i)==-1)
//				j=3;
//			else if(real_scores.get(i)==-2)
//				j=4;
//			else if(real_scores.get(i)==0)
//				j=2;
//			else j=real_scores.get(i);
//			if(sentilist.get(i)==-1)
//				k=3;
//			else if(sentilist.get(i)==-2)
//				k=4;
//			else
//				k=sentilist.get(i);
			j=real_scores.get(i)+2;
			k=sentilist.get(i)+2;
//			if(real_scores.get(i)==sentilist.get(i))
			result[k][j]+=1;
				
		//		break;
		}
		return result;
	}
	public void displayMatrix(int[][] matrix)
	{
		DecimalFormat fmt = new DecimalFormat("0");
		for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                System.out.print(fmt.format(matrix[row][col]) + " "+" "+" ");
            }
            System.out.println();
        }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CrossValidation crv = new CrossValidation();
		//			driver.readTrainingFile("sample_train.txt");
		crv.readTrainingFile("sample_test.txt");
		crv.readTestFile("sample2");
		int[][] res;
		ArrayList scr=scores;
		res=crv.matrixOutput("sample2",scr);
		crv.displayMatrix(res);
	}

}


