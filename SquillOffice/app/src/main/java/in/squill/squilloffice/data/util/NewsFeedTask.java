package in.squill.squilloffice.data.util;

import android.content.Context;

import com.pack.pack.client.api.COMMAND;
import com.squill.feed.web.model.JRssFeedType;

/**
 *
 * Created by Saurav on 13-08-2017.
 *
 */
public class NewsFeedTask extends FeedsLoadTask {

    public NewsFeedTask(Context context) {
        super(context, JRssFeedType.NEWS);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_NEWS_FEEDS;
    }
}
