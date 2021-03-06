package org.study.test.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * 使用该工具类需要以下依赖：
 * <pre>
 *     <dependency>
 *             <groupId>org.apache.commons</groupId>
 *             <artifactId>commons-lang3</artifactId>
 *             <version>3.8</version>
 *     </dependency>
 * </pre>
 * <p>
 * Twitter SnowFlake 的结构为(每部分用-分开)：
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 0000000000 - 000000000000
 * 1、1位标识。由于 long 基本类型在 Java 中是带符号的，最高位是符号位，正数是0，负数是1，所以 id 一般是正数，最高位是0。
 * 2、41位时间截(毫秒级)。注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值(当前时间截 - 开始时间截)，开始时间截一般是 id 生成器开始使用的时间，由程序来指定的(如类中的 epoch 属性)。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69。
 * 3、10位的数据机器位。可以部署在1024个节点，包括5位 dataCenterId 和5位 workerId。
 * 4、12位序列。毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个 ID 序号。
 * 加起来刚好64位，为一个 Long 型。
 * <p>
 * SnowFlake的优点：整体上按照时间自增排序，并且整个分布式系统内不会产生 ID 碰撞(由数据中心 ID 和机器 ID 作区分)，并且效率较高。
 *
 * @author Administrator
 */
@Slf4j
public class SnowFlakeIdWorker {
    /**
     * 开始时间截(2015-01-01)
     */
    private final long epoch = 1420041600000L;

    /**
     * 机器 ID 所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识 ID 所占的位数
     */
    private final long dataCenterIdBits = 5L;

    /**
     * 支持的最大机器 ID，结果是31。该移位算法可以很快的计算出几位二进制数所能表示的最大十进制数
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识 ID，结果是31
     */
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /**
     * 序列在 ID 中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器 ID 向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识 ID 向左移17位(12+5)
     */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码，这里为4095(0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器 ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心 ID(0~31)
     */
    private long dataCenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成 ID 的时间截
     */
    private long lastTimestamp = -1L;

    private static SnowFlakeIdWorker idWorker;

    static {
        idWorker = new SnowFlakeIdWorker(getWorkId(), getDataCenterId());
    }

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public SnowFlakeIdWorker(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获得下一个 ID。该方法是线程安全的
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        // 上次生成 ID 的时间截
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的 ID
        return ((timestamp - epoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成 ID 的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            log.info("当前 HostAddress：{}", hostAddress);
            return hostToLong(hostAddress);
        } catch (UnknownHostException e) {
            log.warn("获取 HostAddress 异常，将使用 0~31 的随机数生成 WorkId");
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

    private static Long getDataCenterId() {
        String hostName = SystemUtils.getHostName();
        log.info("当前 HostName：{}", hostName);
        return hostToLong(hostName);
    }

    private static Long hostToLong(String host) {
        int[] codePoints = StringUtils.toCodePoints(host);
        int sum = 0;
        for (int codePoint : codePoints) {
            sum += codePoint;
        }
        return Long.valueOf(sum % 32);
    }

    /**
     * 静态工具类
     *
     * @return
     */
    public static Long genId() {
        return idWorker.nextId();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            System.out.println(SnowFlakeIdWorker.genId());
        }
    }
}
