#include "Definitions.h"
#include "base/Base64Exception.h"

using namespace Euphony;

Base64Exception::Base64Exception()
: msg(BASE64_EXCEPTION_MSG), file(""), line(0) {}

Base64Exception::Base64Exception(int _line, const std::string &_file)
: msg(BASE64_EXCEPTION_MSG), file(_file), line(_line) {}

Base64Exception::~Base64Exception() throw() {}

const char* Base64Exception::what() const throw() {
    return msg.c_str();
}

const std::string &Base64Exception::MSG() const {
    return msg;
}

int Base64Exception::LINE() const {
    return line;
}

const std::string &Base64Exception::FILE() const {
    return file;
}

