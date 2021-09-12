package co.euphony.util;

public class EuOption {
    public enum CodingType {
        BASE2, BASE16
    }

    public enum ModeType {
        DEFAULT,
        EUPI,
        DETECT
    }

    public enum ModulationType {
        FSK,
        /*
        TODO: v0.7.1.6 had ASK feature. but v0.8 has to create it.
        ASK,
         */
        /*
        TODO: Rearchitecturing necessary because the CPFSK modulation type has some glitch sound.
        CPFSK
         */
    }

    private CodingType mCodingType;
    private ModeType mModeType;
    private ModulationType mModulationType;

    public EuOption(CodingType codingType, ModeType modeType, ModulationType modulationType) {
        mCodingType = codingType;
        mModeType = modeType;
        mModulationType = modulationType;
    }

    public CodingType getCodingType() {
        return mCodingType;
    }

    public void setCodingType(CodingType mCodingType) {
        this.mCodingType = mCodingType;
    }

    public ModeType getMode() {
        return mModeType;
    }

    public void setMode(ModeType mModeType) {
        this.mModeType = mModeType;
    }

    public ModulationType getModulationType() {
        return mModulationType;
    }

    public void setModulationType(ModulationType mModulationType) {
        this.mModulationType = mModulationType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CodingType codingType;
        private ModeType commMode;
        private ModulationType modulationType;

        public Builder encodingWith(CodingType type) {
            codingType = type;
            return this;
        }

        public Builder modeWith(ModeType mode) {
            commMode = mode;
            return this;
        }

        public Builder modulationWith(ModulationType type) {
            modulationType = type;
            return this;
        }

        public EuOption build() {
            return new EuOption(codingType, commMode, modulationType);
        }
    }
}
