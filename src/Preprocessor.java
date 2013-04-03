import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Preprocessor {

	HashSet<String> stopWords;
	static TreeMap<String, int[]> wordSentiDistro = new TreeMap<String, int[]>();
	static HashMap<String, int[]> ratingSentiDistro = new HashMap<String, int[]>();
	
	public Preprocessor(){
		
		loadStopwordsList();
	}
	
	public void loadStopwordsList() {
		stopWords = new HashSet<String>();
		stopWords.add("the");
		stopWords.add("and");
		stopWords.add("for");
		stopWords.add("are");
		stopWords.add("in");
		stopWords.add("is");
		stopWords.add("as");
		stopWords.add("or");
		stopWords.add("it");
		stopWords.add("of");
		stopWords.add("but");
		stopWords.add("from");
		stopWords.add("which");
		stopWords.add("until");
		stopWords.add("that");
		stopWords.add("all");
		stopWords.add("by");
		stopWords.add("was");
		stopWords.add("those");
		stopWords.add("who");
		stopWords.add("had");
		stopWords.add("will");
		stopWords.add("this");
		stopWords.add("their");
		stopWords.add("'s");
		
		stopWords.add("on");
		stopWords.add("to");
		stopWords.add("at");
		
		stopWords.add("do");
		stopWords.add("so");
		stopWords.add("you");
		stopWords.add("not");
		stopWords.add("then");
		stopWords.add("i");
		stopWords.add("if");
		stopWords.add("he");
		stopWords.add("these");
		stopWords.add("into");
		stopWords.add("shall");
		stopWords.add("any");
		
		stopWords.add("with");
		stopWords.add("about");
		stopWords.add("through");
	}
	
	public String removeStopwords(String line)
	{
		StringBuffer sb = new StringBuffer();
		String[] temp = line.split(" ");
		
		for(int i = 0 ; i < temp.length; i++)
		{
			if(!stopWords.contains(temp[i]))
				sb.append(temp[i] + " ");
		}
		
		return sb.toString();
	}
	
	public static void addWordToSentiDistro(String[] words, int score){
		
		int[] temp;
		for(String w : words){
			
			if(!wordSentiDistro.containsKey(w))
				temp = new int[5];
			else
				temp = wordSentiDistro.get(w);
		
			temp[score+2]++;
			wordSentiDistro.put(w, temp);
		}
	}
	
	
	public static void addRatingToSentiDistro(String rating, ArrayList<Integer> scores){
		
		int[] temp;
		
		if(!ratingSentiDistro.containsKey(rating))
			temp = new int[5];
		else
			temp = ratingSentiDistro.get(rating);
			
		for(int score : scores)
			temp[score+2]++;
		
		ratingSentiDistro.put(rating, temp);
	}	
	
	public int[] getSentimentDistributionByWord(String word){
		
		if(wordSentiDistro.containsKey(word))
			return wordSentiDistro.get(word);
		else
			return null;
	}
	
	public int[] getSentimentDistributionByRating(String rating){
		
		if(ratingSentiDistro.containsKey(rating))
			return ratingSentiDistro.get(rating);
		else
			return null;
	}
	
	public HashMap<String,String> getPOSTagsForSentence(){
		return null;
	}
	
	
	public static void printSentiDistroByWord()
	{
		Iterator itr = wordSentiDistro.entrySet().iterator();
		int sum = 0;
		int[] vals;
		int lenDiff = 0;
		String word = null;
		boolean isPrintable = false;
		NumberFormat formatter = new DecimalFormat("#0.000");
		Map.Entry me; 
		FileWriter fw;
		
		try{
			
			fw = new FileWriter("word_senti_smartAnalysis.txt");
			
			while(itr.hasNext()){
				
				sum = 0;
				me = (Map.Entry) itr.next();
				word = (String) me.getKey();
				vals = (int[])me.getValue();
				
				for(int i = 0 ; i < vals.length; i++){
		
					if(i==2) continue;
					sum += vals[i];
					if(vals[i] > 2*vals[2]/3) isPrintable = true;
				}
				
				if(sum > vals[2]) isPrintable = true;
				sum += vals[2];
				
				StringBuffer sb;
				
				if(isPrintable){
				
					sb = new StringBuffer();
//					end = (word.length() > 7) ? 7 : word.length();
					lenDiff = 12 - word.length();

					String s1 = " ", s2 = "  ", s3 = "   ", s4 = "    ", s5 = "     ", s6 = "      ";
					String s7 = s5 + s2;
					String s8 = s5 + s3;
					String s9 = s5 + s4;
					String s10 = s5 + s5;
					String s11 = s10 + s1;
					
					switch(lenDiff){
					case 1 : word = word + s1; break;
					case 2 : word = word + s2; break;
					case 3 : word = word + s3; break;
					case 4 : word = word + s4; break;
					case 5 : word = word + s5; break;
					case 6 : word = word + s6; break;
					case 7 : word = word + s7; break;
					case 8 : word = word + s8; break;
					case 9 : word = word + s9; break;
					case 10 : word = word + s10; break;
					case 11 : word = word + s11; break;
					}
					
					sb.append(word + "\t\t");
					
					for(int v : vals)
//					sb.append(formatter.format(((double)v/sum)) + "\t");
					sb.append(v + "\t");
					
					fw.write(sb.toString() + "\n");
				}
					
				isPrintable = false;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n Size : " + wordSentiDistro.size());
	}
	
	
	public static void printSentiDistroByRating()
	{
		Iterator itr = ratingSentiDistro.entrySet().iterator();
		int sum = 0;
		int[] vals;
		int lenDiff = 0;
		String rating = null;
		NumberFormat formatter = new DecimalFormat("#0.000");
		Map.Entry me; 
		FileWriter fw;
		
		try{
			
			fw = new FileWriter("rating_senti_analysis.txt");
			
			while(itr.hasNext()){
				
				me = (Map.Entry) itr.next();
				rating = (String) me.getKey();
				vals = (int[])me.getValue();
				
				for(int v : vals)
					sum += v;
				
//				System.out.println(sum);
				StringBuffer sb = new StringBuffer();		
				sb.append(rating + "\t\t");
				
				for(int v : vals)
				sb.append(formatter.format(((double)v/sum)) + "\t");
//				sb.append(v + "\t");
				
				System.out.println(sb.toString());// + "\n");
				sum = 0;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("\n\n\n Size : " + ratingSentiDistro.size());
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
