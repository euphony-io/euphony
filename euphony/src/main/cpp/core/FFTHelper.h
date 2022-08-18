#ifndef EUPHONY_FFTHELPER_H
#define EUPHONY_FFTHELPER_H

namespace Euphony {
    class FFTHelper {
    public:
        FFTHelper(const int fftSize, const int sampleRate, const int standardFrequency);
        int getIndexOfStandardFrequency() const;
        int getIndexOfEndFrequency(const int range) const;
        int getIndexOfFrequency(int frequency) const;

        static int getIndexOfStandardFrequency(const int standardFrequency, const int fftSize, const int sampleRate);
        static int getIndexOfEndFrequency(const int range, const int fftSize, const int sampleRate, const int standardFrequency);
        static int getIndexOfFrequency(const int frequency, const int fftSize, const int sampleRate);
        static int getMaxIdxFromSource(const float* fft_source, const int standardFrequency, const int range, const int fftSize, const int sampleRate);

        virtual ~FFTHelper() = default;
    private:
        int fftSize;
        int sampleRate;
        int standardFrequency;
    };
}

#endif //EUPHONY_FFTHELPER_H
