package yaoxin.example.com.popularmoves.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import yaoxin.example.com.popularmoves.MainActivity;
import yaoxin.example.com.popularmoves.R;
import yaoxin.example.com.popularmoves.fragment.dummy.Move;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "ItemFragement";
    private static final int GET_MOVES_OK = 200;


    private static final int REQUEST_CODE = 100;
    private static final int SORT_POPULAR = 301;
    private static final int SORT_VOTE = 302;

    private ProgressBar mProgressBar;
    private TextView mText;

    private String base_url;
    private String apikey;
    private String currentBaseUrl = "";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    private List<Move> moves;
    private MyItemRecyclerViewAdapter mAdapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_MOVES_OK) {
                if (msg.obj != null) {
                    mProgressBar.setVisibility(View.GONE);
                    List<Move> moves = (List<Move>) msg.obj;
                    mAdapter.setmValues(moves);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")

    public static ItemFragment newInstance(String url, String apikey,int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString("base_url", url);
        args.putString("apikey", apikey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            base_url = getArguments().getString("base_url");
            apikey = getArguments().getString("apikey");
            this.currentBaseUrl = base_url;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.list) ;
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            mText = (TextView) view.findViewById(R.id.pleaseOnline);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


//            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener));
            mAdapter = new MyItemRecyclerViewAdapter(getActivity(), moves, mListener);
            recyclerView.setAdapter(mAdapter);


            ((MainActivity)getActivity()).setRefreshListener(new RefreshListener() {
                @Override
                public void refresh() {
                    System.out.print("refresh");
                    if(!currentBaseUrl.isEmpty()){
                        if (((MainActivity) getActivity()).isOnline()) {
                            mText.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.VISIBLE);
                            ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
                            task.execute(currentBaseUrl, apikey);
                        }else{
                            mText.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });

            if(!((MainActivity) getActivity()).isOnline()){
                mProgressBar.setVisibility(View.INVISIBLE);
                mText.setVisibility(View.VISIBLE);
//                recyclerView.setVisibility(View.INVISIBLE);
            }else{
                mProgressBar.setVisibility(View.VISIBLE);
                mText.setVisibility(View.GONE);
            }
        }
        ParseAsyncTask task = new ParseAsyncTask(handler, getActivity());
        task.execute(base_url, apikey);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Fragment onactivityResult");

        ParseAsyncTask task = null;
        String base_url = "";

        mProgressBar.setVisibility(View.VISIBLE);

        if (resultCode == SORT_POPULAR) {

            task = new ParseAsyncTask(handler, getActivity());
            base_url = ((MainActivity) getActivity()).popularurl;

        } else if (resultCode == SORT_VOTE) {

            task = new ParseAsyncTask(handler, getActivity());
            base_url = ((MainActivity) getActivity()).voteUrl;

        } else {

            //do nothing

        }
        System.out.println(base_url);
        this.currentBaseUrl = base_url;
        task.execute(base_url, ((MainActivity) getActivity()).apikey);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLitFragmentInteraction(Move item);
    }


}
