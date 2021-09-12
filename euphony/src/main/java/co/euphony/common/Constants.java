package co.euphony.common;

public class Constants {

	// RX & TX COMMON VARIABLES
	public static final int SAMPLERATE = 44100;
	public static final int FFT_SIZE = 512;
	public static final int DATA_LENGTH = FFT_SIZE * 4;
	public static final int FADE_RANGE = DATA_LENGTH / 16;

	public static final double MAX_FREQ = 22050.0;
	public static final int START_FREQ = 18000;
	public static final int CHANNEL = 16;
	public static final int CHANNEL_SPAN = SAMPLERATE / FFT_SIZE;	// Frequency Interval
	public static final int BUNDLE_INTERVAL = CHANNEL_SPAN * CHANNEL;
	
	// RX
	public static final int MAX_REF = 4000;
	public static final int MIN_REF = 50;

	public static final int DEFAULT_REF = 500;	// BASE Reference value
}
