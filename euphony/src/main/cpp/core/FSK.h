#ifndef EUPHONY_FSK_H
#define EUPHONY_FSK_H

#include "Modem.h"
#include "Wave.h"
#include <string>
#include <vector>
using std::string;
using std::vector;
using std::shared_ptr;
using namespace Euphony;

namespace Euphony {
    class FSK : public Modem {
    public:
        vector<shared_ptr<Wave>> modulate(string code);
        int demodulate(const float* source, const int size);
    private:
        const int getStartFreqIdx() const;
        const int getEndFreqIdx() const;
    };
}
#endif //EUPHONY_FSK_H
