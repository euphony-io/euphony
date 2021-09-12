#include "../HexVector.h"
#include <string>
#include <sstream>

using namespace Euphony;

HexVector::HexVector(int size) {
    hexSource.reserve(size);
}

HexVector::HexVector(const HexVector &copy) {
    setHexSource(copy.getHexSource());
}

HexVector::HexVector(const std::string& hexString) {
    for(char c : hexString) {
        switch(c) {
            case '0': case '1': case '2':
            case '3': case '4': case '5':
            case '6': case '7': case '8':
            case '9': default:
                hexSource.push_back(c - '0');
                break;
            case 'a': case 'b': case 'c':
            case 'd': case 'e': case 'f':
                hexSource.push_back(10 + c - 'a');
                break;
        }
    }
}

HexVector::HexVector(const std::vector<u_int8_t> &hexVectorCopy) {
    setHexSource(hexVectorCopy);
}


HexVector::~HexVector() {
    hexSource.clear();
}

void HexVector::pushBack(u_int8_t hexByte) {
    if(hexByte <= 0x0f) {
        hexSource.push_back(hexByte);
    } else {
        hexSource.push_back((hexByte >> 4));
        hexSource.push_back(hexByte & 0x0f);
    }
}

void HexVector::popBack() {
    hexSource.pop_back();
}

std::string HexVector::toString() const{
    std::stringstream result;

    for(auto data : hexSource) {
        result << std::hex << (int) data;
    }

    return result.str();
}

const std::vector<u_int8_t> &HexVector::getHexSource() const {
    return hexSource;
}

void HexVector::setHexSource(const std::vector<u_int8_t> &hexSrc) {
    this->clear();
    for(u_int8_t hex : hexSrc) {
        this->pushBack(hex);
    }
}

int HexVector::getSize() const {
    return hexSource.size();
}

std::__wrap_iter<std::vector<unsigned char, std::allocator<unsigned char>>::const_pointer>
HexVector::begin() const{
    return hexSource.begin();
}

std::__wrap_iter<std::vector<unsigned char, std::allocator<unsigned char>>::const_pointer>
HexVector::end() const{
    return hexSource.end();
}

void HexVector::clear(){
    hexSource.clear();
}
