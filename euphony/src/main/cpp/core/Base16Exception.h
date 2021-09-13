#ifndef EUPHONY_BASE16EXCEPTION_H
#define EUPHONY_BASE16EXCEPTION_H

#include <exception>
#include <sstream>

namespace Euphony {

    class Base16Exception : public std::exception {
    public:
        Base16Exception();
        Base16Exception(int line, const std::string & file );

        ~Base16Exception() throw();

        const char *what() const throw();
        const std::string & MSG() const;

        int LINE() const;
        const std::string & FILE() const;

    private:
        std::string msg, file;
        int line;
    };
}
#endif //EUPHONY_BASE16EXCEPTION_H
