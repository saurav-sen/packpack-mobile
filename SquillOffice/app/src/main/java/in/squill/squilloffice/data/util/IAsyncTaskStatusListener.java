package in.squill.squilloffice.data.util;

/**
 * Created by Saurav on 11-06-2016.
 */
public interface IAsyncTaskStatusListener {

    public void onPreStart(String taskID);

    public void onSuccess(String taskID, Object data);

    public void onFailure(String taskID, String errorMsg);

    public void onPostComplete(String taskID);
}
