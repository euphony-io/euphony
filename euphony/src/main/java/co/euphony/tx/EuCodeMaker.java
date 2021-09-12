package co.euphony.tx;

import android.util.Log;
import co.euphony.util.PacketErrorDetector;

public class EuCodeMaker extends EuFreqGenerator {
	String mMainString;
	short[] mCodeSource;
	public int START_BIT = getFreqBasePoint() - getFreqSpan();

	public static enum CHANNEL {
		SINGLE, MULTI, EXINGLE
	}
	
	private CHANNEL mChannelMode = CHANNEL.EXINGLE;
	
	public EuCodeMaker() 
	{ }
	
	public EuCodeMaker(CHANNEL channelMode)
	{
		mChannelMode = channelMode;
	}
	

	public short[] assembleData(String data)
    {
    	short[] assembledData = applyCrossFade(makeStaticFrequency(START_BIT, 0));
    	int[] payload = new int[data.length() + 1];
    	int payloadSum = 0;
    	
    	switch(mChannelMode)
    	{
    	case SINGLE:
    		for(int i = 0; i < data.length(); i++)
        	{
        		switch(data.charAt(i))
        		{
        		case '0':
        			assembledData = appendRawData(assembledData, getZeroSource());
        			break;
        		case '1':
        			assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint(),0)));
        			break;
        		}
        	}
    		break;
    		
    	case MULTI:
    		for(int i = 0; i < data.length(); i++)
    		{
    			switch(data.charAt(i))
    			{
    			case '0':
    				break;
    			case '1':
    				assembledData = mixingRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan()*(i+1), 0)));
    				break;
    			}
    		}
    		break;
    		
    	case EXINGLE:
    		for(int i = 0; i < data.length(); i++){
    			char ch = data.charAt(i);
    			switch(ch)
    			{
    			case '0':
    			case '1': 
    			case '2': 
    			case '3': 
    			case '4': 
    			case '5': 
    			case '6': 
    			case '7': 
    			case '8': 
    			case '9': 
    				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - '0'), 0)));
    				break;
    			case 'a': 
    			case 'b': 
    			case 'c': 
    			case 'd': 
    			case 'e': 
    			case 'f': 
    				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - 'a' + 10), 0)));
    				break;
    			}
    		}
    		break;
    	}
    	
    	for(int i = 0; i < data.length(); i++){
    		switch(data.charAt(i))
    		{
    		case '0': case '1': 
			case '2': case '3': 
			case '4': case '5': 
			case '6': case '7': 
			case '8': case '9':
				payload[i] = data.charAt(i) - '0';
				break;
			case 'a': case 'b': 
			case 'c': case 'd': 
			case 'e': case 'f': 
				payload[i] = (data.charAt(i) - 'a') + 10;
				break;
    		}
			payloadSum += payload[i];
    	}
    	int checksum = PacketErrorDetector.makeCheckSum(payloadSum);
    	int parity = PacketErrorDetector.makeParellelParity(payload);
    	
    	Log.i("euphony_code", "CODE" + data + "CHECKSUM : " + checksum + " PARITY : " + parity);
    	assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * checksum, 0)));
    	assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * parity, 0)));

		return assembledData;
    }

	public short[] assembleLiveData(String data)
	{
		short[] liveStartData = applyCrossFade(makeStaticFrequency(START_BIT - getFreqSpan(), 0));
		short[] startData = applyCrossFade(makeStaticFrequency(START_BIT, 0));
		short[] assembledData = liveStartData.clone();
		int[] payload = new int[data.length() + 1];
		int payloadSum = 0;

		switch(mChannelMode)
		{
			case SINGLE:
				for(int i = 0; i < data.length(); i++)
				{
					switch(data.charAt(i))
					{
						case '0':
							assembledData = appendRawData(assembledData, getZeroSource());
							break;
						case '1':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint(),0)));
							break;
					}
					// checksum
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeCheckSum(data.charAt(i) - '0')));
					// parity check
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeParellelParity(data.charAt(i) - '0')));
					// final code
					assembledData = appendRawData(assembledData, startData);
				}
				break;

			case MULTI:
				for(int i = 0; i < data.length(); i++)
				{
					switch(data.charAt(i))
					{
						case '0':
							break;
						case '1':
							assembledData = mixingRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan()*(i+1), 0)));
							break;
					}
					// checksum
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeCheckSum(data.charAt(i) - '0')));
					// parity check
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeParellelParity(data.charAt(i) - '0')));
					// final code
					assembledData = appendRawData(assembledData, startData);
				}

				break;

			case EXINGLE:
				for(int i = 0; i < data.length(); i++){
					char ch = data.charAt(i);
					switch(ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - '0'), 0)));
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - 'a' + 10), 0)));
							break;
					}
					// checksum
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeCheckSum(ch - '0')));
					// parity check
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeParellelParity(ch - '0')));
					// final code
					assembledData = appendRawData(assembledData, startData);
				}
				break;
		}

		return assembledData;
	}
}
