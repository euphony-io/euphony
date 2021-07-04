package co.euphony.common;

import java.util.Stack;

import co.euphony.util.PacketErrorDetector;

public class Packet {

    int[] payload;
    int checksum;
    int parityCode;
    boolean isVerified;

    Stack<Integer> packetStack = new Stack<>();

    public Packet() {
        clear();
    }

    public void push(int data) {
        packetStack.push(data);
    }

    public void clear() {
        packetStack.clear();
        payload = null;
        checksum = -1;
        parityCode = -1;
        isVerified = false;
    }

    public boolean build() {
        parityCode = packetStack.pop();
        checksum = packetStack.pop();

        payload = new int[packetStack.size()];
        for(int i = payload.length - 1; i >= 0; i--) {
            payload[i] = packetStack.pop();
        }

        boolean isChecksumOK = PacketErrorDetector.verifyCheckSum(payload, checksum);
        boolean isParityCodeOK = PacketErrorDetector.verifyEvenParity(payload, parityCode);

        isVerified = isChecksumOK && isParityCodeOK;
        return isVerified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public int[] getPayload() {
        return payload;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("S");
        for(int val : payload) {
            ret.append(getInt2HexString(val));
        }
        ret.append(getInt2HexString(checksum));
        ret.append(getInt2HexString(parityCode));

        return ret.toString();
    }

    private String getInt2HexString(int val) {
        if(isMoreThan0xA(val))
            return String.valueOf((char)('a' + (val - 10)));
        else
            return String.valueOf(val);
    }

    private boolean isMoreThan0xA(int hex) { return hex > 9; }
}
