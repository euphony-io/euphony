package euphony.lib.util;

import android.util.Log;

public class PacketErrorDetector {
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
	 *  parameter : int[] Payload			- Payload Data
	 *  			int nSizeOfPayload,  	- Payload Data's quantity
	 *  			int nCheckSum			- Checksum bit's value
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
