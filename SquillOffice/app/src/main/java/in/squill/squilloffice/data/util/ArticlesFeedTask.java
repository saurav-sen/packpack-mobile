package in.squill.squilloffice.data.util;

import android.content.Context;

import com.pack.pack.client.api.COMMAND;
import com.squill.feed.web.model.JRssFeedType;

/**
 * Created by Saurav on 25-03-2018.
 */
public class ArticlesFeedTask extends FeedsLoadTask {

    public ArticlesFeedTask(Context context) {
        super(context, JRssFeedType.NEWS_SCIENCE_TECHNOLOGY);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_SCIENCE_AND_TECHNOLOGY_NEWS_FEEDS;
    }
}


