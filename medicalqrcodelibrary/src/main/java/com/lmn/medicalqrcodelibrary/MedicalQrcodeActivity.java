package com.lmn.medicalqrcodelibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lmn.medicalqrcodelibrary.constant.Extras;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.lmn.medicalqrcodelibrary.constant.Extras.EXTRA_DATA;

public class MedicalQrcodeActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private Context mContext = this;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_qrcode);
        buildProgressDialog(R.string.loading);
        initokhttp();
        startsend();
    }

    public static void start(Context context, String data) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_DATA, data);
        intent.setClass(context, MedicalQrcodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(intent, Extras.REQUEST_CODE);
    }

    /**
     * 开始调用接口
     */
    private void startsend() {
        //1.1.1	获取个人信息（多介质）
        String content = "{\"MultimediaInfo\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf16\\\" standalone=\\\"yes\\\" ?><root version=\\\"2.003\\\" sender=\\\"capinfo\\\"><input><multiclass>2</multiclass><QRcode>4243697955049960345600000000</QRcode><businessType>01301</businessType><termId>APP</termId><termIp>192.168.1.2</termIp><operatorId>APP-01</operatorId><operatorName>APP-01</operatorName><officeId>01</officeId><officeName>移动终端</officeName></input></root>\"}";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , content);
        sendposthttp("http://261g2715n4.qicp.vip/RestService4Proxy/ybbj/GetPersonInfo_Multimedia", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                senddivide();
            }
        });
    }

    /**
     * 1.1.2费用分解
     */
    private void senddivide() {
        String content = "{\n" +
                "    \"DivideInfo\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-16\\\" standalone=\\\"yes\\\" ?><root version=\\\"2.003\\\" sender=\\\"ZKRKJGFY00002.100311\\\"><input><tradeinfo><curetype>18</curetype><illtype>0</illtype><feeno>20210412173156</feeno><operator>APP</operator></tradeinfo><recipearray><recipe><diagnoseno>1</diagnoseno><recipeno>20210412173156</recipeno><recipedate>20210412173156</recipedate><diagnosename></diagnosename><diagnosecode></diagnosecode><medicalrecord></medicalrecord><sectioncode>20</sectioncode><sectionname>急诊门诊</sectionname><hissectionname> </hissectionname><drid></drid><drname> </drname><recipetype>1</recipetype><drlevel></drlevel><helpmedicineflag>0</helpmedicineflag><remark></remark><registertradeno></registertradeno><billstype>1</billstype></recipe></recipearray><feeitemarray><feeitem itemno=\\\"1\\\" recipeno=\\\"20210412173156\\\"  hiscode=\\\" W0101020019\\\" itemname= \\\" 急诊门诊\\\" itemtype=\\\"1\\\" unitprice=\\\" 70.0\\\" count=\\\"1\\\" fee=\\\" 70.0\\\" dose=\\\"\\\" specification=\\\"\\\" unit=\\\" \\\" howtouse=\\\"\\\" dosage=\\\"\\\" packaging=\\\"\\\" minpackage=\\\"\\\" conversion=\\\"\\\" days=\\\"\\\" /></feeitemarray></input></root>\"\n" +
                "}\n";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , content);
        sendposthttp("http://261g2715n4.qicp.vip/RestService4Proxy/ybbj/divide", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendTradeAll();
            }
        });
    }

    /**
     * 1.1.3	门诊交易确认(新)(TradeAll)
     */
    private void sendTradeAll() {
        String content = "{\n" +
                "    \"QueryInfo\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf16\\\" standalone=\\\"yes\\\" ?><root version=\\\"2.003\\\" sender=\\\"ZKRKJGFY00002.100311\\\"><input><tradeno name=\\\"交易流水号\\\">051550030A210412000723</tradeno></input></root>\"\n" +
                "}\n";

        sendgethttp("http://261g2715n4.qicp.vip/RestService4Proxy/ybbj/TradeAll", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendHISFinishedNotice();
            }
        });
    }

    /**
     * 1.1.4	门诊交易完成通知(HISFinishedNotice)
     */
    private void sendHISFinishedNotice() {
        String content = "{\n" +
                "    \"QueryInfo\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf16\\\" standalone=\\\"yes\\\" ?><root version=\\\"2.003\\\" sender=\\\"ZKRKJGFY00002.100311\\\"><input><tradeno name=\\\"交易流水号\\\">051550030A210412000723</tradeno></input></root>\"\n" +
                "}\n";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , content);
        sendposthttp("http://261g2715n4.qicp.vip/RestService4Proxy/ybbj/HISFinishedNotice", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendTradeRollBack();
            }
        });
    }

    /**
     * 1.1.5	交易挂起回退(TradeRollBack)
     */
    private void sendTradeRollBack() {
        String content = "{\n" +
                "    \"CommitInfo\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf16\\\" standalone=\\\"yes\\\" ?><root version=\\\"2.003\\\" sender=\\\"ZKRKJGFY00002.100311\\\"><input><tradetype>0</tradetype><tradeno>051550030A210412000722</tradeno><fulltradeno></fulltradeno><partialtradeno></partialtradeno></input></root>\"\n" +
                "}\n";
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , content);
        sendposthttp("http://261g2715n4.qicp.vip/RestService4Proxy/ybbj/TradeRollBack", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "加载成功!", Toast.LENGTH_SHORT).show();
                        cancelProgressDialog();
                        //设置返回数据
                        setResult(Extras.RESULT_CODE, new Intent());
                        //关闭Activity
                        finish();
                    }
                });
            }
        });
    }


    private void initokhttp() {
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public void sendgethttp(String url, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(call, e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("okhttp response", response.body().string());
                        callback.onResponse(call, response);
                    }
                });
            }
        }).start();
    }

    public void sendposthttp(String url, RequestBody requestBody, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(call, e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("okhttp response", response.body().string());
                        callback.onResponse(call, response);
                    }
                });
            }
        }).start();
    }

    /**
     * 加载框
     */
    public void buildProgressDialog(int id) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage(getString(id));
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * @Description: TODO 取消加载框
     * @author Sunday
     * @date 2015年12月25日
     */
    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }
}