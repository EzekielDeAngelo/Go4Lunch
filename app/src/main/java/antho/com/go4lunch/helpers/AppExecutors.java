package antho.com.go4lunch.helpers;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
/** Global executor pools for the whole application, avoid the effects of task starvation **/
public class AppExecutors
{
    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread)
    {
        this.mDiskIO = diskIO;
        this.mNetworkIO = networkIO;
        this.mMainThread = mainThread;
    }
    // Constructor
    public AppExecutors()
    {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), new MainThreadExecutor());
    }
    // Executor methods
    public Executor diskIO() {
        return mDiskIO;
    }
    public Executor networkIO() {
        return mNetworkIO;
    }
    public Executor mainThread() {
        return mMainThread;
    }
    // Main thread executor class
    private static class MainThreadExecutor implements Executor
    {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
