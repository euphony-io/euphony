#ifndef EUPHONY_MODEM_H
#define EUPHONY_MODEM_H

#include <string>
using std::string;
using std::vector;
using std::shared_ptr;

namespace Euphony {
    class Wave;

    class Modem {
    public:
        virtual vector<shared_ptr<Wave>> modulate(string code) = 0;
        virtual int demodulate(const float* source, const int size) = 0;
        virtual ~Modem() = default;
    };
}

#endif //EUPHONY_MODEM_H
