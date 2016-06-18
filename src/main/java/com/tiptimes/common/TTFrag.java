package com.tiptimes.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiptimes.signal.Signal;
import com.tiptimes.signal.SignalListener;
import com.tiptimes.signal.SignalManager;

/**
 * Created by xinwenbo on 15/12/3.
 */
public abstract class TTFrag extends Fragment implements SignalListener{

    @Override
    public boolean handleSignal(Signal signal) {
        return true;
    }

    protected View view;
    private Handler delayHandle = new Handler();
    private Runnable delayRunnable = new Runnable() {

        @Override
        public void run() {
            SignalManager.INSTANCE.addSignalListener(TTFrag.this);
            initView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(setContentView(savedInstanceState), null);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                delayHandle.post(delayRunnable);
            }
        });
    }
    protected abstract int setContentView(Bundle savedInstanceState);
    protected abstract void initView();

    protected ProgressDialog progressDialog;
    protected void showLoading() {
        showLoading("正在加载...", false);
    }
    protected void showLoading(String message, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
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

    protected <T extends View> T $(int resId) {
        return (T) view.findViewById(resId);
    }
    protected <T extends View> T $(Activity activity, int resId) {
        return (T) activity.findViewById(resId);
    }
    protected void $onClick(View.OnClickListener listener, int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            $(id).setOnClickListener(listener);
        }
    }

    @Override
    public void onDestroyView() {
        SignalManager.INSTANCE.removeSignalListener(this);
        super.onDestroyView();
    }
}
