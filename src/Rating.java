import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Rating {
	FileReader fr;
	BufferedReader br;
	int numReview = 0;
	boolean isNewPara = false;
	boolean isNewReview = false;
	int sentiScore = -5;
	String currRating = "";
	static HashMap<String, int[]> ratingSentiDistro = new HashMap<String, int[]>();


	public Rating() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public void readTrainingFile(String filename){
		String s;
		try{
			fr = new FileReader(filename);
			br = new BufferedReader(fr); 
			while((s = br.readLine()) != null){ 
				if(s.length() > 0 && s.charAt(0)=='['){
					numReview++;
					isNewReview = true;
				}
				if(s.length() > 0 && s.charAt(0)=='{'){
					isNewPara = true;
				}
				
				if(isNewReview){
					/// Do Something
					currRating = s.substring(s.indexOf('/')+1 ,s.indexOf(']'));
//					System.out.println(currRating);
					isNewReview = false;
					continue;
				}
				if(isNewPara){
					//Do Something
					sentiScore = Integer.parseInt(s.substring(s.indexOf('{')+1 ,s.indexOf('}')));
					addRatingToSentiDistro(currRating, sentiScore);
					isNewPara = false;
//					System.out.println(sentiScore);
				}
				
			}
			System.out.println("Number of reviews  = " + numReview);
			printSentiDistroByRating();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void addRatingToSentiDistro(String rating, int score){
		int[] temp;
		
		if(!ratingSentiDistro.containsKey(rating))
			temp = new int[5];
		else
			temp = ratingSentiDistro.get(rating);
			
		temp[score+2]++;
		
		ratingSentiDistro.put(rating, temp);
	}	
	
	public static void printSentiDistroByRating()
	{
		Iterator itr = ratingSentiDistro.entrySet().iterator();
		int[] vals;
		int sum = 0;
		String rating = null;
		NumberFormat formatter = new DecimalFormat("#0.000");
		Map.Entry me; 
		FileWriter fw;
		
		try{
			
			fw = new FileWriter("src/analysis.txt");
			
			while(itr.hasNext()){
				
				me = (Map.Entry) itr.next();
				rating = (String) me.getKey();
				vals = (int[])me.getValue();
				for(int v : vals)
					sum += v;
				
				System.out.println(sum);
				StringBuffer sb = new StringBuffer();		
				sb.append(rating + "\t\t");
				int f_name = -2;
				for(int v : vals){
						sb.append(f_name + ":");
						System.out.println(f_name + ": " + v);
						sb.append(formatter.format(((double)v/sum)) + "\t\t");
						f_name++;
				}
				sum = 0;
				System.out.println(sb.toString());// + "\n");
				fw.write(sb.toString() + "\n");
			}
			fw.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n Size : " + ratingSentiDistro.size());

	}
	public static void main(String[] args) {
		Rating rating = new Rating();
		rating.readTrainingFile("src/ScottRenshaw_none_merged.txt");
		// TODO Auto-generated method stub

	}

}
