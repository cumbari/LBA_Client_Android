package com.moblyo.market;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class EndScreenActivity extends AppCompatActivity {

    private RelativeLayout end_screen_main_layout;
    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        end_screen_main_layout = (RelativeLayout) findViewById(R.id.end_screen_main_layout);
        end_screen_main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(handler != null){
                    handler.removeCallbacks(runnable);
                }
                moveToHomeScreen();
            }
        });
        placeHandlerToFinishActivity();
    }

    private void placeHandlerToFinishActivity() {
        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {
                moveToHomeScreen();
            }
        };
        handler.postDelayed(runnable, 10*1000);
    }

    private void moveToHomeScreen(){
        finish();
        Intent intent = new Intent(EndScreenActivity.this, HomeScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(handler != null){
            handler.removeCallbacks(runnable);
        }
        moveToHomeScreen();
    }
}
