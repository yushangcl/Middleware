package com.whh.middleware.kafka.event;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by shisheng.wang on 17/12/14.
 */
public class EventConfig {
    public static AtomicLong _dataSend = new AtomicLong(1);

    public static boolean hasSendData() {
        return _dataSend.get() > 0;
    }

    public static void incSendData(int count) {
        _dataSend.addAndGet(count);
    }
    public static void decSendData(int count) {
        _dataSend.addAndGet(-count);
        if (_dataSend.get() < 0) {
            _dataSend.set(0);
        }
    }

    public static AtomicLong _dataRecv = new AtomicLong(1);

    public static boolean hasRecvData() {
        return _dataRecv.get() > 0;
    }

    public static void incRecvData(int count) {
        _dataRecv.addAndGet(count);
    }
    public static void decRecvData(int count) {
        _dataRecv.addAndGet(-count);
        if (_dataRecv.get() < 0) {
            _dataRecv.set(0);
        }
    }
}

