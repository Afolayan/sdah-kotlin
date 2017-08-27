package com.jcedar.sdahyoruba.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.app.SdahYoruba;
import com.jcedar.sdahyoruba.helper.AppHelper;

/**
 * Created by OLUWAPHEMMY on 2/12/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(3000);
            AppHelper.loadHymnDataFromFile(SplashActivity.this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SdahYoruba.getInstance().trackScreenView("Splash");
//        AppHelper.loadHymnDataFromFile(SplashActivity.this);
        Intent intent = new Intent(this, NewDashBoardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
