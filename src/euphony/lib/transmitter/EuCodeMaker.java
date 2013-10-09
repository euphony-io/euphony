package euphony.lib.transmitter;

import euphony.lib.util.ErrorHandler;

public class EuCodeMaker extends EuFreqGenerator {
	String mMainString;
	short[] mCodeSource;
	ErrorHandler mErrorHandler = new ErrorHandler(50);

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
    	short[] assembledData = euMakeFrequency(18000);
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
        			assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeFrequency(getFreqBasePoint())));
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
    				assembledData = euMixingRawData(assembledData, euApplyCrossFade(euMakeFrequency(getFreqBasePoint() + getFreqSpan()*(i+1))));
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
    				assembledData = euAppendRawData(assembledData, getZeroSource());
    				break;
    			case '1': 
    			case '2': 
    			case '3': 
    			case '4': 
    			case '5': 
    			case '6': 
    			case '7': 
    			case '8': 
    			case '9': 
    				assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeFrequency(getFreqBasePoint() + getFreqSpan() * (ch - '0'))));
    				break;
    			case 'a': 
    			case 'b': 
    			case 'c': 
    			case 'd': 
    			case 'e': 
    			case 'f': 
    				assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeFrequency(getFreqBasePoint() + getFreqSpan() * (ch - 'a' + 10))));
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
			case 'a': case 'b': 
			case 'c': case 'd': 
			case 'e': case 'f': 
				payload[i] = data.charAt(i) - 'a';
    		}
    	}
    	int checksum = mErrorHandler.makeCheckSum(payload);
    	
    	assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeFrequency(getFreqBasePoint() + getFreqSpan() * checksum)));
    	return euApplyCrossFade(assembledData);
    }
	
}
