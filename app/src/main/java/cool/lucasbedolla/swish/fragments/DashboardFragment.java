package cool.lucasbedolla.swish.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cool.lucasbedolla.swish.R;
import cool.lucasbedolla.swish.activities.MainActivity;
import cool.lucasbedolla.swish.adapter.RecyclerAdapter;
import cool.lucasbedolla.swish.http.FetchTumblrPostsTask;
import cool.lucasbedolla.swish.listeners.FetchPostListener;
import cool.lucasbedolla.swish.listeners.FragmentEventController;
import cool.lucasbedolla.swish.util.MyPrefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment
        extends Fragment implements FetchPostListener, View.OnTouchListener, View.OnClickListener {

    public static final int ID = 0;

    int firstVisibleItem, visibleItemCount, totalItemCount;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView.LayoutManager manager;
    private List<Post> loadedPosts = new ArrayList<>();
    private RecyclerView recyclerViewMain;
    private RecyclerAdapter adapter;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;

    private View lay;

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshLayout.setRefreshing(true);
            loadedPosts.clear();
            fetchPosts(getActivity(), loadedPosts.size(), ((MainActivity) getActivity()), FetchTumblrPostsTask.DASHBOARD);
        }
    };

    private ImageView loadingLottie;


    private void fetchPosts(Context ctx, int postSize, FetchPostListener listener, int actionID) {

        new FetchTumblrPostsTask().execute(ctx, postSize, listener, actionID);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_dashboard, container, false);

        loadingLottie = layout.findViewById(R.id.loading_lottie);

        // pull to refresh layout config
        refreshLayout = layout.findViewById(R.id.refresher);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setColorSchemeResources(R.color.blue, R.color.orange, R.color.red, R.color.charcoal);
        refreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        refreshLayout.setProgressViewOffset(false, 0, 225);
        refreshLayout.setOnRefreshListener(refreshListener);

        //init menu buttons
        layout.findViewById(R.id.menu_dash).setOnClickListener(this);
        layout.findViewById(R.id.menu_search).setOnClickListener(this);
        layout.findViewById(R.id.menu_spark).setOnClickListener(this);
        layout.findViewById(R.id.menu_profile).setOnClickListener(this);
        //recyclerview config
        recyclerViewMain = layout.findViewById(R.id.recycler);
        recyclerViewMain.setItemViewCacheSize(4);
        recyclerViewMain.setDrawingCacheEnabled(true);
        recyclerViewMain.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        if (MyPrefs.getIsDualMode(getActivity())) {
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            ((StaggeredGridLayoutManager) manager).setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerViewMain.setLayoutManager(manager);
        } else {
            manager = new LinearLayoutManager(getActivity());
            ((LinearLayoutManager) manager).setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewMain.setLayoutManager(manager);
            setOnScroll();
        }

        fetchPosts(getActivity(), loadedPosts.size(), this, FetchTumblrPostsTask.DASHBOARD);

        return layout;
    }

    private void setOnScroll() {
        recyclerViewMain.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (MyPrefs.getIsDualMode(getActivity())) {
                    visibleItemCount = recyclerViewMain.getChildCount();
                    totalItemCount = manager.getItemCount();
                    firstVisibleItem = ((StaggeredGridLayoutManager) manager).findFirstCompletelyVisibleItemPositions(null)[0];
                } else {
                    visibleItemCount = recyclerViewMain.getChildCount();
                    totalItemCount = manager.getItemCount();
                    firstVisibleItem = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                }

                if (loading) {
                    if (totalItemCount >= previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {

                    fetchPosts(getActivity(), loadedPosts.size(), (MainActivity) getActivity(), FetchTumblrPostsTask.DASHBOARD);

                    loading = true;
                }
            }
        });
    }

    @Override
    public void fetchedPosts(List<Post> fetchedPosts) {

        if (loadingLottie.getVisibility() == View.VISIBLE) {
            loadingLottie.setVisibility(View.GONE);
            loadingLottie.clearAnimation();
        }

        int initialPostSize = loadedPosts.size();

        for (Post post : fetchedPosts) {
            if (post instanceof PhotoPost) {
                loadedPosts.add(post);
            }
        }

        if (initialPostSize == 0) {
            //recycleradapter config
            adapter = new RecyclerAdapter((MainActivity) getActivity(), loadedPosts);
            recyclerViewMain.setAdapter(adapter);
            refreshLayout.setRefreshing(false);

        } else {
            adapter.notifyItemRangeChanged(0, loadedPosts.size());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void fetchFailed(Exception e) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        ((FragmentEventController) getActivity()).submitEvent(ID, v, 0);
    }
}
