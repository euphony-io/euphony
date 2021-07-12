package co.euphony.tx;

import co.euphony.util.EuOption;
import co.euphony.util.PacketErrorDetector;

public class EuCodeMaker extends EuFreqGenerator {
	private EuOption mTxOption;
	private int mControlPoint;
	private int mOutsetFrequency;
	private int mDataInterval;

	private EuOption.ModulationType mModulation;
	
	public EuCodeMaker() {
		this(new EuOption());
	}

	public EuCodeMaker(EuOption option) {
		setOption(option);
	}

	private void initialize()
	{
		mControlPoint = mTxOption.getControlPoint();
		mModulation = mTxOption.getModulationType();
		mDataInterval = mTxOption.getDataInterval();
		mOutsetFrequency = mTxOption.getOutsetFrequency();
	}

	public void setOption(EuOption option) {
		mTxOption = option;
		initialize();
	}

	public short[] assembleData(String data)
    {
		short[] assembledData = {};

    	switch(mModulation){
			case ASK: {
				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
						case 'S':
							assembledData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.BOTH);
							break;
						case '0':
							assembledData = appendRawData(assembledData, getZeroSource());
							break;
						case '1':
							assembledData = appendRawData(assembledData, applyCrossFade(makeStaticFrequency(mControlPoint, 0), CrossfadeType.BOTH));
							break;
					}
				}
			}
				break;

			case FSK: {
				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
						case 'S':
							assembledData = applyCrossFade(makeStaticFrequency(mOutsetFrequency, 0), CrossfadeType.BOTH);
							break;
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
				}
			}	break;
			default:
			case CPFSK: {
				for (int i = 0; i < data.length(); i++) {
					char ch = data.charAt(i);
					switch (ch) {
						case 'S':
							assembledData = applyCrossFade(makeFrequencyWithCP(mOutsetFrequency), CrossfadeType.FRONT);
							break;
						case 's':
							assembledData = appendRawData(assembledData, makeFrequencyWithCP(mOutsetFrequency));
							break;
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
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							assembledData = appendRawData(assembledData, makeFrequencyWithCP(mControlPoint + mDataInterval * (ch - 'a' + 10)));
							break;
					}
				}
				assembledData = applyCrossFade(assembledData, CrossfadeType.END);
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
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeParallelParity(data.charAt(i) - '0')));
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
					assembledData = appendRawData(assembledData, makeFrequencyWithValue(PacketErrorDetector.makeParallelParity(ch - '0')));
					// final code
					assembledData = appendRawData(assembledData, startData);
				}
				break;
		}

		return assembledData;
	}
}
