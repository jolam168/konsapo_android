package com.asuha.konkatsuten;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.asuha.konkatsuten.dummy.NoticeListContent;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyItemRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private RecyclerRefreshLayout refreshLayout;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        recyclerView = rootView.findViewById(R.id.list);
        emptyView = rootView.findViewById(R.id.id_empty_view);
        adapter = new MyItemRecyclerViewAdapter(NoticeListContent.ITEMS,mListener);

//        refreshLayout = rootView.findViewById(R.id.refresh_layout);

//        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getData();
//            }
//        });



        getData();

        return rootView;
    }

    private void getData(){
        String url = "https://onesignal.com/api/v1/notifications?app_id=d5f14639-242c-421a-bc5f-76bf2841601e&limit=50&offset=0";
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        mTxtDisplay.setText("Response: " + response.toString());
                        Log.i("JSON Response",response.toString());
                        try {
                            JSONArray n = response.getJSONArray("notifications");
                            Integer count = response.getInt("total_count");
                            Log.i("Response count:",count.toString());
                            NoticeListContent.ITEMS.clear();
                            for (int i = 0; i < n.length(); i++) {

                                JSONObject jb = n.getJSONObject(i);
                                String contents = jb.getJSONObject("contents").optString("en");
                                String headings = jb.getJSONObject("headings").optString("en");
                                String url = jb.optString("url");
                                Boolean isAndroid = jb.getBoolean("isAndroid");
                                Long sendAfter = jb.getLong("send_after");
                                Date date = new Date(sendAfter);
                                Date currentTime = Calendar.getInstance().getTime();

                                if(isAndroid &&  currentTime.after(date)) {
                                    NoticeListContent.NoticeItem item = new NoticeListContent.NoticeItem(headings, contents, sendAfter,url);
                                    NoticeListContent.addItem(item);
                                }
                            }

                            // Set the adapter
                                Context context = recyclerView.getContext();
                                recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).build());
//                                recyclerView.setEmptyView(emptyView);
                                if (mColumnCount <= 1) {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                } else {
                                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                                }
                                if (recyclerView.getAdapter()==null){
                                    recyclerView.setAdapter(adapter);
                                }else{
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }

//                            refreshLayout.setRefreshing(false);
                            if(adapter.isEmpty()){
                                Log.i("Empty","Empty");
//                                emptyView.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
//                            refreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("JSON Response Error",error.toString());
//                        refreshLayout.setRefreshing(false);
                    }


                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                // Add BASIC AUTH HEADER
                Map<String, String> newHeaders = new HashMap<String, String>();
                newHeaders.putAll(headers);
                newHeaders.put("Authorization", "Basic ZjllZWFkMTYtNmEwMy00ZGQxLWI5OTItNDg1YTU3ODgwMTYy");
                return newHeaders;
            };
        };
        queue.add(jsObjRequest);
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
        void onListFragmentInteraction(NoticeListContent.NoticeItem item);
    }
}
