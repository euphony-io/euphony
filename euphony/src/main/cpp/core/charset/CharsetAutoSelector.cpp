#include "CharsetAutoSelector.h"
#include "ASCIICharset.h"
#include "DefaultCharset.h"
#include "UTF8Charset.h"
#include "UTF16Charset.h"
#include "UTF32Charset.h"
#include <sstream>
#include <iomanip>
#include <iostream>
#include <algorithm>

using namespace Euphony;

HexVector CharsetAutoSelector::select(std::string src) {

    HexVector asciiResult = ASCIICharset().encode(src);
    HexVector defaultResult = DefaultCharset().encode(src);
    HexVector utf8Result = UTF8Charset().encode(src);
    HexVector utf16Result = UTF16Charset().encode(src);
    HexVector utf32Result = UTF32Charset().encode(src);

    HexVector results[] = {asciiResult, defaultResult, utf8Result, utf16Result, utf32Result};

    std::sort(results, results+5, [](HexVector& left, HexVector& right) {
        return left.getSize() < right.getSize();
    });
    
    return results[0];
}