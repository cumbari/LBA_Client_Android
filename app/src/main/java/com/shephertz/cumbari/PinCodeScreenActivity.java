package com.moblyo.market;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class PinCodeScreenActivity extends BaseActivity {

    private TextView pincode_time_message;
    private TextView pincode;
    private TextView pincode_coupon_title;
    private TextView pincode_coupon_street;
    private Handler handler;
    private Runnable runnable;
    private int counterMin = 5;
    private int counterSec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code_screen);

        String title = getIntent().getStringExtra("couponTitle");
        String store = getIntent().getStringExtra("Street");
        String pin = getIntent().getStringExtra("pin");
        if(pin == null){
            pin = "1234";
        }

        pincode_time_message = (TextView) findViewById(R.id.pincode_time_message);
        typeFaceClass.setTypefaceBold(pincode_time_message);

        pincode = (TextView) findViewById(R.id.pincode);
        typeFaceClass.setTypefaceBold(pincode);
        pincode.setText(pin);

        pincode_coupon_title = (TextView) findViewById(R.id.pincode_coupon_title);
        typeFaceClass.setTypefaceMed(pincode_coupon_title);
        pincode_coupon_title.setText(title);

        pincode_coupon_street = (TextView) findViewById(R.id.pincode_coupon_street);
        typeFaceClass.setTypefaceNormal(pincode_coupon_street);
        pincode_coupon_street.setText(store);

        pincode_time_message.setText(appUtility.getTimerCountDownValue(PinCodeScreenActivity.this,counterMin,counterSec));
        startTimerForPinCode();
    }

    private void startTimerForPinCode() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }

        runnable = new Runnable(){
            public void run() {
                try{
                    if(counterSec == 0 && counterMin == 0){
                        moveToEndScreen();
                    }else if(counterSec == 0){
                        counterSec = 59;
                        --counterMin;
                    }else{
                        --counterSec;
                    }

                    pincode_time_message.setText(appUtility.getTimerCountDownValue(PinCodeScreenActivity.this,counterMin,counterSec));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (handler != null)
                    handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable,1000);
    }

    private void moveToEndScreen(){
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        finish();
        Intent intent = new Intent(PinCodeScreenActivity.this, EndScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveToEndScreen();
    }
}
