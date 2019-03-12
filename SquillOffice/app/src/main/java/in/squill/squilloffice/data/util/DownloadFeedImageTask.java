package in.squill.squilloffice.data.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 29-05-2017.
 */
public class DownloadFeedImageTask extends DownloadImageTask {

    public DownloadFeedImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context, ProgressBar progressBar) {
        super(imageView, imageWidth, imageHeight, context, progressBar);
    }

    @Override
    protected String lookupURL(String url) {
        return url != null ? url.trim() : url;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.LOAD_EXTERNAL_RESOURCE;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, inputObject);
        return apiParams;
    }
}
