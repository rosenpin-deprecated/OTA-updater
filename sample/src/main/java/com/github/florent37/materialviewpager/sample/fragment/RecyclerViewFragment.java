package com.github.florent37.materialviewpager.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.utils.CardsRecyclerViewAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private static final int ITEM_COUNT = 10;

    private List<Object> mContentItems = new ArrayList<>();

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Parse.initialize(getActivity().getApplicationContext(), "LEuTfc5BQIC9E89F9u0J5ZfwZeGibiYTutcqK41j", "kQQ9CS4Wb7DzF4qooYodTHSmU1RFGfJNLthjzxHZ");
        ParseQuery<ParseObject> query = new ParseQuery<>(DeviceModel());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                mAdapter = new RecyclerViewMaterialAdapter(new CardsRecyclerViewAdapter(parseObjects,getActivity()));
                mRecyclerView.setAdapter(mAdapter);

                MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
            }
        });


    }

    public static String DeviceModel() {
        //return Build.PRODUCT;
        return "hammerhead";
    }
    public RecyclerView getmRecyclerView(){
        return this.mRecyclerView;
    }
}
