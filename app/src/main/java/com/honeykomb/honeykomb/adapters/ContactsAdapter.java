package com.honeykomb.honeykomb.adapters;

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
import com.honeykomb.honeykomb.utils.UtilityHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsAdapter extends CursorAdapter {

    //    private ArrayList<SelectedContactObject> listWithHkIDTemp;
    @NonNull
    private ContactsAdapter.OnItemCheckListener onItemCheckListener;
    private ArrayList<SelectedContactObject> mFilteredList;
    private Context context;
    private Cursor mCursor;
    private HashMap<Long, Boolean> check_status = new HashMap<Long, Boolean>();

    public ContactsAdapter(Context context, Cursor cursor, @NonNull ContactsAdapter.OnItemCheckListener onItemCheckListener/*, ArrayList<SelectedContactObject> listWithHkIDTemp*/) {
        super(context, cursor, 0);
        mFilteredList = new ArrayList<>();
        this.onItemCheckListener = onItemCheckListener;
        this.context = context;
        this.mCursor = cursor;
//        this.listWithHkIDTemp = listWithHkIDTemp;
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup view) {
        final SimpleViewHoldernew holder;
        View itemView = null;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.user_row_item, null);
            holder = new SimpleViewHoldernew();
            holder.name =  itemView.findViewById(R.id.contactname);
            holder.number =  itemView.findViewById(R.id.contactno);
            holder.check =  itemView.findViewById(R.id.contactcheck);
            holder.image =  itemView.findViewById(R.id.polygonImage);
            holder.imageselect=itemView.findViewById(R.id.checkImg);
            holder.hkID =  itemView.findViewById(R.id.tv_hkid);
            holder.ll =  itemView.findViewById(R.id.ll);

            itemView.setTag(holder);
            itemView.setTag(R.id.contactcheck, holder.check);
        }
        return itemView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final SimpleViewHoldernew holder = (SimpleViewHoldernew) view.getTag();
        final SelectedContactObject selectedContactObject = new SelectedContactObject(Parcel.obtain());

        selectedContactObject.setName(cursor.getString(cursor.getColumnIndex("contactName")));
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
            holder.check.setChecked(false);
            holder.imageselect.setVisibility(View.INVISIBLE);
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

class SimpleViewHoldernew {
    public TextView name, number, hkID;
    public CheckBox check;
    public ImageView image,imageselect;
    public LinearLayout ll;

}