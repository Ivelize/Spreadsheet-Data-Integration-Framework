package uk.ac.manchester.dstoolkit.service.impl.util.mutation;

import org.apache.log4j.Logger;

/***
 * Mutation Level: Syntactic level mutations
 * Mutation Type : Misspelling mutations
 * 
 * To introduce systematic misspelling permutations. The class implements the following methods:
 * 	(1) missedCharTypo - random char is missing from word. 
 *  (2) transposeCharsTypo - randomly transpose two chars in word by swapping them.
 *  (3) transposeAllCharsTypo - all chars are transposed in word
 *  (4) doubleCharTypo - random char is double in word.
 *  
 * Also, the class holds an EnumClass for the above 4 choices of mutations. 
 * 
 * 
 * @author klitos
 */
public class SyntacticMutations {
	
	private static Logger logger = Logger.getLogger(SyntacticMutations.class);
		
	RandomGeneratorImpl randomGen = null;
		
	/**
	 * Constructor: 
	 */
	public SyntacticMutations(RandomGeneratorImpl randomGen) {
		this.randomGen = randomGen;		
	}//end constructor	
		
	/**
	 * Method: generate missed character typos
	 */
	public String missedCharTypo(String word) {
		String mutatedWord = null;
		int length = word.length();
		
		if (length == 1) {
			return word;
		} else {
		//get an index at random from the string to mutate
			length = word.length();
			int pos = this.randomGen.randInt(0, length-1);
			logger.debug("random pos: " + pos);
			
			if (pos == 0) {
				return word.substring(pos + 1);
			} else if (pos == (length - 1)) {
				return word.substring(0, (length - 1));
			} else {				 
				 mutatedWord = word.substring(0, pos);
				 mutatedWord += word.substring(pos + 1);
			}					
		}//end else		
		
		return mutatedWord;
	}//end missedCharsTypos()
			
	/**
	 * Method: swap positions of chars within the word
	 */	
	public String transposeCharsTypo(String word) {
		String mutatedWord = null;
		int length = word.length();
		
		if (length == 1) {
			return word;
		} else {
			//get an index at random from the string to mutate
			length = word.length();
			int pos = this.randomGen.randInt(0, length-1);
			logger.debug("random pos: " + pos);
			
			char[] array = null;
			char charToreplace;
			
			if (pos < (length - 1)) {
				logger.debug("pos < (length - 1)");
				array = word.toCharArray();
					
				charToreplace = array[pos];
				array[pos] = array[pos+1];
				array[pos+1] = charToreplace;				
				
				mutatedWord = new String(array);
			} else if (pos == (length - 1)) {
				logger.debug("pos == (length - 1)");
				array = word.toCharArray();
				
				charToreplace = array[pos];
				array[pos] = array[pos-1];
				array[pos-1] = charToreplace;
				
				mutatedWord = new String(array);
			}//end else			
		}//end else	
		
		return mutatedWord;
	}//end transposedCharTypos()	
		
	/**
	 * Method: transpose all characters in a word, like random 
	 * 
	 * Inspired by:
	 *  http://stackoverflow.com/questions/2626640/transposing-and-untransposing-a-string-in-java
	 * 
	 * @param s
	 * @return
	 */
	public String transposeAllCharsTypo(String word) {
	    StringBuilder sb = new StringBuilder();
	    sb.setLength(word.length());
	    
	    for (int i = 0, j = word.length() - 1, x = 0; i <= j; ) {
	        sb.setCharAt(x++, word.charAt(i++));
	        if (i > j) break;
	        sb.setCharAt(x++, word.charAt(j--));
	    }//end for
		    
	    return sb.toString();
	}//end transposeWord()
		
		
	/**
	 * Method: generate double character typos
	 */
	public String doubleCharTypo(String word) {
		String mutatedWord = null;
		int length = word.length();
		
		if (length == 1) {
			return word;
		} else {
			//get an index at random from the string to mutate
			length = word.length();
			int pos = this.randomGen.randInt(0, length-1);
			logger.debug("random pos: " + pos);
			
			//Create the double character
			String doubleChar = "" + word.charAt(pos) + word.charAt(pos);
						
			if (pos == 0) {
				return doubleChar + word.substring(1);
			} else if (pos == (length - 1)) {
				return word.substring(0, (length - 1)) + doubleChar;
			} else {	
				 mutatedWord = word.substring(0, pos) + doubleChar;
				 mutatedWord += word.substring((pos + 1), length);
			}					
		}//end else		
		
		return mutatedWord;
	}//end missedCharsTypos()
		
	
	/**
	 * Method for transposing a random character from a tri-gram 30% of the time
	 */
	public String transposeTriGrams(String word) {
		char[] array = word.toCharArray();

		for (int i = 0; i < word.length() - 2; i++) {
			int j = i+3;
			//choose whether to transpose a character in a tri gram 
			if (randomGen.randomWithBias(0.3)) {
			    int pos = this.randomGen.randInt(i, j-1);
	    
			    if (pos < (word.length() - 1)) {
			    	char charToreplace = array[pos];
			    	array[pos] = array[pos+1];
			    	array[pos+1] = charToreplace;
			    } 	
			}//end if		    
		}//end for
		
		return new String(array);		
	}//end transposeTriGrams()
	
		
	//Test class
	   public static void main( String[] args ) {
	   	RandomGeneratorImpl rg = new RandomGeneratorImpl();
	   	SyntacticMutations typoGen = new SyntacticMutations(rg);
	   	
	   	String word = "Football"; 
	   	
	   	//Test missedCharTypo 
	   	String skipLetterWord = typoGen.missedCharTypo(word);
	   	System.out.println("skipLetterWord: " + skipLetterWord); 
	   	
	   	//test transposeCharsTypo
	   	//String transposeCharsTypo = typoGen.transposeCharsTypo(word);
	   	//System.out.println("transposeCharsTypo: " + transposeCharsTypo); 
	   	
	   	//Test transposeAllCharsTypo
	   	//String transAllWord = typoGen.transposeAllCharsTypo(word);
	   	//System.out.println("transAllWord: " + transAllWord); 
	   	
	   	//test doubleCharTypo
	   	//String trasDoubleChar = typoGen.doubleCharTypo(word);
	   	//System.out.println("trasDoubleChar: " + trasDoubleChar);  
	   	
	   	//test transpose char in tri-grams [target n-Gram algorithm] 
	   	//String trasCharInTriGram = typoGen.doubleCharTypo(word);
	   	//System.out.println("trasCharInTriGram: " + trasCharInTriGram);   	
	   	
	   }//end main	
	    
	   /*****************
	    * Inner EnumClass
	    */
	   public enum MisspellingMutationsType {
	   	MISSED_CHAR("MISSED_CHAR"),
	   	TRANSPOSE_CHAR("TRANSPOSE_CHAR"),
	   	TRANSPOSE_ALL_CHARS("TRANSPOSE_ALL_CHARS"),
	   	DOUBLE_CHAR("DOUBLE_CHAR"),
	   	TRANSPOSE_CHAR_TRI_GRAMS("TRANSPOSE_CHAR_TRI_GRAMS");
	   	
	   	private final String value;
	    	MisspellingMutationsType(String v) {
            value = v;
        }
    	
        public static MisspellingMutationsType fromValue(String v) {
            for (MisspellingMutationsType c: MisspellingMutationsType.values()) {
                if (c.value.equals(v.trim())) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }//end inner class
}//end class