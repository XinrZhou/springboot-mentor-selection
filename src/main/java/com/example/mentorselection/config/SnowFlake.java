package com.example.mentorselection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Configuration
@EnableR2dbcAuditing
public class SnowFlake {
    private final static long twepoch = 12888349746579L;
    private final static long workerIdBits = 5L;
    private final static long datacenterIdBits = 5L;
    private final static long sequenceBits = 12L;
    private final static long workerIdShift = sequenceBits;
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);
    private static long lastTimestamp = -1L;
    private long sequence = 0L;
    private long workerId = 1L;
    private static long workerMask = -1L ^ (-1L << workerIdBits);
    private long processId = 1L;
    private static long processMask = -1L ^ (-1L << datacenterIdBits);

    private static SnowFlake snowFlake = null;

    static{
        snowFlake = new SnowFlake();
    }
    public static synchronized long nextId(){
        return snowFlake.getNextId();
    }

    @Bean
    ReactiveAuditorAware<Long> auditorAware() {
        SnowFlake s = new SnowFlake();
        return () -> Mono.just(s.nextId());
    }

    public SnowFlake() {

        //获取机器编码
        this.workerId=this.getMachineNum();
        //获取进程编码
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.processId=Long.valueOf(runtimeMXBean.getName().split("@")[0]).longValue();
        //避免编码超出最大值
        this.workerId=workerId & workerMask;

        this.processId=processId & processMask;
    }

    public synchronized long getNextId() {
        //获取时间戳
        long timestamp = timeGen();
        //如果时间戳小于上次时间戳则报错
        if (timestamp < lastTimestamp) {
            try {
                throw new Exception("Clock moved backwards.  Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //如果时间戳与上次时间戳相同
        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1，与sequenceMask确保sequence不会超出上限
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        long nextId = ((timestamp - twepoch) << timestampLeftShift) | (processId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long getMachineNum(){
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }
}