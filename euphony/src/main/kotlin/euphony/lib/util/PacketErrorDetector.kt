package euphony.lib.util

import android.util.Log

class PacketErrorDetector {

    private var mEvenParity = false

    /*****************************************************
     * This function sets EvenParity Bit's Value with parameter (force to set)
     *
     * parameter :
     * int nParityValue - 0 : Data Bit Value 1's number  is EVEN
     * 1 : Data Bit Value 1's number  is ODD
     *
     *
     * return : none
     */
    fun euSetEvenParityState(nParityState: Boolean) {
        mEvenParity = nParityState
    }

    /*****************************************************
     * This function returns EvenParity Bit's Value.
     * parameter : none
     * return type : int
     * 0 : Data Bit Value 1's number  is EVEN
     * 1 : Data Bit Value 1's number  is ODD
     */
    fun euGetEvenParityState(): Boolean {
        return mEvenParity
    }

    companion object {

        /*****************************************************
         * This function checks ParityBit(Even) and judges payload data is reliable
         *
         * parameter :
         * int[] Payload			- Payload Data
         * int nParity				- Payrity bit's value
         * return type : int
         * TRUE  - Parity Check Result means data is reliable
         * FALSE - Parity Check Result means data is unreliable
         */
        fun checkEvenParity(payload: IntArray, nParityBit: Int): Boolean {
            var ntemp = 0
            for (i in payload.indices) {
                ntemp = ntemp xor payload[i]
            }
            ntemp = ntemp xor nParityBit
            return if (ntemp == 0)
            // result of Parity Calculation is 0 --> Even Parity Correct!
                true
            else {            // result of Parity Calculation is 1 --> Even Parity incorrect!
                false
            }
        }

        /*****************************************************
         * This function makes ParellelParityBit(int[] payLoad) (word : 4bit)
         *
         * parameter :
         * int[] payLoad         - Payload Data
         * return type : int
         * the EvenParity data to transmit
         */
        fun makeParellelParity(payLoad: IntArray): Int {
            var evenParity1 = 0
            var evenParity2 = 0
            var evenParity3 = 0
            var evenParity4 = 0
            val evenParity: Int

            for (i in payLoad.indices) {
                evenParity1 += 0x8 and payLoad[i] shr 3
                evenParity2 += 0x4 and payLoad[i] shr 2
                evenParity3 += 0x2 and payLoad[i] shr 1
                evenParity4 += 0x1 and payLoad[i]
            }

            Log.i("UHEHE", "$evenParity1 $evenParity2 $evenParity3 $evenParity4")
            evenParity = (evenParity1 and 0x1) * 8 + (evenParity2 and 0x1) * 4 + (evenParity3 and 0x1) * 2 + (evenParity4 and 0x1)

            return evenParity
        }

        /*****************************************************
         * This function verify Checksum bit (word : 4bit)
         * parameter :
         * int[] Payload  			- Payload Data
         * int nSizeOfPayload,  	- Payload Data's quantity
         * int nCheckSum			- Checksum bit's value
         * return type : boolean
         * true - Checksum is correct
         * WAIT - Checksum is incorrect
         */
        fun verifyCheckSum(payLoad: IntArray, checkSum: Int): Boolean {
            var sumTemp = 0
            var checkResult = 0
            for (i in payLoad.indices) {  // add all of payload data
                sumTemp += payLoad[i]
            }
            // add checksum data
            sumTemp += checkSum
            // get 2's Complement and remove carry
            checkResult = sumTemp.inv() + 1 and 0xF
            return if (checkResult == 0x0) {
                true
            } else
                false
        }

        /*****************************************************
         * This function makes Checksum bit(word : 4bit)
         * parameter : int[] Payload			- Payload Data
         * int nSizeOfPayload,  	- Payload Data's quantity
         * int nCheckSum			- Checksum bit's value
         * return type : int
         * the Checksum data to transmit
         */
        fun makeCheckSum(payLoad: IntArray): Int {
            var nSumTemp = 0
            var nCheckSum = 0 // CheckSum's Initial value is 0
            for (i in payLoad.indices) {
                nSumTemp += payLoad[i]
            }
            // remove carry
            nSumTemp = nSumTemp and 0xF
            // get 2's Complement and make checksum 4bit word
            nCheckSum = nSumTemp.inv() + 1 and 0xF
            Log.v("TCheckSum", "TCheckSum $nCheckSum")
            return nCheckSum
        }
    }
}