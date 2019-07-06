package euphony.lib.util;

public interface COMMON {
	// RX & TX COMMON VARIABLES
	int SAMPLERATE = 44100;
	int FFT_SIZE = 512;
	int DATA_LENGTH = FFT_SIZE * 4;
	int FADE_RANGE = DATA_LENGTH / 16;
	
	double MAX_FREQ = 22050.0;
	int START_FREQ = 18000;
	int CHANNEL = 16;
	int CHANNEL_SPAN = SAMPLERATE / FFT_SIZE;	// Frequency Interval
	int BUNDLE_INTERVAL = CHANNEL_SPAN * CHANNEL;
	
	// RX
	int MAX_REF = 4000;
	int MIN_REF = 50;

	int DEFAULT_REF = 500;	// BASE Reference value
}
