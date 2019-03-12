package in.squill.squilloffice;

import android.content.Context;

/**
 * Created by Saurav on 25-09-2018.
 */
public class SquillUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Context context;

    public SquillUncaughtExceptionHandler(Thread.UncaughtExceptionHandler androidDefaultUEH, Context context) {
        this.androidDefaultUEH = androidDefaultUEH;
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        androidDefaultUEH.uncaughtException(thread, ex);
    }
}
