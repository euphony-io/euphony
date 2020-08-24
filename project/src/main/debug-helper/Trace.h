//
// Created by opener on 20. 8. 24.
//

#ifndef EUPHONY_TRACE_H
#define EUPHONY_TRACE_H


class Trace {
public:
    static void beginSection(const char *format, ...);
    static void endSection();
    static bool isEnabled() { return is_enabled_; }
    static void initialize();

private:
    static bool is_enabled_;
    static bool has_error_been_shown_;
};


#endif //EUPHONY_TRACE_H
