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

    HexVector asciiCharset = ASCIICharset().encode(src);
    HexVector defaultCharset = DefaultCharset().encode(src);
    HexVector utf8Charset = UTF8Charset().encode(src);
    HexVector utf16Charset = UTF16Charset().encode(src);
    HexVector utf32Charset = UTF32Charset().encode(src);

    HexVector results[] = {defaultCharset, asciiCharset, utf8Charset, utf16Charset, utf32Charset};

    std::sort(results, results+5, [](HexVector& left, HexVector& right) {
        return left.getSize() < right.getSize();
    });
    
    return results[0];
}