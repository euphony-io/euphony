package euphony.lib.receiver;

public interface PositionDetector {
	void detectSignal(int signal);
	void detectFq(int fq);
}
