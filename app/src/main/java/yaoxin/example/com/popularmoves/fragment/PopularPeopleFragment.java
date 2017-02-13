package yaoxin.example.com.popularmoves.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import yaoxin.example.com.popularmoves.PopularPeopleDetailActivity;
import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.bean.PopularPeople;
import yaoxin.example.com.popularmoves.fragment.support.PopluarPeopleRunnable;
import yaoxin.example.com.popularmoves.fragment.support.PopularPeopleAdapter;
import yaoxin.example.com.popularmoves.support.ThreadPoolManager;

/**
 * Created by yaoxinxin on 2017/2/9.
 * <p>
 * popular people
 */

public class PopularPeopleFragment extends Fragment {

    private static final String TAG = "PopularPeopleFragment";

    public static final String BASEURL =
            "https://api.themoviedb.org/3/person/popular?language=en-US";


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PopularPeopleAdapter mAdapter;

    private int currentPage = 1;
    private List<PopularPeople> allPeoples = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PopluarPeopleRunnable.TASK_POPULARPEOPLE) {
                swipeRefreshLayout.setRefreshing(false);
                if (msg.obj != null) {
                    List<PopularPeople> peoples = (List<PopularPeople>) msg.obj;
                    allPeoples.addAll(peoples);
                    mAdapter.setData(allPeoples);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popularpeople, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.popularPeople);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        PopluarPeopleRunnable runnable = new PopluarPeopleRunnable(BASEURL, currentPage, handler);
        ThreadPoolManager.newInstance().creataPool().run(runnable);
        swipeRefreshLayout.setEnabled(false);//禁止下拉刷新
        recyclerView.addOnScrollListener(new EndlessLoadMore(linearLayoutManager));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new PopularPeopleAdapter(getActivity(), allPeoples);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickPopularPeopleListener(new PopularPeopleAdapter.OnClickPopularPeopleListener() {
            @Override
            public void click(View v, PopularPeople people) {
                Log.i(TAG, "name =" + people.name);
                Intent intent = new Intent(getActivity(), PopularPeopleDetailActivity.class);
                intent.putExtra("popularpeople", people);
                startActivity(intent);
            }
        });
    }

    /**
     * 下拉加载更多 interface
     */
    class EndlessLoadMore extends RecyclerView.OnScrollListener {


        private LinearLayoutManager linearLayoutManager;

        private int previousCount;

        private int totalItem;

        private boolean isloading = true;


        public EndlessLoadMore(LinearLayoutManager linearLayoutManager) {
            super();
            this.linearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totalItem = linearLayoutManager.getItemCount();
            int visiablecount = recyclerView.getChildCount();
            int firstvisablepos = linearLayoutManager.findFirstVisibleItemPosition();

            if (totalItem > previousCount) {//当前已加载完毕
                isloading = false;
                previousCount = totalItem;
            }

            if (!isloading && firstvisablepos + visiablecount >= totalItem) {//为什么是>=
                isloading = true;
                currentPage++;
                onLoadMore(currentPage);
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        private void onLoadMore(int page) {
            PopluarPeopleRunnable runnable = new PopluarPeopleRunnable(BASEURL, page, handler);
            ThreadPoolManager.newInstance().creataPool().run(runnable);
        }
    }
}
