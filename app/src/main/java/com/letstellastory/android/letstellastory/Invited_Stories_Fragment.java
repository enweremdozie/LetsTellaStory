package com.letstellastory.android.letstellastory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class Invited_Stories_Fragment extends Fragment {
    private StaggeredGridLayoutManager _sGridLayoutManager;
    String story = "";
    String genre = "";

    public Invited_Stories_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invited__stories_, container, false);
        GridView gridview = (GridView)view.findViewById(R.id.gridview);
        List<ItemObject> sList = getListItemData();
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), sList);
        gridview.setAdapter(customAdapter);
        //RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //recyclerView.setHasFixedSize(true);

        //_sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //recyclerView.setLayoutManager(_sGridLayoutManager);



        //StoryRecycler rcAdapter = new StoryRecycler(getActivity(), sList);
        //recyclerView.setAdapter(rcAdapter);


        return view;


    }


    private List<ItemObject> getListItemData() {
        //Intent intent = getIntent();
        //story = intent.getExtras().getString("title");
        //genre = intent.getExtras().getString("genre");
        List<ItemObject> listViewItems = new ArrayList<ItemObject>();
        listViewItems.add(new ItemObject("story", "genre"));
        /*listViewItems.add(new ItemObject("Pride and Prejudice", "Horror"));
        listViewItems.add(new ItemObject("One Hundred Years of Solitude", "Romance"));
        listViewItems.add(new ItemObject("The Book Thief", "Romance"));
        listViewItems.add(new ItemObject("The Hunger Games", "Horror"));
        listViewItems.add(new ItemObject("The Hitchhiker's Guide to the Galaxy", "Drama"));
        listViewItems.add(new ItemObject("The Theory Of Everything", "Romance"));*/

        return listViewItems;
    }


}