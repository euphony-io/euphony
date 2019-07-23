package euphony.lib.util;

public class EuOption {
    public enum EncodingType {
        ASCII, HEX
    }

    public enum CommunicationMode {
        GENERAL,
        LIVE
    }

    private EncodingType mEncodingType;
    private CommunicationMode mCommunicationMode;

    public EuOption() {}
    public EuOption(EncodingType _encodingType, CommunicationMode _commMode) {
        mEncodingType = _encodingType;
        mCommunicationMode = _commMode;
    }

    public EncodingType getEncodingType() {
        return mEncodingType;
    }

    public void setEncodingType(EncodingType mEncodingType) {
        this.mEncodingType = mEncodingType;
    }

    public CommunicationMode getCommunicationMode() {
        return mCommunicationMode;
    }

    public void setCommunicationMode(CommunicationMode mCommunicationMode) {
        this.mCommunicationMode = mCommunicationMode;
    }
}
