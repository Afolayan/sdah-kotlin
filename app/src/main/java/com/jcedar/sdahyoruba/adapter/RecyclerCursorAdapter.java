package com.jcedar.sdahyoruba.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.helper.FileUtils;
import com.jcedar.sdahyoruba.helper.FormatUtils;
import com.jcedar.sdahyoruba.provider.DataContract;
import com.jcedar.sdahyoruba.ui.FavoriteListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecyclerCursorAdapter extends RecyclerViewCursorAdapter<RecyclerCursorAdapter.HymnViewHolder>
        implements OnClickListener, View.OnLongClickListener {

    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private OnLongClickListener onLongClickListener;

    private FavoriteListFragment mFragment;
    public Context context;

    public MultiSelector mMultiSelector = new MultiSelector();
    private HymnViewHolder holder;
    ModalMultiSelectorCallback multiSelectorCallback = new MyModalSelector(mMultiSelector);

    public RecyclerCursorAdapter(final Context context, Fragment fragment) {
        super();

        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;

        if (fragment instanceof FavoriteListFragment) {
            this.mFragment =  (FavoriteListFragment) fragment;
        } else {
            this.mFragment = null;
        }
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnLongClickListener(final OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }



    @Override
    public HymnViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = this.layoutInflater.inflate(R.layout.list_n_item, parent, false);
        view.setOnClickListener(this);

        return new HymnViewHolder(view, mMultiSelector, context);
    }

    @Override
    public void onBindViewHolder(final HymnViewHolder holder, final Cursor cursor) {
        holder.bindData(cursor);
        String id = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.SONG_ID));
        holder.setTag(id);
        this.holder = holder;
        /*holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onLongClicks(v);
            }
        });*/
        
    }

     /*
     * View.OnClickListener
     */

    @Override
    public void onClick(final View view)
    {
        if( !mMultiSelector.tapSelection(holder)){
            if (this.onItemClickListener != null) {
                final RecyclerView recyclerView = (RecyclerView) view.getParent();
                final int position = recyclerView.getChildLayoutPosition(view);
                if (position != RecyclerView.NO_POSITION) {
                    final Cursor cursor = this.getItem(position);
                    this.onItemClickListener.onItemClicked(cursor);
                }
             }
        }

    }

    public boolean onLongClicks(View v) {
        if (mFragment != null) {

            Log.e("Adapter TAG", "selected id tag == " + v.getTag());

            if (!mMultiSelector.isSelectable()) {
                ((AppCompatActivity) context).startSupportActionMode(multiSelectorCallback);
                mMultiSelector.setSelectable(true);
                mMultiSelector.setSelected(holder, true);
                this.onLongClickListener.onLongClicked(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        onLongClicks(v);
        return false;
    }


    public class HymnViewHolder extends SwappingHolder {

        @Bind(R.id.lblName)
        TextView textViewName;

        @Bind(R.id.lblEnglish)
        TextView textViewEnglish;

        @Bind(R.id.card_view)
        CardView cardView;

        Context context;

        View view;

        public HymnViewHolder(View itemView, MultiSelector multiSelector, Context context) {
            super(itemView, multiSelector);
            this.context = context;
            this.view = itemView;
            ButterKnife.bind(this, itemView);
            itemView.setLongClickable(true);

        }


        public void bindData(final Cursor cursor) {
            String userId = cursor.getString(
                    cursor.getColumnIndex(DataContract.Hymns._ID));

            final String num = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.SONG_ID));
            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.SONG_NAME));

            String display = num +" - "+FileUtils.removeBrackets(FormatUtils.ellipsize(name, 35));

            this.textViewName.setText(display);


            final String eng = cursor.getString(cursor.getColumnIndex(DataContract.Hymns.ENGLISH_VERSION));
            this.textViewEnglish.setText(eng);


        }
        public void setTag(String string){
            this.view.setTag( string);
        }
        public String getTag(){
           return this.view.getTag().toString();
        }


        public boolean onLongClicks(View v) {
            if( !mMultiSelector.isSelectable()){
                ((AppCompatActivity) context).startSupportActionMode(multiSelectorCallback);
                mMultiSelector.setSelectable(true);
                mMultiSelector.setSelected(HymnViewHolder.this, true);
                return true;
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(Cursor cursor);
    }
    public interface OnLongClickListener {
        void onLongClicked(boolean clicked);
    }


    public class MyModalSelector extends ModalMultiSelectorCallback{

        List<Integer> mOptionList = setOptionLists();
        public MyModalSelector(MultiSelector multiSelector) {
            super(multiSelector);
        }

        private List<Integer> setOptionLists() {
            List<Integer> mOptionList = new ArrayList<>();
            mOptionList.add(R.id.action_delete);
            mOptionList.add(R.id.action_count);
            return mOptionList;
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            super.onDestroyActionMode(actionMode);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            super.onCreateActionMode(mode, menu);
            mode.getMenuInflater().inflate(R.menu.menu_favorite_action, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            for (int i = 0; i < menu.size(); i++){
                MenuItem item = menu.getItem(i);
                if( !mOptionList.contains(item.getItemId()))
                    item.setVisible(false);
            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch ( item.getItemId() ){
                case R.id.action_delete:
                    Toast.makeText(context, R.string.action_delete, Toast.LENGTH_SHORT).show();

                    mode.finish();

                    for(int i = mFragment.recyclerView.getAdapter().getItemCount(); i >= 0; i--){
                        if(mMultiSelector.isSelected(i, 0)){

                            //String id = viewHolder.getTag();
                            Log.e("TAG Action Item", " tag == id == "+i);

                            context.getContentResolver()
                                    .delete(DataContract.FavoriteHymns.CONTENT_URI,
                                            DataContract.FavoriteHymns.SONG_ID + "=?",
                                            new String[]{i + ""});
                            mFragment.recyclerView.getAdapter().notifyItemRemoved(i);
                        }
                    }
                    mMultiSelector.clearSelections();
                    mFragment.updateFavList();
                    return true;

                case R.id.action_count:
                    mode.finish();
                    break;

            }
            return false;
        }

    }
}