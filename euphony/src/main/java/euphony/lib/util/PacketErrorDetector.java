package euphony.lib.util;

import android.util.Log;

public class PacketErrorDetector {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	/*****************************************************
	 * This function get all result of error detection algorithms
	 *
	 *  parameter :
	 *  	String payload			- Payload Data
	 *  return type : String
	 *   	result of error detection algorithms
	 *****************************************************/
	public static String makeErrorDetectionCode(String payload)
	{
		int payloadSum = 0;
		int evenParity1 = 0;
		int evenParity2 = 0;
		int evenParity3 = 0;
		int evenParity4 = 0;
		int evenParity;

		for(int i = 0 ; i < payload.length(); i++){
			char ch = payload.charAt(i);
			int ch2i = 0;
			switch(ch) {
				default:
					ch2i = ch - '0';
					break;
				case 'a': case 'b': case 'c':
				case 'd': case 'e': case 'f':
					ch2i = ch - 'a' + 10;
					break;
			}
			evenParity1 += ((0x8 & ch2i) >> 3);
			evenParity2 += ((0x4 & ch2i) >> 2);
			evenParity3 += ((0x2 & ch2i) >> 1);
			evenParity4 += (0x1 & ch2i);

			payloadSum += ch2i;
		}
		payloadSum &= 0xF;
		payloadSum = (~payloadSum + 1) & 0xF;
		evenParity = (evenParity1&0x1)*8+(evenParity2&0x1)*4+(evenParity3&0x1)*2+(evenParity4&0x1);

		return "" + HEX_ARRAY[payloadSum] + HEX_ARRAY[evenParity];
	}


	
	/***************************************************** 
	 * This function checks ParityBit(Even) and judges payload data is reliable
	 *  
	 *  parameter : 
	 *  		int[] Payload			- Payload Data
	 *  		int nParity				- Payrity bit's value
	 *  return type : int
	 *   			TRUE  - Parity Check Result means data is reliable	
	 *   			FALSE - Parity Check Result means data is unreliable
	 *****************************************************/
	public static boolean verifyEvenParity(int[] payload, int nParityBit){
		int ntemp = 0;
		for(int i = 0 ; i < payload.length ; i++){
			ntemp ^= payload[i];
		}
		ntemp ^= nParityBit;
		if(ntemp == 0) // result of Parity Calculation is 0 --> Even Parity Correct!
			return true;
		else {			// result of Parity Calculation is 1 --> Even Parity incorrect!
			return false;
		}
	}

	/*****************************************************
	 * This function makes ParellelParityBit(int[] payLoad) (word : 4bit)
	 *
	 *  parameter :
	 *        int[] payLoad         - Payload Data
	 *  return type : int
	 *            the EvenParity data to transmit
	 *****************************************************/
	public static int makeParallelParity(int payLoad){
		int evenParity1 = ((0x8 & payLoad) >> 3);
		int evenParity2 = ((0x4 & payLoad) >> 2);
		int evenParity3 = ((0x2 & payLoad) >> 1);
		int evenParity4 = (0x1 & payLoad);
		int evenParity;

		evenParity = (evenParity1&0x1)*8+(evenParity2&0x1)*4+(evenParity3&0x1)*2+(evenParity4&0x1);

		return evenParity;
	}

	/***************************************************** 
	    * This function makes ParellelParityBit(int[] payLoad) (word : 4bit)
	    *  
	    *  parameter : 
	    *        int[] payLoad         - Payload Data    
	    *  return type : int
	    *            the EvenParity data to transmit
	    *****************************************************/
	   public static int makeParallelParity(int[] payLoad){
	      int evenParity1 = 0;
	      int evenParity2 = 0;
	      int evenParity3 = 0;
	      int evenParity4 = 0;
	      int evenParity;
	      
	      for(int i = 0 ; i < payLoad.length ; i++){         
	         evenParity1 += ((0x8 & payLoad[i]) >> 3);   
	         evenParity2 += ((0x4 & payLoad[i]) >> 2);
	         evenParity3 += ((0x2 & payLoad[i]) >> 1);
	         evenParity4 += (0x1 & payLoad[i]);
	      }

	      evenParity = (evenParity1&0x1)*8+(evenParity2&0x1)*4+(evenParity3&0x1)*2+(evenParity4&0x1);
	      
	      return evenParity;
	   }

	/*****************************************************
	 * This function makes ParellelParityBit(int[] payLoad) (word : 4bit)
	 *
	 *  parameter :
	 *        int[] payLoad         - Payload Data
	 *  return type : int
	 *            the EvenParity data to transmit
	 *****************************************************/
	public static int makeParallelParity(String payload){
		int evenParity1 = 0;
		int evenParity2 = 0;
		int evenParity3 = 0;
		int evenParity4 = 0;
		int evenParity;

		for(int i = 0 ; i < payload.length(); i++){
			char ch = payload.charAt(i);
			int ch2i = 0;
			switch(ch) {
				default:
					ch2i = ch - '0';
					break;
				case 'a': case 'b': case 'c':
				case 'd': case 'e': case 'f':
					ch2i = ch - 'a' + 10;
					break;
			}
			evenParity1 += ((0x8 & ch2i) >> 3);
			evenParity2 += ((0x4 & ch2i) >> 2);
			evenParity3 += ((0x2 & ch2i) >> 1);
			evenParity4 += (0x1 & ch2i);
		}

		evenParity = (evenParity1&0x1)*8+(evenParity2&0x1)*4+(evenParity3&0x1)*2+(evenParity4&0x1);

		return evenParity;
	}

	/***************************************************** 
	 * This function verify Checksum bit (word : 4bit)
	 *  parameter : 
	 *  			int[] Payload  			- Payload Data
	 *  			int nSizeOfPayload,  	- Payload Data's quantity
	 *  			int nCheckSum			- Checksum bit's value
	 *  return type : boolean
	 *   			true - Checksum is correct	
	 *   			WAIT - Checksum is incorrect
	 *****************************************************/
	public static boolean verifyCheckSum(int[] payLoad,  int checkSum){
		int sumTemp = 0;
		int checkResult = 0;
		for(int i = 0; i < payLoad.length ; i++){  // add all of payload data
			sumTemp += payLoad[i];
		}
		// add checksum data
		sumTemp += checkSum;
		// get 2's Complement and remove carry
		checkResult = ((~sumTemp) + 1) & 0xF;
		if(checkResult == 0x0){
			return true;
		}
		else 
			return false;
	}

	/*****************************************************
	 * This function makes Checksum bit(word : 4bit)
	 *  parameter : int PayloadSum			- Payload Data
	 *  return type : int
	 *   	 the Checksum data to transmit
	 *****************************************************/
	public static int makeCheckSum(int payloadSum){
		// remove carry
		payloadSum &= 0xF;
		// get 2's Complement and make checksum 4bit word
		return (~payloadSum + 1) & 0xF;
	}

	/***************************************************** 
	 * This function makes Checksum bit(word : 4bit)
	 *  parameter : int[] Payload			- Payload Data
	 *  return type : int
	 *   	 the Checksum data to transmit
	 *****************************************************/
	public static int makeCheckSum(int[] payLoad){
		int nSumTemp = 0;
		int nCheckSum = 0; // CheckSum's Initial value is 0
		for(int i = 0 ; i < payLoad.length ; i++){
			nSumTemp += payLoad[i];
		}
		// remove carry
		nSumTemp &= 0xF;
		// get 2's Complement and make checksum 4bit word
		nCheckSum = (~nSumTemp + 1) & 0xF;

		return nCheckSum;
	}

	/*****************************************************
	 * This function makes Checksum bit(word : 4bit)
	 *  parameter : int[] Payload			- Payload Data
	 *  return type : int
	 *   	 the Checksum data to transmit
	 *****************************************************/
	public static int makeCheckSum(String payload){
		int nSumTemp = 0;
		int nCheckSum = 0; // CheckSum's Initial value is 0
		for(int i = 0 ; i < payload.length(); i++){
			char ch = payload.charAt(i);
			switch(ch) {
				default:
					nSumTemp += ch - '0';
					break;
				case 'a': case 'b': case 'c':
				case 'd': case 'e': case 'f':
					nSumTemp += ch - 'a' + 10;
					break;
			}
		}
		// remove carry
		nSumTemp &= 0xF;
		// get 2's Complement and make checksum 4bit word
		nCheckSum = (~nSumTemp + 1) & 0xF;
		Log.v("TCheckSum", "TCheckSum "+ nCheckSum);
		return nCheckSum;
	}
}
