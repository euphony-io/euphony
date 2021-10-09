#ifndef EUPHONY_BASE32EXCEPTION_H
#define EUPHONY_BASE32EXCEPTION_H

#include <exception>
#include <sstream>

namespace Euphony {

    class Base32Exception : public std::exception {
    public:
        Base32Exception();
        Base32Exception(int line, const std::string & file );

        ~Base32Exception() throw();

        const char *what() const throw();
        const std::string & MSG() const;

        int LINE() const;
        const std::string & FILE() const;

    private:
        std::string msg, file;
        int line;
    };
}
#endif //EUPHONY_BASE32EXCEPTION_H
