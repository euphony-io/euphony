#include "../Definitions.h"
#include "../Base16Exception.h"

using namespace Euphony;

Base16Exception::Base16Exception()
: msg(BASE16_EXCEPTION_MSG), file(""), line(0) {}

Base16Exception::Base16Exception(int _line, const std::string &_file)
: msg(BASE16_EXCEPTION_MSG), file(_file), line(_line) {}

Base16Exception::~Base16Exception() throw() {}

const char* Base16Exception::what() const throw() {
    return msg.c_str();
}

const std::string &Base16Exception::MSG() const {
    return msg;
}

int Base16Exception::LINE() const {
    return line;
}

const std::string &Base16Exception::FILE() const {
    return file;
}

