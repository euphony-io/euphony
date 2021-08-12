#ifndef EUPHONY_PACKET_H
#define EUPHONY_PACKET_H

#include <string>
#include <vector>

using std::string;
using std::vector;

namespace Euphony {
    class Packet {
    public:
        string create(string code);
        void clear();

    private:
        vector<int> payload;
        int checksum;
        int parityCode;
        bool isVerified;
    };
}

#endif //EUPHONY_PACKET_H
