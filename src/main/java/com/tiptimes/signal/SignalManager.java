package com.tiptimes.signal;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xinwenbo on 16/1/30.
 */
public enum SignalManager {

    INSTANCE;

    SignalManager() {
    }

    private List<Signal> sList = new LinkedList<>(); //信号集合
    private List<SignalListener> slList = new ArrayList<>(); //信号监听者集合

    /**
     * 发送信号
     *
     * @param signal
     */
    public synchronized void send(Signal signal) {
        sList.add(signal);
        for (int i = 0; i < slList.size(); i++) {
            try {
                sendToTarget(slList.get(i), signal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加入监听者
     *
     * @param signalListener
     */
    public synchronized void addSignalListener(SignalListener signalListener) {
        if(!slList.contains(signalListener)){
            slList.add(signalListener);
        }
        for (int i = 0; i < sList.size(); i++) {
            try {
                sendToTarget(signalListener, sList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 移除监听者
     *
     * @param signalListener
     */
    public synchronized void removeSignalListener(SignalListener signalListener) {
        slList.remove(signalListener);
    }

    /**
     * 发送完成不需要分发，移除信号
     *
     * @param signal
     */
    private synchronized void removeSignal(Signal signal) {
        sList.remove(signal);
    }

    /**
     * 发送信号
     *
     * @param signalListener
     * @param signal
     */
    private boolean sendToTarget(SignalListener signalListener, Signal signal) throws Exception {
        boolean result = false;
        List<Signal> removeSignals = new LinkedList<Signal>();//要移除的signal

        Method handleMethod = signalListener.getClass().getMethod("handleSignal", Signal.class);
        handleMethod.setAccessible(true);
        S targetS = handleMethod.isAnnotationPresent(S.class) ? handleMethod.getAnnotation(S.class) : null;

        if (targetS == null) {
            if (signal.target == null) {//当过虑条件和信号标识别都为null时匹配
                result = (Boolean) handleMethod.invoke(signalListener, signal);
                signal.Distribution();
                if (result) {
                    removeSignals.add(signal);
                }
            }
        } else {
            if (targetS.name() != null && signal.target.equals(targetS.name())) {//匹配
                result = (Boolean) handleMethod.invoke(signalListener, signal);
                signal.Distribution();//分发数量加1
                if (result) {//是否要移除
                    removeSignals.add(signal);
                }
            }
        }
        for (int i = 0; i < removeSignals.size(); i++) {
            removeSignal(removeSignals.get(i));
        }
        return result;
    }

}
