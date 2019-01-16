package honghesytemui.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ist.istsystemui.R;

import honghesytemui.adapter.AppAdapter;
import honghesytemui.adapter.RecentAdapter;
import honghesytemui.adapter.SideBarVpAdapter;
import honghesytemui.bean.RecentBean;

import honghesytemui.dialog.CommomDialog;
import honghesytemui.recycler.AppItemTounhHelperCallBack;
import honghesytemui.recycler.AppsAdapter;
import honghesytemui.util.RecentTaskUtil;
import honghesytemui.view.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static honghesytemui.util.RecentTaskUtil.clearAllRecent;
import com.ist.android.tv.util.IstConstant;
import honghesytemui.util.SharedUtil;
import com.ist.android.tv.util.IstConstant;
public class MenuLeftActiviy extends AppCompatActivity implements AppsAdapter.ChangeStateListener, View.OnClickListener {
    private ViewPager sidebarVp;
    private String[] tabs = new String[]{"工具", "通知"};
    private SideBarVpAdapter vpAdapter;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ImageView del_recentapp,msgtip_iv;
//   private ListView mRecentLV;
//    private AppAdapter recentAdapter;

    //smile add delect recent app 20190116
    private RecyclerView mRecentLV;
    private AppsAdapter recentAdapter;
    private AppItemTounhHelperCallBack callback ;
    private   ItemTouchHelper touchHelper ;

    private EditText et_search;
    private List<RecentBean> mRecentlyData = new ArrayList<>();
    private List<HashMap<String, Object>> appInfos = new ArrayList<HashMap<String, Object>>();
    private FinishActivityRecevier mRecevier;
    public static String RECEIVER_ACTION_FINISH_Left = "Close LeftMenu";
    public static String Menu_Left = "LeftMenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) getResources().getDimension(R.dimen.x1762);
//        params.height = this.getResources().getDisplayMetrics().heightPixels;
//        params.height = (int) getResources().getDimension(R.dimen.x2160);
        params.height =WindowManager.LayoutParams.MATCH_PARENT ;
        params.gravity = Gravity.LEFT | Gravity.TOP;
//      params.y=
        getWindow().setAttributes(params);
        setContentView(R.layout.menuleft_layout);
        initView();


        vpAdapter = new SideBarVpAdapter(getSupportFragmentManager(), Menu_Left,this);
        sidebarVp.setAdapter(vpAdapter);

        mPagerSlidingTabStrip.setViewPager(sidebarVp);
        mPagerSlidingTabStrip.setShouldExpand(true);

        mRecevier = new FinishActivityRecevier();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION_FINISH_Left);
        registerReceiver(mRecevier, intentFilter);


    }

    private void initView() {
        SharedUtil.initialize(this);
        sidebarVp = (ViewPager) findViewById(R.id.sidebar_vp);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //mRecentLV = (ListView) findViewById(R.id.recent_lv);

        mRecentLV = (RecyclerView) findViewById(R.id.recent_lv);
        mRecentLV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        et_search = (EditText) findViewById(R.id.et_search);
        Drawable drawable_search = getResources().getDrawable(R.drawable.iconsearch);
        drawable_search.setBounds(0, 0, (int) getResources().getDimension(R.dimen.x45), (int) getResources().getDimension(R.dimen.x45));
        et_search.setCompoundDrawables(drawable_search, null, null, null);
        del_recentapp = (ImageView) findViewById(R.id.del_recentapp);
        msgtip_iv = (ImageView) findViewById(R.id.msgtip_iv);
        del_recentapp.setOnClickListener(this);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("https://www.baidu.com/s?wd=" + et_search.getText().toString());
                        intent.setData(content_url);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                }
                return false;
            }
        });

        LinearLayout menuleft_ll = (LinearLayout) findViewById(R.id.menuleft_ll);
        Bitmap bitmap=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/.blurpics/.blurWallpaper1.jpg");
        if(bitmap!=null){
            menuleft_ll.setBackground(new BitmapDrawable(bitmap));
        }else{
            menuleft_ll.setBackgroundResource(R.drawable.navmenu_bg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设定显示的最大的任务数为20
        RecentTaskUtil.reloadButtons(this, appInfos, 21*3);
        //smile hide
//        recentAdapter = new AppAdapter(this, appInfos, this);
//        mRecentLV.setAdapter(recentAdapter);

        recentAdapter = new AppsAdapter(this,appInfos,this);
        mRecentLV.setAdapter(recentAdapter);
        callback = new AppItemTounhHelperCallBack(recentAdapter);
        touchHelper.attachToRecyclerView(mRecentLV);
        del_recentapp.setVisibility(appInfos.size() != 0 ? View.VISIBLE : View.GONE);
        if(appInfos.size() == 0){
            mRecentLV.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    finish();
                    return false;
                }
            });
        }
        msgtip_iv.setVisibility(SharedUtil.getLong("notifiCount")>0?View.VISIBLE:View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.left_out);
    }


    @Override
    public void change(int index) {
        appInfos.remove(index);
        mRecentLV.setAdapter(recentAdapter);
        if (appInfos != null && appInfos.size() == 0) {
            del_recentapp.setVisibility(View.GONE);
            mRecentLV.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    finish();
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
       final CommomDialog dialog = null;

        new CommomDialog(MenuLeftActiviy.this, R.style.MyDialog, "您确定关闭所有的后台运行应用", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    clearAllRecent(appInfos);
                    appInfos.clear();
                    mRecentLV.setAdapter(recentAdapter);
                    del_recentapp.setVisibility(View.GONE);
                    mRecentLV.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            finish();
                            return false;
                        }
                    });
                    dialog.dismiss();
                }
            }
        }).setTitle("提示").show();



    }

    private class FinishActivityRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if (RECEIVER_ACTION_FINISH_Left.equals(intent.getAction())) {
                MenuLeftActiviy.this.finish();
            }
        }
    }


}
