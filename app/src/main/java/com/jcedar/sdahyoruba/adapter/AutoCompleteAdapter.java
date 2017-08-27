package com.jcedar.sdahyoruba.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.helper.FileUtils;
import com.jcedar.sdahyoruba.io.model.Hymn;
import com.jcedar.sdahyoruba.provider.DataContract;

import java.util.ArrayList;

/**
 * Created by Afolayan Oluwaseyi on 24/10/2016.
 */
public class AutoCompleteAdapter extends CursorAdapter {
    ArrayList<Hymn> hymns;
    Context context;

    public AutoCompleteAdapter(Context context, Cursor c) {
        super(context, c, true);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor c = (Cursor) getItem(position);
        if( convertView == null ){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_hymn_search, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById( android.R.id.text1 );

        Hymn hymn = new Hymn();
        hymn.setSongId(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_ID)));
        hymn.setSongTitle(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_NAME)));
        hymn.setSongText(c.getString(c.getColumnIndex(DataContract.Hymns.SONG_TEXT)));
        hymn.setEnglishVersion(c.getString(c.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION)));

        String id = hymn.getSongId().trim();
        String title = FileUtils.removeBrackets(hymn.getSongTitle());
        String searchedString = id+": "+title;

        tvName.setText( searchedString );
        return convertView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public void setHymns(ArrayList<Hymn> hymns) {
        this.hymns = hymns;
    }
}
