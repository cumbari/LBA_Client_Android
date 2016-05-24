package com.shephertz.cumbari;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shephertz.cumbari.utils.SharedPrefKeys;

public class SeekBarSelectionOfferOrRangeActivity extends BaseActivity
{
    private TextView title;
    private TextView seek_bar_value_label;
    private TextView seek_bar_value;
    private SeekBar seek_bar_changer;
    private Button reset_value;

    private ImageView map_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar);
        initialiseResources();
    }

    private void initialiseResources() {
        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        seek_bar_value_label = (TextView) findViewById(R.id.seek_bar_value_label);
        typeFaceClass.setTypefaceMed(seek_bar_value_label);

        seek_bar_value = (TextView) findViewById(R.id.seek_bar_value);
        typeFaceClass.setTypefaceBold(seek_bar_value);

        seek_bar_changer = (SeekBar) findViewById(R.id.seek_bar_changer);

        reset_value = (Button) findViewById(R.id.reset_value);
        typeFaceClass.setTypefaceMed(reset_value);
        reset_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getIntent().getBooleanExtra("isRange",false)){
                    sharedPreferenceUtil.saveData(SharedPrefKeys.MAX_NUMBER,10);
                    seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10)+""));
                    seek_bar_changer.setProgress(0);
                }else{
                    sharedPreferenceUtil.saveData(SharedPrefKeys.RANGE,5000);
                    seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000)+""));
                    seek_bar_changer.setProgress(4750);
                }
            }
        });

        if(!getIntent().getBooleanExtra("isRange",false)){
            title.setText(getResources().getString(R.string.offers_in_list));
            seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10)+""));
            seek_bar_changer.setMax(40);
            seek_bar_changer.setProgress(sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10)-10);

        }else{
            title.setText(getResources().getString(R.string.range));
            seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000)+""));
            seek_bar_changer.setMax(9750);
            seek_bar_changer.setProgress(sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000)-250);
        }

        seek_bar_changer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!getIntent().getBooleanExtra("isRange",false)){
                    sharedPreferenceUtil.saveData(SharedPrefKeys.MAX_NUMBER,(progress+10));
                    seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,0)+""));
                }else{
                    sharedPreferenceUtil.saveData(SharedPrefKeys.RANGE,(progress+250));
                    seek_bar_value.setText((sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,0)+""));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
