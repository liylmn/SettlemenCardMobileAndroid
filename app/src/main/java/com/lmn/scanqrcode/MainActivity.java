package com.lmn.scanqrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lmn.medicalqrcodelibrary.MedicalQrcodeActivity;
import com.lmn.medicalqrcodelibrary.constant.Extras;
import com.lmn.medicalqrcodelibrary.sdk.ZKRManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZKRManager.getInstance().init(MainActivity.this);
                ZKRManager.startService("");
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Extras.RESULT_CODE){
            Toast.makeText(this, "接受成功!", Toast.LENGTH_SHORT).show();
        }
//        String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
    }
}