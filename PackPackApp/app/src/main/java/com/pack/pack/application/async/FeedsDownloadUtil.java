package com.pack.pack.application.async;

import android.content.Context;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.util.ArticlesFeedTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.pack.pack.application.data.util.ScienceNewsFeedTask;
import com.pack.pack.application.data.util.SportsFeedTask;
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

        if(resetPreviousReceiveStateInfo) {
            AppController.getInstance().getFeedReceiveState().setIsFirstPageNewsReceived(false);
            AppController.getInstance().getFeedReceiveState().setIsFirstPageSportsNewsReceived(false);
            AppController.getInstance().getFeedReceiveState().setIsFirstPageScienceNewsReceived(false);
            AppController.getInstance().getFeedReceiveState().setIsFirstPageArticlesReceived(false);
        }

        NewsFeedTask newsFeedTask = new NewsFeedTask(context, 0);
        SportsFeedTask sportsFeedTask = new SportsFeedTask(context, 0);
        ScienceNewsFeedTask scienceNewsFeedTask = new ScienceNewsFeedTask(context, 0);
        ArticlesFeedTask artilcesFeedTask = new ArticlesFeedTask(context, 0);

        IAsyncTaskStatusListener listener = new FeedReceiveTaskStatusListener(callback)
                .addTaskID(newsFeedTask.getTaskID(), JRssFeedType.NEWS)
                .addTaskID(sportsFeedTask.getTaskID(), JRssFeedType.NEWS_SPORTS)
                .addTaskID(scienceNewsFeedTask.getTaskID(), JRssFeedType.NEWS_SCIENCE_TECHNOLOGY)
                .addTaskID(artilcesFeedTask.getTaskID(), JRssFeedType.ARTICLE);

        newsFeedTask.addListener(listener);
        sportsFeedTask.addListener(listener);
        scienceNewsFeedTask.addListener(listener);
        artilcesFeedTask.addListener(listener);

        newsFeedTask.execute(String.valueOf(0));
        sportsFeedTask.execute(String.valueOf(0));
        scienceNewsFeedTask.execute(String.valueOf(0));
        artilcesFeedTask.execute(String.valueOf(0));
    }
}
