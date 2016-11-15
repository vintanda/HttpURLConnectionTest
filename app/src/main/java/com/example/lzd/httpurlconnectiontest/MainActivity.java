package com.example.lzd.httpurlconnectiontest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int SHOW_REQUEST = 0;

    private Button button;
    private TextView textView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_REQUEST:
                    String response = (String) msg.obj;
                    textView.setText(response);
                    Log.d("Activity","handler initView");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.sendRequest);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(this);
        Log.d("onCreate","this is onCreate");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendRequest) {
            sendRequestWithHttpURLConnection();
            Log.d("onClick","button is clicked");
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    if(connection.getResponseCode() == 200) {
                        Log.d("sendRequest","GET success");
                        //获取数据并进行解析
                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder builder = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                            Log.d("IOStream", "读到一行数据并存到StringBuilder中");
                        }
                        Log.d("run", "have got data");

                        //Message对象，使用Handler将它发送出去
                        Message message = new Message();
                        message.what = SHOW_REQUEST;
                        message.obj = builder.toString();
                        handler.sendMessage(message);
                        Log.d("message", "give handler a message");
                    } else {
                        Log.d("sendRequest","GET failed");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}

