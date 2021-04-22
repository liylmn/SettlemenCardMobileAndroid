package com.lmn.medicalqrcodelibrary.sdk;

import android.content.Context;

import com.lmn.medicalqrcodelibrary.MedicalQrcodeActivity;

public class ZKRManager {
    private static volatile ZKRManager zkrManager;
    private static Context context;
    private ZKRManager() {
    };

    public static ZKRManager getInstance() {
        if (zkrManager == null) {
            synchronized (ZKRManager.class) {
                if (zkrManager == null) {
                    zkrManager = new ZKRManager();
                }
            }
        }
        return zkrManager;
    }

    public void init(Context context){
        this.context=context;
    }
    public static void startService(String data){
        MedicalQrcodeActivity.start(context,data);
    }

}
