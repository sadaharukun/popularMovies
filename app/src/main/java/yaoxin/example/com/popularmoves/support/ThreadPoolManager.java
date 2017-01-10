package yaoxin.example.com.popularmoves.support;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yaoxinxin on 2017/1/5.
 */

public class ThreadPoolManager {

    private static ThreadPoolManager instance;

    private ThreadPoolManager() {

    }

    public static ThreadPoolManager newInstance() {
        if (instance == null) {
            synchronized (ThreadPoolManager.class) {
                if (instance == null) {
                    instance = new ThreadPoolManager();
                }
            }
        }
        return instance;
    }


    private ThreadPoolProxy mThreadPoolProxy;

    public synchronized ThreadPoolProxy creataPool() {
        if (mThreadPoolProxy == null) {
            mThreadPoolProxy = new ThreadPoolProxy(5, 10, 5000L);
        }
        return mThreadPoolProxy;
    }


    public static class ThreadPoolProxy {

        ThreadPoolExecutor pool;
        int corePoolSize;
        int maximumPoolSize;
        long keepAliveTime;

        /**
         * 创建线程池
         *
         * @param corePoolSize    核心线程池的大小
         * @param maximumPoolSize 线程池最大线程数
         * @param keepAliveTime   线程没有任务执行时最多保持多久会终止
         */
        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void run(Runnable r) {
            if (pool == null) {
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
            }
            pool.execute(r);
        }

        public void cancel() {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.shutdown();
            }
        }

        public void remove(Runnable r) {
            if (pool != null && !pool.isShutdown() && !pool.isTerminated()) {
                pool.remove(r);
            }
        }
    }


}
