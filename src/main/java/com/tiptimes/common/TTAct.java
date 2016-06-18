package com.tiptimes.common;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tiptimes.signal.Signal;
import com.tiptimes.signal.SignalListener;
import com.tiptimes.signal.SignalManager;
import com.tiptimes.utils.ActManager;


/**
 * Created by xinwenbo on 15/12/1.
 */
public abstract class TTAct extends AppCompatActivity implements SignalListener{

    @Override
    public boolean handleSignal(Signal signal) {
        return true;
    }

    private Handler delayHandle = new Handler();
    private Runnable delayRunnable = new Runnable() {

        @Override
        public void run() {
            SignalManager.INSTANCE.addSignalListener(TTAct.this);
            initView();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActManager.getInstance().add(this);
        setContentView(setContentView(savedInstanceState));
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                delayHandle.post(delayRunnable);
            }
        });
    }
    protected abstract int setContentView(Bundle savedInstanceState);
    protected abstract void initView();

    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }
    protected void $onClick(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            $(id).setOnClickListener(listener);
        }
    }

    protected  ProgressDialog progressDialog;
    protected void showLoading() {
        showLoading("正在加载...", false);
    }
    protected void showLoading(String message, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    protected void hiddenLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        ActManager.getInstance().pop(this);
        SignalManager.INSTANCE.removeSignalListener(this);
        super.onDestroy();
    }
}
