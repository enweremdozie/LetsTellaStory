package com.letstellastory.android.letstellastory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by dozie on 2017-06-30.
 */

public class StoryView extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    public TextView storyName;
    public TextView genreName;

    public StoryView(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        storyName = (TextView) itemView.findViewById(R.id.StoryName);
        genreName = (TextView) itemView.findViewById(R.id.AuthorName);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(),
                "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT)
                .show();
    }

}