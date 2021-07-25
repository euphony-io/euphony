#include "../FFTModel.h"
#include "../Definitions.h"

void Euphony::FFTModel::initialize(int fftSize) {
    initialize(fftSize, kSampleRate);
}