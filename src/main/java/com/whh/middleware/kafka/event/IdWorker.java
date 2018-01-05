package com.whh.middleware.kafka.event;


import java.net.InetAddress;

/**
 * 在分布式系统中，需要生成全局UID的场合还是比较多的，twitter的snowflake解决了这种需求，
 * 实现也还是很简单的，除去配置信息，核心代码就是毫秒级时间41位+机器ID 10位+毫秒内序列12位。
 * 该项目地址为：https://github.com/twitter/snowflake是用Scala实现的。
 * python版详见开源项目https://github.com/erans/pysnowflake。
 * 10位机器id = 最多1024个机器 = 4个局域网网段ip
 *
 * @author   xiqiao
 * @Date	 2013-12-19
 */
public class IdWorker {
    //根据具体机器环境提供
    private final long workerId;
    //滤波器,使时间变小,生成的总位数变小,一旦确定不能变动
    private final static long twepoch = 1361753741828L;
    private long sequence = 0L;
    private final static long workerIdBits = 10L;
    private final static long maxWorkerId = -1L ^ -1L << workerIdBits;
    private final static long sequenceBits = 12L;

    private final static long workerIdShift = sequenceBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits;
    private final static long sequenceMask = -1L ^ -1L << sequenceBits;

    private long lastTimestamp = -1L;
    //根据主机id获取机器码
    private static IdWorker worker = new IdWorker();

    /**
     * 创建 IdWorker对象.
     * @Deprecated 请调用静态方法getId()
     * @param workerId
     */
    @Deprecated
    public IdWorker(final long workerId) {
        if (workerId > IdWorker.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                    IdWorker.maxWorkerId));
        }
        this.workerId = workerId;
    }

    public IdWorker() {
        this.workerId = getAddress() % (IdWorker.maxWorkerId + 1);
    }

    public static long getId() {
        return worker.nextId();
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & IdWorker.sequenceMask;
            if (this.sequence == 0) {
                //System.out.println("###########" + sequenceMask);//等待下一毫秒
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(String.format(
                        "Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp
                                - timestamp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << IdWorker.workerIdShift)
                | (this.sequence);
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp1) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp1) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private static long getAddress() {
        try {
            String currentIpAddress = InetAddress.getLocalHost().getHostAddress();
            String[] str = currentIpAddress.split("\\.");
            StringBuilder hardware = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                hardware.append(str[i]);
            }
            return Long.parseLong(hardware.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 2L;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(final String[] args) {
        long[] ids = new long[100000];
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            ids[i] = getId();
        }
        long end = System.currentTimeMillis();
        System.out.println((100000 / (end - start)) + "个/ms");
    }

}

