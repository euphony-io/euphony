#ifndef EUPHONY_BASE64EXCEPTION_H
#define EUPHONY_BASE64EXCEPTION_H

#include <exception>
#include <sstream>

namespace Euphony {

    class Base64Exception : public std::exception {
    public:
        Base64Exception();
        Base64Exception(int line, const std::string & file );

        ~Base64Exception() throw();

        const char *what() const throw();
        const std::string & MSG() const;

        int LINE() const;
        const std::string & FILE() const;

    private:
        std::string msg, file;
        int line;
    };
}
#endif //EUPHONY_BASE64EXCEPTION_H
