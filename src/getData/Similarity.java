package getData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Similarity {

	public static void main(String args[]) throws Exception
	{
		
	}
	
	public static TreeMap<String, Float> getIDF() throws IOException
	{
		final TreeMap<String, Float> invfrequencyMap = new TreeMap<String, Float>(); 

		final File folder = new File("E:/Dataset/MSR2014/StemmedBugReports/"+""+"/");
		final List<File> fileList = Arrays.asList(folder.listFiles());

		List<String> stopWords = Arrays.asList("a", "aaa","aad","abc","able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "b", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your", "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours ", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"
				,"aaaaaaaaaaaaaaaa","aaaaaaaaaae","aaaabqlodgaaaa","aaab","abcdefghijklm","abcdefghijkl","abcdefghijk","abcdefghij","abcdefghi","abcdefgh","abf");

		for(int i=0;i<fileList.size();i++)
		{
			final BufferedReader br = new BufferedReader(new FileReader(fileList.get(i))); 
			// Mapping of String->Integer (word -> frequency) 
			final TreeMap<String, Float> frequencyMap = new TreeMap<String, Float>(); 

			// Iterate through each line of the file 
			String line; 
			while ((line = br.readLine()) != null) { 

				// Iterate through each word of the current line 
				final StringTokenizer ps = new StringTokenizer(line, " \t\n\r\f.,;:!?'{}#+=~|0<>123456789$()[]-/"); 

				while (ps.hasMoreTokens()) { 
					final String word = ps.nextToken(); 

					//if(word.length()>=3 && !(stopWords.contains(word)))
					//{
					Float frequency = frequencyMap.get(word); 

					if (frequency == null) { 
						frequency = 0.0f; 
					} 
					frequencyMap.put(word, frequency+ 1); 
					//}
				}
			}

			//System.out.println(frequencyMap);


			for(Map.Entry<String,Float> entry : frequencyMap.entrySet()) {
				String key = entry.getKey();
				Float value = entry.getValue();

				if(key!=null)
				{
					Float frequency = invfrequencyMap.get(key); 
					if (frequency == null) { 
						frequency = (float) 0; 
					}
					frequency=frequency+1; 
					invfrequencyMap.put(key,frequency);
				}
			}

			br.close();
		}

		for(Map.Entry<String,Float> entry : invfrequencyMap.entrySet()) {
			String key = entry.getKey();
			Float value = entry.getValue();

			DecimalFormat df = new DecimalFormat("##.####");
			df.setRoundingMode(RoundingMode.DOWN);

			Float frequency=(float)Math.log10(fileList.size()/value);

			invfrequencyMap.put(key,Float.parseFloat(df.format(frequency)));
		}

		return invfrequencyMap;
	}
}
