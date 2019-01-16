package honghesytemui.recycler;


import android.app.ActivityManagerNative;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ist.istsystemui.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mile on 2019/1/14.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> implements ItemTounhHelperAdapter {

    private ChangeStateListener changeStateListener;
    List<HashMap<String, Object>> appInfos;
    Context mContext;


    public AppsAdapter(Context mContext, List<HashMap<String, Object>> appInfos, ChangeStateListener changeStateListener){
        Log.d("lml","-----"+appInfos.size());
        this.appInfos = appInfos;
        this.mContext = mContext;
        this.changeStateListener = changeStateListener;
    }

    @Override
    public int getItemCount() {
        return appInfos.size();
    }

    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public AppsAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        AppsAdapter.MyViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item_layout, null, false);
        viewHolder = new AppsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        String title = (String) appInfos.get(position).get("title");
        Drawable icon = (Drawable) appInfos.get(position).get("icon");
        Intent singleIntent = (Intent) appInfos.get(position).get("tag");
        final String packageName = (String) appInfos.get(position).get("packageName");

        holder.infoView.setTag(singleIntent);
        holder.mImageView.setImageDrawable(icon);
        holder.mTextView.setText(title);
        holder.indexTv.setText(String.valueOf(position + 1));
        holder.del_iv.setOnClickListener(new View.OnClickListener(){
            //根据包名来清除应用进程
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "已清除进程的包名为："+packageName, 0).show();
                removeTask((Integer) appInfos.get(position).get("id"));
//                appInfos.remove(position);
                changeStateListener.change(position);

            }
        });
        Bitmap thumNail=getLoacalBitmap((String) appInfos.get(position).get("thumnailpath"));
        if(thumNail!=null){
            holder.thumnail_iv.setImageBitmap(thumNail);
        }

        //绑定点击事件，用来进行应用间的跳转
        holder.infoView.setOnClickListener(new SingleAppClickListener());
    }
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean removeTask(Integer taskId) throws SecurityException {
        try {
            return ActivityManagerNative.getDefault().removeTask(taskId);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public boolean onItemRemove(int position) {
        removeTask((Integer) appInfos.get(position).get("id"));
        changeStateListener.change(position);
        return true;
    }

    @Override
    public boolean onItemRemoveBefore(int position) {
        return false;
    }

    @Override
    public boolean canSwipe(int position) {
        return true;
    }

    //点击应用的图标启动应用程序
    class SingleAppClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = (Intent) v.getTag();
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                try {
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.w("Recent", "Unable to launch recent task", e);
                }
            }
        }
    }

    public interface ChangeStateListener {
        void change(int index);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        View infoView;
        ImageView mImageView;
        ImageView del_iv;
        ImageView thumnail_iv;
        TextView mTextView;
        TextView indexTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            infoView = itemView;
            mImageView = (ImageView) infoView.findViewById(R.id.appicon_iv);
            del_iv = (ImageView) infoView.findViewById(R.id.item_del_iv);
            thumnail_iv = (ImageView) infoView.findViewById(R.id.thumnail_iv);
            mTextView = (TextView) infoView.findViewById(R.id.appName_tv);
            indexTv = (TextView) infoView.findViewById(R.id.appindex_tv);
        }
    }




}
