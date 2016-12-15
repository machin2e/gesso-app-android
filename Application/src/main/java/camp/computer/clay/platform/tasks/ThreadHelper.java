package camp.computer.clay.platform.tasks;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class ThreadHelper {

    /**
     * Used to start multiple AsyncTasks concurrently.
     *
     * @param asyncTask
     * @param params
     * @param <T>
     */
    // TODO: Update the above to the below (from http://stackoverflow.com/questions/4068984/running-multiple-asynctasks-at-the-same-time-not-possible)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }

    /*
    // Note: This is a less capable implementation of the above method by the same name.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void executeAsyncTask(AsyncTask asyncTask, String... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }
    */
}
