package com.jcedar.sdahyoruba.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.helper.FileUtils;
import com.jcedar.sdahyoruba.helper.FormatUtils;
import com.jcedar.sdahyoruba.io.model.Hymn;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Afolayan Oluwaseyi on 31/12/2016.
 */
public class HymnListRecyclerViewAdapter extends RecyclerView.Adapter<HymnListRecyclerViewAdapter.HymnViewHolder> implements View.OnClickListener {

    private final LayoutInflater layoutInflater;

    public Context context;
    ArrayList<Hymn> allHymns;

    private HymnViewHolder holder;
    private OnItemClickListener onItemClickListener;

    public HymnListRecyclerViewAdapter(final Context context, ArrayList<Hymn> hymns) {
        super();

        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        allHymns = hymns;

    }



    @Override
    public HymnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = this.layoutInflater.inflate(R.layout.list_n_item, parent, false);
        view.setOnClickListener(this);

        return new HymnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HymnViewHolder holder, int position) {
         Hymn hymn = allHymns.get(position);
        String songId = hymn.getSongId();
        String songTitle = hymn.getSongTitle();
        String engV = hymn.getEnglishVersion();

        String display = songId +" - "+ FileUtils.removeBrackets(FormatUtils.ellipsize(songTitle, 35));
        holder.textViewName.setText(display);
        holder.textViewEnglish.setText(engV);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return allHymns.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        if (this.onItemClickListener != null) {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                final Hymn hymn = allHymns.get(position);
                this.onItemClickListener.onItemClicked(Integer.parseInt(hymn.getSongId()));
            }
        }
    }

    public class HymnViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.lblName)
        TextView textViewName;

        @Bind(R.id.lblEnglish)
        TextView textViewEnglish;

        @Bind(R.id.card_view)
        CardView cardView;

        Context context;

        View view;

        public HymnViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, itemView);

        }


        public void bindData(final Hymn hymn) {
            /*String userId = cursor.getString(
                    cursor.getColumnIndex(DataContract.Hymns._ID));

            final String num = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.SONG_ID));
            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.SONG_NAME));

            String display = num +" - "+ FileUtils.removeBrackets(FormatUtils.ellipsize(name, 35));

            this.textViewName.setText(display);


            final String eng = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION));
            this.textViewEnglish.setText(eng);*/


        }
        public void setTag(String string){
            this.view.setTag( string);
        }
        public String getTag(){
            return this.view.getTag().toString();
        }

    }
}
