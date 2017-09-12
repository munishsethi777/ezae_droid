package in.learntech.rights.utils.seekbar;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Map;

public class CustomSeekBar {
    int textColor;
    Context mContext;
    LinearLayout mSeekLin;
    SeekBar mSeekBar;
    ArrayMap<Integer,String> itemsMap;
    public CustomSeekBar(Context context, ArrayMap<Integer,String> itemsMap, int textColor) {
        this.mContext = context;
        this.textColor = textColor;
        this.itemsMap = itemsMap;
    }

    public void addSeekBar(LinearLayout parent) {

        if (parent instanceof LinearLayout) {

            parent.setOrientation(LinearLayout.VERTICAL);
            mSeekBar = new SeekBar(mContext);
            mSeekBar.setMax(itemsMap.size()-1);

            // Add LinearLayout for labels below SeekBar
            mSeekLin = new LinearLayout(mContext);
            mSeekLin.setOrientation(LinearLayout.HORIZONTAL);
            mSeekLin.setPadding(10, 0, 10, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(35, 10, 35, 0);
            mSeekLin.setLayoutParams(params);


            addLabelsBelowSeekBar();
            parent.addView(mSeekBar);
            parent.addView(mSeekLin);

        } else {
            Log.e("Seekbar", " Parent is not a LinearLayout");
        }

    }

    private void addLabelsBelowSeekBar() {
        for(Map.Entry<Integer,String> entry : itemsMap.entrySet()){
            TextView textView = new TextView(mContext);
            textView.setId(entry.getKey());
            textView.setText(String.valueOf(entry.getValue()));
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.LEFT);
            mSeekLin.addView(textView);
            if(itemsMap.indexOfKey(entry.getKey()) == itemsMap.size()-1){
                textView.setLayoutParams(getLayoutParams(0.0f));
            }else{
                textView.setLayoutParams(getLayoutParams(1.0f));
            }
         }

    }

    LinearLayout.LayoutParams getLayoutParams(float weight) {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight);
    }
}
