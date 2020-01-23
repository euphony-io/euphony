package euphony.lib.transmitter;

import android.util.Log;

import euphony.lib.util.EuOption;
import euphony.lib.util.PacketErrorDetector;

public class EuCodeMaker extends EuFreqGenerator {
	String mMainString;
	short[] mCodeSource;
	private EuOption mTxOption;
	private int mControlPoint;
	private int mOutsetFrequency;
	private int mDataInterval;

	private EuOption.ModulationType mModulation;
	
	public EuCodeMaker() { }

	public EuCodeMaker(EuOption option) {
		mTxOption = option;
		mControlPoint = option.getControlPoint();
		mModulation = option.getModulationType();
		mDataInterval = option.getDataInterval();
		mOutsetFrequency = option.getOutsetFrequency();
	}

	public short[] assembleData(String data)
    {

    	int[] payload = new int[data.length() + 1];
    	short[] assembledData;
    	int payloadSum = 0;
    	
    	switch(mModulation){
			case ASK: {
				assembledData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.BOTH);
				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
						case '0':
							assembledData = appendRawData(assembledData, getZeroSource());
							break;
						case '1':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint, 0), CrossfadeType.BOTH));
							break;
					}
					payload[i] = ch - '0';
					payloadSum += payload[i];
				}


				int checksum = PacketErrorDetector.makeCheckSum(payloadSum);
				int parity = PacketErrorDetector.makeParellelParity(payload);

				Log.i("euphony_code", "CODE" + data + "CHECKSUM : " + checksum + " PARITY : " + parity);
				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * checksum, 0), CrossfadeType.BOTH));
				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint  + mDataInterval * parity, 0), CrossfadeType.BOTH));

			}
				break;

			case FSK: {
				assembledData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.BOTH);

				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
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
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * (ch - '0'), 0), CrossfadeType.BOTH));
							payload[i] = ch - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * (ch - 'a' + 10), 0), CrossfadeType.BOTH));
							payload[i] = (ch - 'a') + 10;
							break;
					}
					payloadSum += payload[i];
				}


				int checksum = PacketErrorDetector.makeCheckSum(payloadSum);
				int parity = PacketErrorDetector.makeParellelParity(payload);

				Log.i("euphony_code", "CODE" + data + "CHECKSUM : " + checksum + " PARITY : " + parity);
				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * checksum, 0), CrossfadeType.BOTH));
				assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint  + mDataInterval * parity, 0), CrossfadeType.BOTH));

			}	break;
			default:
			case CPFSK: {
				assembledData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.FRONT);
				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
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
							assembledData = appendRawData(assembledData, makeFrequencyWithCP(mControlPoint + mDataInterval * (ch - '0')));
							payload[i] = ch - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							assembledData = appendRawData(assembledData, makeFrequencyWithCP(mControlPoint + mDataInterval * (ch - 'a' + 10)));
							payload[i] = (ch - 'a') + 10;
							break;
					}
					payloadSum += payload[i];
				}

				int checksum = PacketErrorDetector.makeCheckSum(payloadSum);
				int parity = PacketErrorDetector.makeParellelParity(payload);

				Log.i("euphony_code", "CODE" + data + "CHECKSUM : " + checksum + " PARITY : " + parity);
				assembledData = appendRawData(assembledData, makeFrequencyWithCP(mControlPoint + mDataInterval * checksum));
				assembledData = appendRawData(assembledData, applyCrossFade(makeFrequencyWithCP(mControlPoint  + mDataInterval * parity), CrossfadeType.END));

			}
			break;
		}

		return assembledData;
    }

	public short[] assembleLiveData(String data)
	{
		short[] liveStartData = applyCrossFade(makeStaticFrequency(mOutsetFrequency - mDataInterval, 0), CrossfadeType.BOTH);
		short[] startData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.BOTH);
		short[] assembledData = liveStartData.clone();
		int[] payload = new int[data.length() + 1];
		int payloadSum = 0;

		switch(mModulation)
		{
			case ASK:
				for(int i = 0; i < data.length(); i++)
				{
					switch(data.charAt(i))
					{
						case '0':
							assembledData = appendRawData(assembledData, getZeroSource());
							break;
						case '1':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint,0), CrossfadeType.BOTH));
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

			case FSK:
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
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * (ch - '0'), 0), CrossfadeType.BOTH));
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint + mDataInterval * (ch - 'a' + 10), 0), CrossfadeType.BOTH));
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
