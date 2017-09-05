package common.widget.gridlist;

import android.database.Cursor;

/**
 * Created by JiaHongYa
 */
public class GridListDataSetObserver  {

    /**
     * This method is called when the entire data set has changed,
     * most likely through a call to {@link Cursor#requery()} on a {@link Cursor}.
     */
    public void onChanged() {
        // Do nothing
    }
    public void onItemChanged(int pos){

    }
}
