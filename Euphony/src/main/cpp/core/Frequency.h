//
// Created by designe on 20. 9. 16.
//

#ifndef EUPHONY_FREQUENCY_H
#define EUPHONY_FREQUENCY_H

#include <vector>

constexpr int32_t kSampleRate = 48000;
constexpr double kPi = M_PI;
constexpr double kTwoPi = kPi * 2.0;

namespace Euphony {

    enum CrossfadeType {
        FRONT, END, BOTH
    };

    class Frequency {
    private:
        int mHz;
        int mSize;
        std::vector<float> mSource;
        float mPhase = 0.0;
        std::atomic<double> mPhaseIncrement{0.0};

        void createFrequency(int hz, int size);

    public:
        Frequency(int hz, int size);

        Frequency();

        int getHz() const;

        void setHz(int hz);

        int getSize() const;

        void setSize(int size);

        const std::vector<float> &getSource() const;

        void setSource(const std::vector<float> &source);

        void applyCrossfade(CrossfadeType);
    };

}

#endif //EUPHONY_FREQUENCY_H
