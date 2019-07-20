package euphony.lib.util;

import android.util.Log;

public class PacketErrorDetector {


	private boolean mEvenParity = false;
	

	/*****************************************************
	 *  This function sets EvenParity Bit's Value with parameter (force to set)
	 *   
	 * parameter : 
	 * 		int nParityValue - 0 : Data Bit Value 1's number  is EVEN
	 * 						   1 : Data Bit Value 1's number  is ODD
	 * 			
	 * 			
	 * return : none
	 *****************************************************/
	public void euSetEvenParityState(boolean nParityState){
		mEvenParity = nParityState;
	}

	/*****************************************************
	 * This function returns EvenParity Bit's Value.
	 *  parameter : none
	 *  return type : int
	 *  					   0 : Data Bit Value 1's number  is EVEN
	 * 						   1 : Data Bit Value 1's number  is ODD
	 *****************************************************/
	public boolean euGetEvenParityState(){
		return mEvenParity;
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
	public static boolean checkEvenParity(int[] payload,  int nParityBit){
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
	   public static int makeParellelParity(int[] payLoad){
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
	     
	      Log.i("UHEHE", evenParity1 + " " + evenParity2 + " " + evenParity3 + " " + evenParity4);
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
		Log.v("TCheckSum", "TCheckSum "+ nCheckSum);
		return nCheckSum;
	}
}
