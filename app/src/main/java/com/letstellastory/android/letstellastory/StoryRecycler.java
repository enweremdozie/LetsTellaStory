package com.letstellastory.android.letstellastory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;


    public class StoryRecycler extends RecyclerView.Adapter<StoryView>
    {
        private List<ItemObject> itemList;
        private Context context;

        public StoryRecycler(Context context,
                                         List<ItemObject> itemList)
        {
            this.itemList = itemList;
            this.context = context;
        }

        @Override
        public StoryView onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.story_grid_layout, null);
            StoryView rcv = new StoryView(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(StoryView holder, int position)
        {
            holder.storyName.setText(itemList.get(position).getName());
            holder.genreName.setText(itemList.get(position).getGenre());
        }

        @Override
        public int getItemCount()
        {
            return this.itemList.size();
        }
    }


