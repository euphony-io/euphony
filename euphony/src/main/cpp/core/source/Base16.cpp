#include "../Base16.h"
#include <sstream>
#include <iomanip>

string Euphony::Base16::encode(string src) {
    std::ostringstream result;
    result << std::setw(2) << std::setfill('0') << std::hex;
    std::copy(src.begin(), src.end(), std::ostream_iterator<unsigned int>(result, ""));
    return result.str();
}

string Euphony::Base16::decode(string src) {
    string result = "";
    for(int i = 0; i < src.length() - 1; i+=2) {
        string c = src.substr(i, 2);
        result.push_back((char) (int) strtol(c.c_str(), nullptr, 16));
    }

    return result;
}

int Euphony::Base16::convertChar2Int(char source) {
    switch(source) {
        case '0': case '1': case '2':
        case '3': case '4': case '5':
        case '6': case '7': case '8':
        case '9':
            return source - '0';
        case 'a': case 'b': case 'c':
        case 'd': case 'e': case 'f':
            return source - 'a' + 10;
        default:
            throw Base16Exception();
    }

    return -1;
}

