package com.nuist.currentlimit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wtx
 * @Date 2024/5/23
 * @Desc 漏桶限流算法
 * 控制流入网络的速率速度，以防止网络堵塞。核心思想就是将请求比作小水滴，漏桶看作固定容量的水桶，数据包从顶部进入漏桶，漏桶底部以匀速形式流出数据包。水满则溢，拒绝请求。
 * 优点：可以十分平滑的处理请求，对瞬间增加的请求有一定的承载能力
 * 缺点：需要对请求进行缓存，会增加服务器的内存消耗。面对突发流量无法进行快速处理请求。
 */
public class LeakyBucketRateLimiter {
    /**
     * 桶容量
     */
    private final int capacity;

    /**
     * 剩余水量
     */
    private final AtomicInteger water;

    /**
     * 限速  每s漏水速率
     */
    private final int leakRate;

    /**
     * 计算的起始时间
     */
    private Long lastTime;


    public LeakyBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.water = new AtomicInteger(0);
        this.leakRate = leakRate;
        this.lastTime = System.currentTimeMillis();
    }

    public synchronized boolean access() {
        // 空桶 放行
        if (water.get() == 0) {
            this.lastTime = System.currentTimeMillis();
            water.addAndGet(1);
            return true;
        }
        // 执行漏水
        int waterLeaked = (int) (((System.currentTimeMillis() - this.lastTime) / 1000) * leakRate);
        // 计算剩余水量
        int waterSurplus = this.water.get() - waterLeaked;
        water.set(Math.max(0, waterSurplus));
        lastTime = System.currentTimeMillis();
        // 尝试加水
        if (water.get() < this.capacity) {
            // 桶未满  放行
            water.addAndGet(1);
            return true;
        }
        // 水满，禁止访问
        return false;
    }


}
