# Charset

## Table of Contents
* [Charset](#Charset)
* [ASCIICharset](#ASCIICharset)
* [DefaultCharset](#DefaultCharset)
* [UTFCharset](#UTFChraset)
  * [UTF8Charset](#UTF8Charset)
  * [UTF16Charset](#UTF16Charset)
  * [UTF32Charset](#UTF32Charset)
* [CharsetAutoSelector](#CharsetAutoSelector)
* [Reference](#Reference)

## Charset
* In euphony, Follow the Charset interface.
* Currently, Euphony library supports 5 types charsets such as ASCIICharset, DefaultCharset, UTF8Charset, UTF16Charset, UTF32Charset.
* In case of the DefaultCharset, Hex Value Itself.
* In case of the ASCIICharset, English & some symbols can be expressed.
* In addition, euphony supports UTF charset.


## ASCIICharset
All of you are familiar with ASCII codes. When you learn C language in your freshman year of college, you must have experienced that each letter is matched with a number. 'a' is stored as 61, 'b' as 62, and 'c' as 63.
ASCII code is stored as a total of 128 characters from 0 to 127. With only 7 bits, everything can be expressed. This means that it is stored in one byte. Everyone knows that a char type is 1 byte. ASCII is expressed with only 7 bits, so it is neatly stored in a char. All you need to know about ASCII code is this.

## DefaultCharset
Hex Value Itself.


## UTFCharset
UTF is a variable-width character encoding used for electronic communication. Defined by the Unicode Standard, the name is derived from Unicode (or Universal Coded Character Set) Transformation Format.

#### Unicode
An industry standard designed to consistently represent and handle all characters in the world on your computer.

#### Configuration
ISO 10646 Character Set, Character Encoding, Character Information Database, Algorithms to Handle Characters, etc.

#### Code Point
The number of tables on which characters correspond.

#### Encoding
Rules for mapping code points to binary data.
Unicode mapping methods include UTF (Unicode Transformation Format) and UCS (Universal Coded Character Set).

#### Example
Text: "A"
Code point: U+0041 (0000000 01000001)
ASCII encoding: 01000001
UTF-8 encoding: 01000001

Text: "가"
Code Point: U+AC00 (1010110 0000000)
ASCII encoding: None
UTF-8 encoding: 11101010 10110000 10000000


### UTF8Charset
* Variable length encoding (1-3 bytes)
* Most used
* High compatibility (superset in ASCII)

### UTF16Charset
* Fixed length encoding (2 bytes)
* Easy to implement (same as bit representation of code points on a 2-byte basis)

### UTF32Charset
* Fixed length encoding (4 bytes)
* Easy to compare with Unicode (it is placed in a 4-byte position)
* Has the advantage of simplifying string processing (the size of one character is fixed to 4 bytes.)

## CharsetAutoSelector
It is a class that returns a character set that shows the shortest encoding result when a desired string is entered.

## Reference
* https://github.com/orgs/euphony-io/discussions/44
* https://github.com/orgs/euphony-io/discussions/58