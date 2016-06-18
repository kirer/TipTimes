package com.tiptimes.signal;


/**
 * signal监听者
 */
public  interface SignalListener{
	boolean handleSignal(Signal signal);
}
