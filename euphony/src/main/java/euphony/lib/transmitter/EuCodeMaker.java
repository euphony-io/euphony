package euphony.lib.transmitter;

import android.util.Log;
import euphony.lib.util.PacketErrorDetector;

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
	
	public short[] euAssembleData(String data)
	{
		return euAssembleData(data, 1);
	}
	
	public short[] euAssembleData(String data, int count)
    {
    	short[] assembledData = euApplyCrossFade(euMakeStaticFrequency(START_BIT, 0));
    	int[] payload = new int[data.length() + 1];
    	
    	switch(mChannelMode)
    	{
    	case SINGLE:
    		for(int i = 0; i < data.length(); i++)
        	{
        		switch(data.charAt(i))
        		{
        		case '0':
        			assembledData = euAppendRawData(assembledData, getZeroSource());
        			break;
        		case '1':
        			assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint(),0)));
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
    				assembledData = euMixingRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint() + getFreqSpan()*(i+1), 0)));
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
    				assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - '0'), 0)));
    				break;
    			case 'a': 
    			case 'b': 
    			case 'c': 
    			case 'd': 
    			case 'e': 
    			case 'f': 
    				assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint() + getFreqSpan() * (ch - 'a' + 10), 0)));
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
    	}
    	int checksum = PacketErrorDetector.makeCheckSum(payload);
    	int parity = PacketErrorDetector.makeParellelParity(payload);
    	
    	Log.i("UHEHE", "CHECKSUM : " + checksum + " PARITY : " + parity);
    	assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint() + getFreqSpan() * checksum, 0)));
    	assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(getFreqBasePoint() + getFreqSpan() * parity, 0)));
    	
    	short[] result = assembledData;
		for(int i = 1; i < count; i++)
			result = euAppendRawData(result, assembledData);
    	
    	result = euAppendRawData(result, euApplyCrossFade(euMakeStaticFrequency(START_BIT, 0)));
    	return result;
    }
	
}
