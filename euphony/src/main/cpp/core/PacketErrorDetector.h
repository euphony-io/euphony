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
        static string makeParityAndChecksum(string payload);
        static int makeChecksum(vector<int> payload);
        static int makeParallelParity(vector<int> payload);
        static bool verifyChecksum(vector<int> payload, int checksum);
        static bool verifyParallelParity(vector<int> payload, int parity);
    };
}
#endif //EUPHONY_PACKETERRORDETECTOR_H
