package euphony.lib.util;

import android.util.Log;

public class ErrorHandler {
	
	public final boolean BUSY = true;
	public final boolean UNBUSY = false;	
	public final boolean NONE  = false;
	public final boolean EXIST = true;
	
	private boolean mChannelState ;
	private boolean mEvenParity ;
	private int mNoiseRefrence=30;
	private boolean mboiseState;
	
	/*****************************************************
	 *  This function is constructor, sets member variables
	 *   Default member variable value:
	 *  			nChannelState = BUSY;
	 *  			nEvenParity = FALSE;
	 *				nNoiseState = EXIST;
	 * parameter : 
	 * 			int nNosieReference : defines  Noise level Reference
	 * return : none
	 *****************************************************/
	public ErrorHandler(int nNoiseRef){
		mChannelState = BUSY;
		mEvenParity = false;
		mboiseState = EXIST;
		mNoiseRefrence = nNoiseRef;
	}

	/*****************************************************
	 *  This function sets Channel's State with parameter
	 * parameter : int nState
	 * 				BUSY - false	
	 *   			WAIT - true
	 * return : none
	 *****************************************************/
	public void euSetChannelState(boolean nState){
		mChannelState = nState;
	}

	/*****************************************************
	 * This function returns Channel's State.
	 * 
	 *  return type : int nChannelState
	 *   				BUSY - false	
	 *   				WAIT - true
	 *****************************************************/
	public boolean euGetChannelState(){
		return mChannelState;
	}

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
	 * This function scans Start Channel line is busy or not
	 * parameter : 	
	 * 				int[] startChannelBuffer -  Start Channl's Buffer (18kHz)
	 * 				int nChannelBufferSize   -  Start Channl's Buffer Size (Communication Period)
	 * 				int nNumberOfOneBit      -  the data's number which contains 'Bit 1'
	 *  return type : boolean
	 *   				BUSY - false	
	 *   				WAIT - true
	 ****************************************************/	
	public boolean channelScan(int[] startChannelBuffer, int nChannelBufferSize, int nNumberOfOneBit){
		boolean nStateFlag;
		boolean bitDataflag = false;
		int nBitDataCnt = 0;
		// Check Buffer's all Data  (if Bit 1 is Exist??)
		for(int i = 1 ; i < nChannelBufferSize ; i++){ 
			if(startChannelBuffer[i-1] == 0 && startChannelBuffer[i] == 1){  // at rising edge : only works on Bits, not works on Noise
				bitDataflag = true;
				nBitDataCnt++; // for first data count Of Bit 1
			}
			if(startChannelBuffer[i-1] == 1 && startChannelBuffer[i] == 0){  // at falling edge
				bitDataflag = false;
			}
			if(bitDataflag == true && startChannelBuffer[i] == 1){  // On Data Bit loop
				nBitDataCnt++; // for other data count Of Bit 1
			}
		}
		if(nBitDataCnt > nNumberOfOneBit){
			nStateFlag = BUSY;
		}
		else{
			nStateFlag = UNBUSY;
		}
		// set Channels State with nStateFlag
		euSetChannelState(nStateFlag);
		return nStateFlag;
	}

	/***************************************************** 
	 * This function checks ParityBit(Even) and judges payload data is reliable
	 *  
	 *  parameter : 
	 *  		int[] Payload			- Payload Data
	 *  		int nSizeOfPayload		- Payload Data's quantity
	 *  		int nParity				- Payrity bit's value
	 *  return type : int
	 *   			TRUE  - Parity Check Result means data is reliable	
	 *   			FALSE - Parity Check Result means data is unreliable
	 *****************************************************/
	public boolean checkEvenParity(int[] Payload, int nSizeOfPayload,  int nParityBit){
		int ntemp = 0;
		for(int i = 0 ; i < nSizeOfPayload ; i++){
			ntemp ^= Payload[i];
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
	public boolean verifyCheckSum(int[] payLoad,  int checkSum){
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
	public int makeCheckSum(int[] payLoad){
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


	/***************************************************** 
	 * This function checks Noise around device
	 * parameter : int[] freqBuffer - the integer buffer of fft data 
	 *  return type : int
	 *   		NONE - true	
	 *   		EXIST - false
	 *****************************************************/
	//public int checkNoise(int[] freqBuffer, int nSizeOfBuffer,int nNoiseRef){
	public boolean checkNoise(int[] freqBuffer, int nSizeOfBuffer){
		int nNoiseAverage = 0;
		for(int i = 0; i < nSizeOfBuffer; i++){  // from 18k to 21kHz			
			nNoiseAverage += freqBuffer[i];
		}
		nNoiseAverage /= nSizeOfBuffer;
		Log.v("NOISE", "NOISE Average is " + nNoiseAverage);
		if(nNoiseAverage > mNoiseRefrence){
			return EXIST;
		}
		return NONE;
	}
}
