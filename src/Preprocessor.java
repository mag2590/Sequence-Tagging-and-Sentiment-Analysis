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
	static HashMap<Integer,TreeMap<Integer,Integer>> lengthSentiDistro = new HashMap<Integer, TreeMap<Integer,Integer>>();
	static HashMap<Integer,TreeMap<String,Integer>> posSentiDistro = new HashMap<Integer, TreeMap<String,Integer>>();
	static int[][] sentiTransition;
	
	public Preprocessor(){
		
		sentiTransition = new int[7][7];
		loadStopwordsList();
	}
	
	public static void addPosmapToSentiDistro(int sentiScore , HashMap<String,Integer> posmap){
		
		TreeMap<String,Integer> temp;
		String pos;
		int freq;
		int prevCount = -1; 
		if(!posSentiDistro.containsKey(sentiScore))
			temp = new TreeMap<String, Integer>();
		else
			temp = posSentiDistro.get(sentiScore);
		
		
		Iterator itr = posmap.entrySet().iterator();
		Map.Entry me;
		
		while(itr.hasNext()){
			
			me = (Map.Entry) itr.next();
			pos = (String) me.getKey();
			freq = (Integer) me.getValue();
			
			if(!temp.containsKey(pos))
				prevCount = 0;
			else
				prevCount = temp.get(pos);
			
			prevCount += freq;
			temp.put(pos, prevCount);
		}
		
		posSentiDistro.put(sentiScore, temp);
	}
	
	public static void printPOSDistroBySentiment(){
		
		Iterator itr_tree, itr = posSentiDistro.entrySet().iterator();
		Map.Entry me, me_tree;
		TreeMap<String,Integer> posDistro;
		ArrayList<String> posList;
		ArrayList<Integer> freqList;
		int sum = 0;
		StringBuffer sb;
		NumberFormat formatter = new DecimalFormat("#0.0000");
		while(itr.hasNext()){
			
			sum = 0; posList = new ArrayList<String>(); freqList = new ArrayList<Integer>();
			sb = new StringBuffer();
			me = (Map.Entry) itr.next();
			
			sb.append((Integer)me.getKey() + "\t");
			posDistro = (TreeMap<String,Integer>) me.getValue();
			
			itr_tree = posDistro.entrySet().iterator();
			while(itr_tree.hasNext()){
				
				me_tree = (Map.Entry)itr_tree.next();
				posList.add((String) me_tree.getKey());
				sum += (Integer) me_tree.getValue();
				freqList.add((Integer) me_tree.getValue());
			}
			
			for(int i=0; i < posList.size(); i++){
				
				sb.append(posList.get(i) + ":");
				sb.append(formatter.format(((double)freqList.get(i)/sum)) + "  " );
			}
				
			System.out.println(sb.toString());
		}
	}
	
	
	public static void addLengthToSentiDistro(int sentiScore , int length){
		
		TreeMap<Integer,Integer> temp;
		int prevCount = -1; 
		if(!lengthSentiDistro.containsKey(sentiScore))
			temp = new TreeMap<Integer, Integer>();
		else
			temp = lengthSentiDistro.get(sentiScore);
		
		if(!temp.containsKey(length))
			prevCount = 0;
		else
			prevCount = temp.get(length);
		
		prevCount++;
		temp.put(length, prevCount);
		lengthSentiDistro.put(sentiScore, temp);
	}
	
	
	public static void printLengthDistroBySentiment(){
		
		Iterator itr_tree, itr = lengthSentiDistro.entrySet().iterator();
		Map.Entry me, me_tree;
		TreeMap<Integer,Integer> lenDistro;
		StringBuffer sb;
		while(itr.hasNext()){
			
			sb = new StringBuffer();
			me = (Map.Entry) itr.next();
			
			sb.append((Integer)me.getKey() + "\t");
			lenDistro = (TreeMap<Integer,Integer>) me.getValue();
			
			itr_tree = lenDistro.entrySet().iterator();
			while(itr_tree.hasNext()){
				
				me_tree = (Map.Entry)itr_tree.next();
				sb.append((Integer) me_tree.getKey() + ":");
				sb.append((Integer) me_tree.getValue() + "  ");
			}
			
			System.out.println(sb.toString());
		}
	}
	
	public static void addsentiTransition(int i, int j){
		
		if(i>=0 && j>=0)
		sentiTransition[i][j]++;
	}
	
	public static void printSentiTransition(){
		
		StringBuffer sb;
		for(int i = 0; i < 7; i++){
			
			sb = new StringBuffer();
			for(int j = 0; j < 7 ; j++){
				
				sb.append(sentiTransition[i][j] + "\t");
			}
			
			System.out.println(sb.toString());
		}
			
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
		
		stopWords.add("a"); stopWords.add("over"); stopWords.add("her"); stopWords.add("has"); 
		stopWords.add("than"); stopWords.add("can"); stopWords.add("we"); stopWords.add("be");
	}
	
	public HashSet<String> removeStopwords(List<String> temp)
	{
//		StringBuffer sb = new StringBuffer();
//		String[] temp = line.split(" ");
	
		HashSet<String> result = new HashSet<String>();
		for(String w : temp)
		{
			if(!stopWords.contains(w))
//				sb.append(temp[i] + " ");
				result.add(w);
		}
		
		return result;
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
