package euphony.lib.util;

public class COMMON {
	// RX & TX COMMON VARIABLES
	public final static int SAMPLERATE = 44100;
	public final static int FFT_SIZE = 512;
	
	public final static double MAX_FREQ = 22050.0;
	public final static int START_FREQ = 18000;
	public final static int CHANNEL = 16;
	public final static int FREQ_SPAN = SAMPLERATE / FFT_SIZE;	// Frequency Interval
	
	// RX
	public final static int MAX_REF = 4000;
	public final static int MIN_REF = 50;

	public final static int DEFAULT_REF = 500;	// BASE Reference value
}
