package com.aucn.tv;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aucn.tv.adapter.OpenTabTitleAdapter;
import com.aucn.tv.config.Config;
import com.aucn.tv.config.DisplayBase;
import com.aucn.tv.utils.AsyncTaskImageLoad;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.bridge.OpenEffectBridge;
import com.open.androidtvwidget.utils.NetWorkUtils;
import com.open.androidtvwidget.utils.OPENLOG;
import com.open.androidtvwidget.view.GridViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.OpenTabHost;
import com.open.androidtvwidget.view.ReflectItemView;
import com.open.androidtvwidget.view.TextViewWithTTF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2016/11/21.
 */

public class MainActivity extends Activity  implements OpenTabHost.OnTabSelectListener {

    private List<View> viewList;// view数组
    private View view1, view2, view3,view4;
    private GridViewTV GV2;
    private GridViewTV GV3;
    private GridViewTV GV4;

    private static boolean view2Inited = false;
    private static boolean view3Inited = false;
    private static boolean view4Inited = false;

    private GridViewAdapter mAdapter2;
    private GridViewAdapter mAdapter3;
    private GridViewAdapter mAdapter4;

    private List<String> data2;
    private List<String> data3;
    private List<String> data4;

    ViewPager viewpager;
    OpenTabHost mOpenTabHost;
    OpenTabTitleAdapter mOpenTabTitleAdapter;
    // 移动边框.
    MainUpView mainUpView1;
    EffectNoDrawBridge mEffectNoDrawBridge;
    View mNewFocus;
    View mOldView;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        startActivity(new Intent(getApplicationContext(), DemoGridViewActivity.class));
//        Intent i = new Intent(getApplicationContext(), YouTubePlayerActivity.class);
//        i.putExtra("key","O1YzPhOct5s");
//        startActivity(i);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_viewpager_activity);

        if(!NetWorkUtils.isNetWorkdetect(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "未检测到网络连接，请检测网络后重试。", Toast.LENGTH_SHORT).show();
            MainActivity.this.finish();
        }
//
//        if(!NetWorkUtils.isIpReachable(Config.GOOGLE_TEST_IP)){
//            Toast.makeText(getApplicationContext(), "GOOGLE服务不可用，请稍后重试。", Toast.LENGTH_SHORT).show();
//            MainActivity.this.finish();
//        }

        final Intent intent = new Intent();
        intent.setClass(MainActivity.this,SplashActivity.class);
        MainActivity.this.startActivity(intent);

        initAllTitleBar();
        // 初始化viewpager.
        initAllViewPager();
        // 初始化移动边框.
        initMoveBridge();
//        ReflectItemView riv = (ReflectItemView) findViewById(R.id.page1_item4);
//        riv.setDrawShape();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initMoveBridge() {
        float density = getResources().getDisplayMetrics().density;
        mainUpView1 = (MainUpView) findViewById(R.id.mainUpViewViewPager);
        mEffectNoDrawBridge = new EffectNoDrawBridge();
        mainUpView1.setEffectBridge(mEffectNoDrawBridge);
        mEffectNoDrawBridge.setUpRectResource(R.drawable.white_light_10); // 设置移动边框图片.
        RectF rectF = new RectF(getDimension(R.dimen.w_10) * density, getDimension(R.dimen.h_10) * density, getDimension(R.dimen.w_10) * density, getDimension(R.dimen.h_10) * density);
        mEffectNoDrawBridge.setDrawUpRectPadding(rectF);
    }

    private void initAllTitleBar() {
        mOpenTabHost = (OpenTabHost) findViewById(R.id.openTabHost);
        mOpenTabTitleAdapter = new OpenTabTitleAdapter();
        mOpenTabHost.setOnTabSelectListener(this);
        mOpenTabHost.setAdapter(mOpenTabTitleAdapter);
    }

    private void initAllViewPager() {
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        //
        LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.test_page1, null);
        view2 = inflater.inflate(R.layout.test_page2, null);
        view3 = inflater.inflate(R.layout.test_page3, null);
        view4 = inflater.inflate(R.layout.test_page4, null);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        initView1();
        initView2();
        initView3();
        initView4();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        // 初始化滚动窗口适配. (请注意哈，在不同的dpi下, 滚动相差的间距不一样哈)
        float density = getResources().getDisplayMetrics().density;
//        SmoothHorizontalScrollView shsv = (SmoothHorizontalScrollView) view1.findViewById(R.id.test_hscroll);

//        shsv.setFadingEdge((int) (getDimension(R.dimen.w_200) * density));


        viewpager.setAdapter(new DemoPagerAdapter());
        // 全局焦点监听
        viewpager.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                // 你也可以让标题栏放大，有移动边框.
                if ((newFocus != null && (newFocus instanceof GridView)) ||(newFocus != null && newFocus instanceof ReflectItemView)){
                    mEffectNoDrawBridge.setVisibleWidget(false);
                    mainUpView1.setFocusView(newFocus, mOldView, 1.1f);
                    mOldView = newFocus;
//                    mainUpView1.setUnFocusView(oldFocus);
                    // 让被挡住的焦点控件在前面.
                    newFocus.bringToFront();
                    OPENLOG.D("addOnGlobalFocusChangeListener");
                } else { // 标题栏处理.
                    mNewFocus = null;
                    mOldView = null;
                    mainUpView1.setUnFocusView(oldFocus);
                    mEffectNoDrawBridge.setVisibleWidget(true);
                }
            }
        });
//


        viewpager.setOffscreenPageLimit(4);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switchTab(mOpenTabHost, position);
                OPENLOG.D("onPageSelected position:" + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // viewPager 正在滚动中.
                OPENLOG.D("onPageScrolled position:" + position + " positionOffset:" + positionOffset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(null != mOldView){
                    mainUpView1.setUnFocusView(mOldView);
                }
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE: // viewpager 滚动结束.
                        mainUpView1.setFocusView(mNewFocus, mOldView, 1.1f);
                        // 监听动画事件.
                        mEffectNoDrawBridge.setOnAnimatorListener(new OpenEffectBridge.NewAnimatorListener() {
                            @Override
                            public void onAnimationStart(OpenEffectBridge bridge, View view, Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(OpenEffectBridge bridge, View view, Animator animation) {
                                // 动画结束的时候恢复原来的时间. (这里只是DEMO)
                                mEffectNoDrawBridge.setTranDurAnimTime(OpenEffectBridge.DEFAULT_TRAN_DUR_ANIM);
                            }
                        });
                        // 让被挡住的焦点控件在前面.
                        if (mNewFocus != null)
                            mNewFocus.bringToFront();
                        OPENLOG.D("SCROLL_STATE_IDLE");
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        OPENLOG.D("SCROLL_STATE_DRAGGING");
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING: // viewPager开始滚动.
                        mEffectNoDrawBridge.clearAnimator(); // 清除之前的动画.
                        mEffectNoDrawBridge.setTranDurAnimTime(0); // 避免边框从其它地方跑出来.
                        OPENLOG.D("SCROLL_STATE_SETTLING");
                        break;
                }
            }
        });
        // 初始化.
        viewpager.setCurrentItem(0);
        switchTab(mOpenTabHost, 0);
    }

    private void initView4() {

        GV4 = (GridViewTV) view4.findViewById(R.id.gridView4);
        getData4(Config.vips.size());
        updateGridViewAdapter4();
        GV4.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //
        GV4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 这里注意要加判断是否为NULL.
                 * 因为在重新加载数据以后会出问题.
                 */
                if (view != null) {
                    mainUpView1.setFocusView(view, mOldView, 1.1f);
                }
                mOldView = view;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        GV4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "GridView Item " + position + " pos:" + mSavePos, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), VActivity.class);
                i.putExtra("title",Config.vips.get(position).entityId);
                i.putExtra("content",Config.vips.get(position).entityTytle);
                i.putExtra("img",Config.vips.get(position).entityImg);
                startActivity(i);
            }
        });
    }

    private void initView1() {
//        ImageView imageView = (ImageView)view1.findViewById(R.id.waitingImg);
//        ImageView imageView1 = (ImageView)view1.findViewById(R.id.waitingImg1);
//        ImageView imageView2 = (ImageView)view1.findViewById(R.id.waitingImg2);
//        ImageView imageView3 = (ImageView)view1.findViewById(R.id.waitingImg3);
//        ImageView imageView4 = (ImageView)view1.findViewById(R.id.waitingImg4);
        final String live = Config.liveId;
        if(live== null || "".equals(live)){
//        if(live!= null && !"".equals(live)){
/*            WebView webView1 = (WebView) view1.findViewById(R.id.webViewLive);
            iv.setVisibility(View.GONE);
            webView1.setVisibility(View.VISIBLE);
            webView1.getSettings().setJavaScriptEnabled(true);
            webView1.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            webView1.loadUrl(Config.LIVE_COMMING_URL);*/
            int index = (int)(1+Math.random()*5);

            ReflectItemView iv = (ReflectItemView)view1.findViewById(R.id.to_play);
            iv.setVisibility(View.GONE);
//            imageView.setVisibility(View.GONE);
//            imageView1.setVisibility(View.GONE);
//            imageView2.setVisibility(View.GONE);
//            imageView3.setVisibility(View.GONE);
//            imageView4.setVisibility(View.GONE);
//            ImageView[] imgs = {imageView,imageView1,imageView2,imageView3,imageView4};
//            imgs[index-1].setVisibility(View.VISIBLE);
            TextView tvPre = (TextView) view1.findViewById(R.id.livePreText);
            tvPre.setText(Config.buildPreText());
            TextView living = (TextView)view1.findViewById(R.id.textLiveLiving);
            living.setVisibility(View.GONE);
            TextView pre = (TextView)view1.findViewById(R.id.textLivePre);
            pre.setVisibility(View.VISIBLE);
        }else{
            ReflectItemView iv = (ReflectItemView)view1.findViewById(R.id.to_play);
//            imageView.setVisibility(View.GONE);
//            imageView1.setVisibility(View.GONE);
//            imageView2.setVisibility(View.GONE);
//            imageView3.setVisibility(View.GONE);
//            imageView4.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);
            TextView living = (TextView)view1.findViewById(R.id.textLiveLiving);
            living.setVisibility(View.VISIBLE);
            TextView pre = (TextView)view1.findViewById(R.id.textLivePre);
            pre.setVisibility(View.GONE);
            iv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (view != null) {
                        mainUpView1.setFocusView(view, mOldView, 1.1f);
                    }
                    mOldView = view;
                }
            });
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), YouTubePlayerActivity.class);
                    i.putExtra("key",live);
                    startActivity(i);
                }
            });
        }
    }

    private void initView3() {

        GV3 = (GridViewTV) view3.findViewById(R.id.gridView3);
        getData3(Config.playLists.size());
        updateGridViewAdapter3();
        GV3.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //
        GV3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /**
                 * 这里注意要加判断是否为NULL.
                 * 因为在重新加载数据以后会出问题.
                 */
                if (view != null) {
                    mainUpView1.setFocusView(view, mOldView, 1.1f);
                }
                mOldView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        GV3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ON Item Selectd called ...... position = [" + position + "]");
//                mFindhandler.removeCallbacksAndMessages(null);
//                mSavePos = position; // 保存原来的位置(不要按照我的抄，只是DEMO)
//                initGridViewData(new Random().nextInt(3));
//                mFindhandler.sendMessageDelayed(mFindhandler.obtainMessage(), 111);
//                Toast.makeText(getApplicationContext(), "GridView Item " + position + " pos:" + mSavePos, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), PlayListGridViewActivity.class);
                i.putExtra("plid",Config.playLists.get(position).entityId);
                i.putExtra("position",position);
                startActivity(i);
            }
        });
    }

    private void initView2() {

        GV2 = (GridViewTV) view2.findViewById(R.id.gridView2);
        getData2(Config.updateToday.size());
        updateGridViewAdapter2();
        GV2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //
        GV2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ON Item Selectd called ...... position = [" + position + "]");
                /**
                 * 这里注意要加判断是否为NULL.
                 * 因为在重新加载数据以后会出问题.
                 */
                if (view != null) {
                    mainUpView1.setFocusView(view, mOldView, 1.1f);
                }
                mOldView = view;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("");
            }


        });
        GV2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mFindhandler.removeCallbacksAndMessages(null);
//                mSavePos = position; // 保存原来的位置(不要按照我的抄，只是DEMO)
//                initGridViewData(new Random().nextInt(3));
//                mFindhandler.sendMessageDelayed(mFindhandler.obtainMessage(), 111);
//                Toast.makeText(getApplicationContext(), "GridView Item " + position + " pos:" + mSavePos, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), YouTubePlayerActivity.class);
                i.putExtra("key",Config.updateToday.get(position).entityId);
                i.putExtra("name",Config.updateToday.get(position).entityTytle);
                startActivity(i);
            }
        });
    }

    // 延时请求初始位置的item.
//    Handler mFirstHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
////            System.out.println("Handler MSG called ..........");
//            GV2.setDefualtSelect(0);
//            GV3.setDefualtSelect(0);
//        }
//    };

//    // 更新数据后还原焦点框.
//    Handler mFindhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (mSavePos != -1) {
//                gridView.requestFocusFromTouch();
//                gridView.setSelection(mSavePos);
//            }
//        }
//    };

    public List<String> getData2(int count) {
        data2 = new ArrayList<String>();
        List<DisplayBase> ut = Config.updateToday;

        for (int i = 0; i < count; i++) {
            data2.add(ut.get(i).entityTytle);
        }
        return data2;
    }

    public List<String> getData3(int count) {
        data3 = new ArrayList<String>();
        List<DisplayBase> dbList = Config.playLists;

        for (int i = 0; i < count; i++) {
            data3.add(dbList.get(i).entityTytle);
        }
        return data3;
    }

    public List<String> getData4(int count) {
        data4 = new ArrayList<String>();
        List<DisplayBase> dbList = Config.vips;

        for (int i = 0; i < count; i++) {
            data4.add(dbList.get(i).entityTytle.replace(".jpg",""));
        }
        return data4;
    }
    private void updateGridViewAdapter2() {
        mAdapter2 = new GridViewAdapter(view2.getContext(), data2,"2");
        GV2.setAdapter(mAdapter2);
        mAdapter2.notifyDataSetChanged();
    }

    private void updateGridViewAdapter3() {
        mAdapter3 = new GridViewAdapter(view3.getContext(), data3 , "3");
        GV3.setAdapter(mAdapter3);
        mAdapter3.notifyDataSetChanged();
    }

    private void updateGridViewAdapter4() {
        mAdapter4 = new GridViewAdapter(view4.getContext(), data4 , "4");
        GV4.setAdapter(mAdapter4);
        mAdapter4.notifyDataSetChanged();
    }

    @Override
    public void onTabSelect(OpenTabHost openTabHost, View titleWidget, int position) {
        if (viewpager != null) {
            viewpager.setCurrentItem(position);
        }
    }

    /**
     * demo (翻页的时候改变状态)
     * 将标题栏的文字颜色改变. <br>
     * 你可以写自己的东西，我这里只是DEMO.
     */
    public void switchTab(OpenTabHost openTabHost, int postion) {
        List<View> viewList = openTabHost.getAllTitleView();
        for (int i = 0; i < viewList.size(); i++) {
            TextViewWithTTF view = (TextViewWithTTF) openTabHost.getTitleViewIndexAt(i);
            if (view != null) {
                Resources res = view.getResources();
                if (res != null) {
                    if (i == postion) {
                        if(postion == 0){
                            initView1();
                        }
                        if(postion == 1 && data2.size() <= 0){
                            initView2();
                        }
                        if(postion == 2 && data3.size() <= 0){
                            initView3();
                        }
                        if(postion == 3 && data4.size() <= 0){
                            initView4();
                        }
                        view.setTextColor(res.getColor(android.R.color.white));
                        view.setTypeface(null, Typeface.BOLD);
                        view.setSelected(true); // 为了显示 失去焦点，选中为 true的图片.
                    } else {
                        view.setTextColor(res.getColor(R.color.white_50));
                        view.setTypeface(null, Typeface.NORMAL);
                        view.setSelected(false);
                    }
                }
            }
        }
    }

    private float getDimension(int id) {
        return getResources().getDimension(id);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /**
     * viewpager 的 adpater.
     */
    class DemoPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

    }

    class GridViewAdapter extends BaseAdapter {

        private List<String> mDatas;
        private final LayoutInflater mInflater;
        private String thisStep;

        private int POS0 = 1;

        public GridViewAdapter(Context context, List<String> data, String step) {
            thisStep = step;
            mDatas = data;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {

            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
//            System.out.println("getItem Method Called .......... position is : " + position);
            return data2.get(position);
        }

        @Override
        public long getItemId(int position) {
//            System.out.println("getItemId Method Called ..........Position is : " + position);
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            System.out.println("MainView getView Method Called ...... + position = [" + position + "]");
            GridViewAdapter.ViewHolder viewHolder = null;
//            if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_gridview, parent, false);
            convertView.setTag(new GridViewAdapter.ViewHolder(convertView));
//            }
            viewHolder = (GridViewAdapter.ViewHolder) convertView.getTag();
            bindViewData(position, viewHolder);
            return convertView;
        }

        private void bindViewData(int position, GridViewAdapter.ViewHolder viewHolder) {
            String title = mDatas.get(position);
//            System.out.println("Bind View Data ......Position = [" + position + "]， Titie = [" + title + "]");
            viewHolder.titleTv.setText(title);
            ImageView iv = viewHolder.imageView;
            if("2".equals(thisStep)){
                LoadImage(iv,Config.updateToday.get(position).entityImg);
//                System.out.println("Position = :【" + position + "】，" + Config.playLists.get(position).entityImg + "-----------------" + Config.playLists.get(position).entityId);
            }
            if("3".equals(thisStep)){
                LoadImage(iv,Config.playLists.get(position).entityImg);
//                System.out.println("Position = :【" + position + "】，" + Config.playLists.get(position).entityImg + "-----------------" + Config.playLists.get(position).entityId);
            }
            if("4".equals(thisStep)){
                LoadImage(iv,Config.vips.get(position).entityImg);
//                System.out.println("Position = :【" + position + "】，" + Config.playLists.get(position).entityImg + "-----------------" + Config.playLists.get(position).entityId);
            }
        }

        class ViewHolder {
            View itemView;
            TextView titleTv;
            ImageView imageView;

            public ViewHolder(View view) {
                this.itemView = view;
                this.titleTv = (TextView) view.findViewById(R.id.textView);
                this.imageView = (ImageView)view.findViewById(R.id.imgView);
            }
        }
    }
    private void LoadImage(ImageView img, String path){
        if(img.getDrawable() == null){
            //异步加载图片资源
            AsyncTaskImageLoad async = new AsyncTaskImageLoad(img);
            //执行异步加载，并把图片的路径传送过去
            async.execute(path);
        }
    }
}
