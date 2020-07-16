//
// Created by desig on 2020-07-16.
//

#include "kiss_fftr.h"
#include <malloc.h>
#include <stdlib.h>
#include <math.h>

#define MAX_SHORT 32767.0f

static inline float scale( kiss_fft_scalar val )
{
    if( val < 0 )
        return val * ( 1 / 32768.0f );
    else
        return val * ( 1 / 32767.0f );
}

struct KissFFT
{
    kiss_fftr_cfg config;
    kiss_fft_cpx* spectrum;
    int numSamples;
};