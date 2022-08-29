package co.euphony.sample.fragments.rx;

import static java.lang.String.format;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import co.euphony.sample.R;

class EuPIRxViewMaker {
    Integer mFrequency = 0;
    Integer mKeyDownCount = 0;
    Integer mKeyPressedCount = 0;
    Integer mKeyUpCount = 0;
    String mLabel = "";

    TextView mLabelView = null;
    TextView mCountView = null;

    public EuPIRxViewMaker(int frequency, Context context) {
        mFrequency = frequency;
        mLabel = format(Locale.getDefault(), "EuPI %d", frequency);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        mLabelView = new TextView(context);
        mLabelView.setLayoutParams(lParams);
        mLabelView.setTextSize(30);
        mLabelView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mLabelView.setText(mLabel);

        mCountView = new TextView(context);
        mCountView.setLayoutParams(lParams);
        mCountView.setTextSize(25);
        mCountView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mCountView.setTextColor(context.getResources().getColor(R.color.purple_500));
        mCountView.setText("0\t\t\t0\t\t\t0");
    }

    public void incrementKeyDownCount() {
        mKeyDownCount++;
        mCountView.setText(format(Locale.getDefault(), "%d\t\t\t%d\t\t\t%d", mKeyDownCount, mKeyPressedCount, mKeyUpCount));
    }

    public void incrementKeyPressedCount() {
        mKeyPressedCount++;
        mCountView.setText(format(Locale.getDefault(), "%d\t\t\t%d\t\t\t%d", mKeyDownCount, mKeyPressedCount, mKeyUpCount));
    }

    public void incrementKeyUpCount() {
        mKeyUpCount++;
        mCountView.setText(format(Locale.getDefault(), "%d\t\t\t%d\t\t\t%d", mKeyDownCount, mKeyPressedCount, mKeyUpCount));
    }

    public TextView getCountView() {
        return mCountView;
    }
    public TextView getLabelView() { return mLabelView; }
}
