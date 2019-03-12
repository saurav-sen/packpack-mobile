package in.squill.squilloffice.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 26-08-2018.
 */
public class PagedObject<T> {

    private List<T> result;

    private long nextPageRef;

    public List<T> getResult() {
        if(result == null) {
            result = new ArrayList<T>();
        }
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public long getNextPageRef() {
        return nextPageRef;
    }

    public void setNextPageRef(long nextPageRef) {
        this.nextPageRef = nextPageRef;
    }
}
