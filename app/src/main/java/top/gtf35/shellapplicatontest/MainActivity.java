package top.gtf35.shellapplicatontest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mCmdInputEt;
    private Button mRunShellBtn;
    private TextView mOutputTv;

    private String[] mWebAddress = {
            "https://na.mbd.baidu.com/je3rqk2?f=cp",
            "https://mo.mbd.baidu.com/3z7ipq3?f=cp",
            "https://mt.mbd.baidu.com/pck0n7u?f=cp",
            "https://mi.mbd.baidu.com/wxk4dl0?f=cp",
            "https://mx.mbd.baidu.com/olc3ija?f=cp"};

    private int i = 0;

    private void initView() {
        mCmdInputEt = findViewById(R.id.et_cmd);
        mRunShellBtn = findViewById(R.id.btn_runshell);
        mOutputTv = findViewById(R.id.tv_output);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mRunShellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String cmd = mCmdInputEt.getText().toString();
//                if (TextUtils.isEmpty(cmd)) {
//                    Toast.makeText(MainActivity.this, "输入内容为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                runShell(cmd);
                startBrushOrder();
            }
        });
    }

    private void startBrushOrder() {
        try {
            // adb shell input text 'hello'
            runShell("adb shell am start -n com.baidu.searchbox/com.baidu.searchbox.MainActivity");

            Thread.sleep(1000);

            // adb shell input tap 500 380

            runShell("adb shell input tap 460 410");

            Thread.sleep(1000);

            // adb shell input text  https://baijiahao.baidu.com/u?app_id=1604509451686382
            runShell("adb shell input text  " + mWebAddress[i % mWebAddress.length]);

            Thread.sleep(3000);

            // adb shell input keyevent 66
            runShell("adb shell input keyevent 66");

            Thread.sleep(3000);

            runShell("adb shell input swipe  200 1800  200 0");

            Thread.sleep(1500);

            runShell("adb shell input swipe  200 1800  200 0");

            Thread.sleep(1500);

            runShell("adb shell input swipe  200 1800  200 0");

            Thread.sleep(1500);

            // 545 1330
            runShell("adb shell input tap 545 980");

            Thread.sleep(4000);

            runShell("adb shell input keyevent 4");

            Thread.sleep(200);

            runShell("adb shell input keyevent 4");

            Thread.sleep(200);

            runShell("adb shell input keyevent 4");

            Thread.sleep(200);

            runShell("adb shell input keyevent 4");

            Thread.sleep(200);

            runShell("adb shell input keyevent 4");

            Thread.sleep(1000);

            i++;

            if (i >= 10000000) {
                i = 0;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startBrushOrder();
    }

    private void runShell(final String cmd) {
        if (TextUtils.isEmpty(cmd)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                new SocketClient(cmd, new SocketClient.onServiceSend() {
                    @Override
                    public void getSend(String result) {
                        showTextOnTextView(result);
                    }
                });
            }
        }).start();
    }

    private void showTextOnTextView(final String text) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mOutputTv.getText())) {
                    mOutputTv.setText(text);
                } else {
                    mOutputTv.setText(mOutputTv.getText() + "\n" + text);
                }
            }
        });
    }
}
