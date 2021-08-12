#ifndef EUPHONY_PACKETERRORDETECTOR_H
#define EUPHONY_PACKETERRORDETECTOR_H

#include <vector>
#include <string>
using std::string;
using std::vector;

namespace Euphony {

    class PacketErrorDetector {
    public:
        static string makeParityAndChecksum(vector<int> payload);
    };
}
#endif //EUPHONY_PACKETERRORDETECTOR_H
