package co.euphony.common;

public class Constants {

	// RX & TX COMMON VARIABLES
	public static final int SAMPLERATE = 44100;
	public static final int HALF_SAMPLERATE = (SAMPLERATE >> 1);
	public static final int FFT_SIZE = 512;

	public static final int CHANNEL = 16;
	public static final int CHANNEL_INTERVAL = SAMPLERATE / FFT_SIZE;	// Frequency Interval

	public static final int STANDARD_FREQ = 18001;
	public static final int START_SIGNAL_FREQ = STANDARD_FREQ - CHANNEL_INTERVAL;

	public static final int BUNDLE_INTERVAL = CHANNEL_INTERVAL * CHANNEL;
	
	// RX
	public static final int MAX_REF = 4000;
	public static final int MIN_REF = 50;

	public static final int DEFAULT_REF = 500;	// BASE Reference value
}
