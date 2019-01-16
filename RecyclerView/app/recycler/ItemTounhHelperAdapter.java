package honghesytemui.recycler;

/**
 * Created by smile on 2019/1/14.
 */

public interface ItemTounhHelperAdapter {

    public boolean onItemRemove(int position);
    public boolean onItemRemoveBefore(int position);
    public boolean canSwipe(int position);

}
