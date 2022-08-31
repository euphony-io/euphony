#include "Definitions.h"
#include "base/Base32Exception.h"

using namespace Euphony;

Base32Exception::Base32Exception()
: msg(BASE32_EXCEPTION_MSG), file(""), line(0) {}

Base32Exception::Base32Exception(int _line, const std::string &_file)
: msg(BASE32_EXCEPTION_MSG), file(_file), line(_line) {}

Base32Exception::~Base32Exception() throw() {}

const char* Base32Exception::what() const throw() {
    return msg.c_str();
}

const std::string &Base32Exception::MSG() const {
    return msg;
}

int Base32Exception::LINE() const {
    return line;
}

const std::string &Base32Exception::FILE() const {
    return file;
}

