package com.pack.pack.application.async;

import android.content.Context;

import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.pack.pack.application.data.util.ArticlesFeedTask;
import com.squill.feed.web.model.JRssFeedType;

/**
 * Created by Saurav on 25-09-2018.
 */
public final class FeedsDownloadUtil {

    private FeedsDownloadUtil() {
    }

    public static void downloadLatestFeedsFromOrigin(Context context, FeedReceiveCallback callback, boolean resetPreviousReceiveStateInfo) {
        if(context == null || callback == null) {
            return;
        }

        /*if(resetPreviousReceiveStateInfo) {
            AppController.getInstance().getFeedReceiveState().setLastUpdateTimestamp(JRssFeedType.NEWS, FeedReceiveState.DEFAULT_MIN_TIMESTAMP);
            AppController.getInstance().getFeedReceiveState().setLastUpdateTimestamp(JRssFeedType.NEWS_SPORTS, FeedReceiveState.DEFAULT_MIN_TIMESTAMP);
            AppController.getInstance().getFeedReceiveState().setLastUpdateTimestamp(JRssFeedType.NEWS_SCIENCE_TECHNOLOGY, FeedReceiveState.DEFAULT_MIN_TIMESTAMP);
            AppController.getInstance().getFeedReceiveState().setLastUpdateTimestamp(JRssFeedType.ARTICLE, FeedReceiveState.DEFAULT_MIN_TIMESTAMP);
        }*/

        NewsFeedTask newsFeedTask = new NewsFeedTask(context);
        ArticlesFeedTask scienceNewsFeedTask = new ArticlesFeedTask(context);

        IAsyncTaskStatusListener listener = new FeedReceiveTaskStatusListener(callback)
                .addTaskID(newsFeedTask.getTaskID(), JRssFeedType.NEWS)
                .addTaskID(scienceNewsFeedTask.getTaskID(), JRssFeedType.NEWS_SCIENCE_TECHNOLOGY);

        newsFeedTask.addListener(listener);
        scienceNewsFeedTask.addListener(listener);

        newsFeedTask.execute(String.valueOf(0));
        scienceNewsFeedTask.execute(String.valueOf(0));
    }

   /* public void removeExpiredOfflineData(Context context) {
        new AsyncDelete(context).execute();
    }*/

    /*private class AsyncDelete extends AsyncTask<Void, Integer, Integer> {

        private Context context;

        AsyncDelete(Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return DBUtil.removeExpiredOfflineJsonModel(context);
        }
    }*/
}
