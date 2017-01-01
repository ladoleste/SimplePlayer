package br.com.ladoleste.simpleplayer;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Anderson Silva on 23/12/2016.
 */

class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    private Context context;
    private List<String> songs;
    private int layout;

    SongsAdapter(Context context, List<String> songs, @LayoutRes int layout) {
        this.context = context;
        this.songs = songs;
        this.layout = layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
