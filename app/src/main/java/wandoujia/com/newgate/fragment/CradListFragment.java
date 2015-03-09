package wandoujia.com.newgate.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.LinkedList;

import wandoujia.com.newgate.R;
import wandoujia.com.newgate.activity.FeedActivity;
import wandoujia.com.newgate.adapter.CustomCardListAdapter;
import wandoujia.com.newgate.adapter.RecyclerHeaderAdapter;
import wandoujia.com.newgate.listener.EndlessRecyclerOnScrollListener;
import wandoujia.com.newgate.model.News;
import wandoujia.com.newgate.util.NewsDataServices;
import wandoujia.com.newgate.util.NewsServices;
import wandoujia.com.newgate.util.Utils;


public class CradListFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private LinkedList<News> newsList = new LinkedList<News>();

    private RecyclerView mRecyclerView;
    private NewsServices newsDataServices;
    private SwipeRefreshLayout mRefreshLayout;
    private CustomCardListAdapter mAdapter;
    private LinearLayout mToolbarContainer;
    private int mToolbarHeight;

    private StickyHeadersItemDecoration top;
    private Tracker t;

    private int position;

    public static CradListFragment newInstance(int position) {
        CradListFragment f = new CradListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    private void setLoadingAppearance() {
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_home_cards, container, false);
        final FeedActivity c = (FeedActivity) getActivity();

        mToolbarContainer = (LinearLayout) c.findViewById(R.id.toolbar_container);
        mToolbarHeight = Utils.getToolbarHeight(c);

        // pull down to refresh
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                    mRefreshLayout.setRefreshing(false);
                    newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_PULLDOWN);

                    if(t != null){
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.ga_category_home))
                                .setAction(getString(R.string.ga_action_pulldown))
                                .setLabel(getString(R.string.ga_label_loadmore))
                                .build());
                    }
                    }
                }, 2000);
            }
        });
        setLoadingAppearance();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.card_list);

        final int paddingTop = Utils.getToolbarHeight(getActivity()) + Utils.getTabsHeight(getActivity());
        mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), paddingTop, mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CustomCardListAdapter(newsList);
        mAdapter.setHasStableIds(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(c, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        final int tabHeight = Utils.getTabsHeight(getActivity());
        final int actionbarHeight = Utils.getToolbarHeight(getActivity());

        top = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(new RecyclerHeaderAdapter(newsList))
                .build();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(top);

        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager, getActivity()) {
            private int lastY;
            @Override
            public void onMoved(int distance) {
                mToolbarContainer.setTranslationY(-distance);
            }

            @Override
            public void onShow() {
                mToolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        top.setOffsetAdjustment(tabHeight + actionbarHeight);
                    }
                }, 200);
            }

            @Override
            public void onHide() {
                mToolbarContainer.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        top.setOffsetAdjustment(tabHeight);
                    }
                }, 200);
            }
            @Override
            public void onLoadMore(int current_page) {
                newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_SCROLL);
                if(t != null){
                    t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.ga_category_home))
                        .setAction(getString(R.string.ga_action_scroll))
                        .setLabel(getString(R.string.ga_label_loadmore))
                        .build());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                top.setOffsetAdjustment(mToolbarContainer.getMeasuredHeight() + Math.round(mToolbarContainer.getTranslationY()));
            }

        });

        new Thread(new Runnable() {
            @Override
            public void run() {
            c.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                newsDataServices = new NewsServices(c, newsList, mRefreshLayout, mAdapter);
                newsDataServices.requestHomeNews(NewsDataServices.REGRESH_FLAG_INIT);
                }
            });
            }
        }).start();
        return view;
    }

}
