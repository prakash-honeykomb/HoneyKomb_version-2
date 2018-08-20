package com.honeykomb.honeykomb.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.SelectedContactObject;
import com.honeykomb.honeykomb.utils.Util;
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleCursorRecyclerAdapter extends CursorAdapter {

//    private ArrayList<SelectedContactObject> listWithHkIDTemp;
    @NonNull
    private OnItemCheckListener onItemCheckListener;
    private ArrayList<SelectedContactObject> mFilteredList;
    private Context context;
    private Cursor mCursor,mCursor1;
    int groupSize;
    TextView txtViewSelectedUsers;
    private HashMap<Long, Boolean> check_status = new HashMap<Long, Boolean>();
//TextView txtView;
    public SimpleCursorRecyclerAdapter(Context context, Cursor cursor, @NonNull OnItemCheckListener onItemCheckListener/*, ArrayList<SelectedContactObject> listWithHkIDTemp*/) {
        super(context, cursor, 0);
        mFilteredList = new ArrayList<>();
        this.onItemCheckListener = onItemCheckListener;
        this.context = context;
        this.mCursor = cursor;

//        this.listWithHkIDTemp = listWithHkIDTemp;
//        txtView = (TextView) ((Activity)context).findViewById(R.id.toolbar_save_TV);

    }
    public SimpleCursorRecyclerAdapter(Context context, Cursor cursor,Cursor cursor1, @NonNull OnItemCheckListener onItemCheckListener/*, ArrayList<SelectedContactObject> listWithHkIDTemp*/) {
        super(context, cursor, 0);
        mFilteredList = new ArrayList<>();
        this.onItemCheckListener = onItemCheckListener;
        this.context = context;
        this.mCursor = cursor;
        this.mCursor1 = cursor1;
        this.groupSize=cursor1.getCount();
        txtViewSelectedUsers = (TextView) ((Activity)context).findViewById(R.id.tv_all_contacts);

//        this.listWithHkIDTemp = listWithHkIDTemp;
//        txtView = (TextView) ((Activity)context).findViewById(R.id.toolbar_save_TV);

    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup view) {
        final SimpleViewHolder holder;
        View itemView = null;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.list_row, null);
            holder = new SimpleViewHolder();
            holder.name =  itemView.findViewById(R.id.contactname);
            holder.number =  itemView.findViewById(R.id.contactno);
            holder.check =  itemView.findViewById(R.id.contactcheck);
            holder.image =  itemView.findViewById(R.id.polygonImage);
            holder.hkID =  itemView.findViewById(R.id.tv_hkid);
            holder.ll =  itemView.findViewById(R.id.ll);
            holder.imageselect=itemView.findViewById(R.id.checkImg);
            holder.name.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
            holder.number .setTypeface(Util.setTextViewTypeFace(context, "FiraSans-Regular.otf"));
            itemView.setTag(holder);
            itemView.setTag(R.id.contactcheck, holder.check);
        }
        return itemView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final SimpleViewHolder holder = (SimpleViewHolder) view.getTag();
        final SelectedContactObject selectedContactObject = new SelectedContactObject(Parcel.obtain());
      //  deviceContact.contactName=getIntent().getExtras().get("groupname").toString()!=null?getIntent().getExtras().get("groupname").toString():"test";

        selectedContactObject.setName(cursor.getString(cursor.getColumnIndex("contactName"))!=null? cursor.getString(cursor.getColumnIndex("contactName")):"test group");
        selectedContactObject.setNumber(cursor.getString(cursor.getColumnIndex("contactNo")));
        selectedContactObject.setImage(cursor.getString(cursor.getColumnIndex("image")));
        selectedContactObject.setHkID(cursor.getString(cursor.getColumnIndex("hkID")));
        selectedContactObject.setHkUUID(cursor.getString(cursor.getColumnIndex("hkUUID")));

        if (selectedContactObject.getHkID().trim().length() > 2) {
            holder.image.setImageResource(R.mipmap.hk_user);
        } else {
            holder.image.setImageResource(R.mipmap.user_old);
        }
        holder.name.setText(selectedContactObject.getName());
        holder.number.setText(selectedContactObject.getNumber());
        holder.hkID.setText(selectedContactObject.getHkID());

        long theId = cursor.getLong(cursor.getColumnIndex("_id"));

        holder.check.setTag(new Long(theId));
        holder.check.setClickable(false);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long theRealId = (Long) holder.check.getTag();
                if (check_status.containsKey(theRealId)) {
                    if (check_status.get(theRealId)) {
                        holder.check.setChecked(false);
                        selectedContactObject.setSelected(false);
                        check_status.put(theRealId, false);
                        onItemCheckListener.onItemUncheck(selectedContactObject);
                        holder.imageselect.setVisibility(View.INVISIBLE);

                    } else {
                        holder.check.setChecked(true);
                        selectedContactObject.setSelected(true);
                        check_status.put(theRealId, true);
                        onItemCheckListener.onItemCheck(selectedContactObject);
                        holder.imageselect.setVisibility(View.VISIBLE);

                    }
                } else {
                    holder.check.setChecked(true);
                    selectedContactObject.setSelected(true);
                    check_status.put(theRealId, true);
                    onItemCheckListener.onItemCheck(selectedContactObject);
                    holder.imageselect.setVisibility(View.VISIBLE);

                }
            }
        });
        if (check_status.get(theId) != null) {
            boolean sv = check_status.get(theId);
            if (!sv) {
                holder.check.setChecked(false);
                holder.imageselect.setVisibility(View.INVISIBLE);

            } else {
                holder.imageselect.setVisibility(View.VISIBLE);

                holder.check.setChecked(sv);
            }
        } else {
            holder.imageselect.setVisibility(View.INVISIBLE);

            holder.check.setChecked(false);
        }

        mFilteredList.add(selectedContactObject);
    }


    public void bindViewnew(View view, Context context, final Cursor cursor) {

        final SimpleViewHolder holder = (SimpleViewHolder) view.getTag();
        final SelectedContactObject selectedContactObject = new SelectedContactObject(Parcel.obtain());
        //  deviceContact.contactName=getIntent().getExtras().get("groupname").toString()!=null?getIntent().getExtras().get("groupname").toString():"test";

        selectedContactObject.setName(cursor.getString(1));
        selectedContactObject.setNumber(cursor.getString(2)+" Members");
        selectedContactObject.setImage(cursor.getString(2));
        selectedContactObject.setHkID("");
        selectedContactObject.setHkUUID(cursor.getString(0));
        selectedContactObject.setQuickBloxID(cursor.getString(0));


        if (selectedContactObject.getHkID().trim().length() > 2) {
            holder.image.setImageResource(R.mipmap.default_group);
         } else {
            holder.image.setImageResource(R.mipmap.default_group);
        }
        holder.name.setText(selectedContactObject.getName());
        holder.number.setText(selectedContactObject.getNumber());
        holder.hkID.setText(selectedContactObject.getHkID());

        long theId = cursor.getLong(cursor.getColumnIndex("_id"));

        holder.check.setTag(new Long(theId));
        holder.check.setClickable(false);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long theRealId = (Long) holder.check.getTag();
                if (check_status.containsKey(theRealId)) {
                    if (check_status.get(theRealId)) {
                        holder.check.setChecked(false);
                        selectedContactObject.setSelected(false);
                        check_status.put(theRealId, false);
                        onItemCheckListener.onItemUncheck(selectedContactObject);
                        holder.imageselect.setVisibility(View.INVISIBLE);

                    } else {
                        holder.check.setChecked(true);
                        selectedContactObject.setSelected(true);
                        check_status.put(theRealId, true);
                        onItemCheckListener.onItemCheck(selectedContactObject);
                        holder.imageselect.setVisibility(View.VISIBLE);

                    }
                } else {
                    holder.check.setChecked(true);
                    selectedContactObject.setSelected(true);
                    check_status.put(theRealId, true);
                    onItemCheckListener.onItemCheck(selectedContactObject);
                    holder.imageselect.setVisibility(View.VISIBLE);

                }
            }
        });
        if (check_status.get(theId) != null) {
            boolean sv = check_status.get(theId);
            if (!sv) {
                holder.check.setChecked(false);
                holder.imageselect.setVisibility(View.INVISIBLE);

            } else {
                holder.imageselect.setVisibility(View.VISIBLE);

                holder.check.setChecked(sv);
            }
        } else {
            holder.imageselect.setVisibility(View.INVISIBLE);

            holder.check.setChecked(false);
        }

        mFilteredList.add(selectedContactObject);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(context, mCursor, parent);
        } else {
            v = convertView;
        }
        bindView(v, context, mCursor);
        if(groupSize>position) {
            if (!mCursor1.moveToPosition(position)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            bindViewnew(v, context, mCursor1);
        }
        return v;
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        return super.swapCursor(c);
    }

    public interface OnItemCheckListener {

        void onItemCheck(SelectedContactObject item);

        void onItemUncheck(SelectedContactObject item);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                UtilityHelper.getCursor(context, charSequence.toString());
                notifyDataSetChanged();
            }
        };
    }
}

class SimpleViewHolder {
    public TextView name, number, hkID;
    public CheckBox check;
    public ImageView image,imageselect;
    public LinearLayout ll;

}