package Utils.Thread;

import android.app.Activity;

import com.lu.mydemo.Notification.AlertCenter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建时间: 2019/12/02 11:15 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class MyThreadController {

    static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2, //corePoolSize
            10,	//maximumPoolSize
            1L,
            TimeUnit.SECONDS,
            workQueue
    );

    public static void commit(Runnable runnable) {
        threadPool.execute(runnable);
    }

}
