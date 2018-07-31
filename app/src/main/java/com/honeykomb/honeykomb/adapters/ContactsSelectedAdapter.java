package com.honeykomb.honeykomb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.SelectedContactObject;

import net.grobas.view.PolygonImageView;

import java.util.ArrayList;

public class ContactsSelectedAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = ContactsSelectedAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<SelectedContactObject> arrayList;
        public ContactsSelectedAdapter(Context context, ArrayList<SelectedContactObject> mainDataList) {
        this.mContext = context;
        this.arrayList = mainDataList;
        Log.i(TAG, "selectedContactObjects size  = " + arrayList.size());
    }

    /*private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }*/

    /*public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }*/

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public SelectedContactObject getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        SelectedContactObject contactObject = arrayList.get(position);
        final ViewHolder holder;

        if (view == null) {
            contactObject = getItem(position);
            holder = new ViewHolder();
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(R.layout.selected_contact_list, null);

            final SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResource(position));
            swipeLayout.setSwipeEnabled(true);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.ll_delete_contact));
            final View finalView = view;
            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userName = holder.name.getText().toString();
                    Toast.makeText(mContext, "Clicked on contact " + userName, Toast.LENGTH_SHORT).show();
                }
            });

            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    finalView.setOnClickListener(null);

                    if (layout.getDragEdge() == SwipeLayout.DragEdge.Right) {
                        if (holder.name.getText() != null) {
                            String userName = holder.name.getText().toString();
                            Toast.makeText(mContext, "Delete User ? " + userName, Toast.LENGTH_SHORT).show();
                            arrayList.remove(position);
                            Log.i(TAG, "Arraylist size = " + arrayList.size());
                            if (getCount() < 1) {
                                arrayList.clear();
                            }
                            notifyDataSetChanged();
                            notifyDataSetInvalidated();
                            swipeLayout.close();
                        }
                    }
                }

                @Override
                public void onClose(SwipeLayout layout) {
                    super.onClose(layout);
                }
            });

            notifyDataSetChanged();

            holder.name =  view.findViewById(R.id.contactname);
            holder.number =  view.findViewById(R.id.contactno);
            holder.hkID =  view.findViewById(R.id.tv_hkid);
            holder.image = view.findViewById(R.id.polygonImage);

            view.setTag(holder);
            view.setTag(R.id.contactname, holder.name);
            view.setTag(R.id.contactno, holder.number);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(contactObject.getName());
        holder.number.setText(contactObject.getNumber());
        holder.hkID.setText(contactObject.getHkID());
        if (contactObject.getHkID().trim().length() > 2) {
            holder.image.setImageResource(R.mipmap.hk_user);
            holder.image.setBorder(true);
            holder.image.addBorderResource(5, R.color.white);
            holder.image.setCornerRadius(5);
            holder.image.setVertices(6);
            holder.image.setRotationAngle(60);
        } else {
            holder.image.setImageResource(R.mipmap.user);
            holder.image.setBorder(true);
            holder.image.addBorderResource(5, R.color.white);
            holder.image.setCornerRadius(5);
            holder.image.setVertices(6);
            holder.image.setRotationAngle(60);
        }

        return view;
    }

    /*public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
       *//* if (charText.length() == 0) {
//            arrayList.addAll(arraylist);
        } else {
            for (SelectedContactObject wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayList.add(wp);
                }*//**//*else if(wp.getHkID().toLowerCase(Locale.getDefault())
                        .contains(charText)){
                    arrayList.add(wp);
                }else if(wp.getNumber().toLowerCase(Locale.getDefault())
                        .contains(charText)){
                    arrayList.add(wp);
                }*//**//*
            }
        }*//*
        notifyDataSetChanged();
    }*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    public int getSwipeLayoutResource(int position) {
        return R.id.swipeContacts;
    }

    @Override
    public void onClick(View view) {

    }

    /*public Bitmap getByteContactPhoto(String contactId) {

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
//        Cursor cursor = mContext.getContentResolver().query(photoUri,
//                new String[]{Contacts.Photo.DATA15}, null, null, null);
//        ArrayList<ArrayList<String>> contacts = db_Helper.getFinalContactList();
        ArrayList<ArrayList<String>> contacts = Util._db.getFinalContactList();

        //       if (contacts.size() == 0) {
        //           return null;
        //       }
        if (contacts.size() > 0) {
//            byte[] data = cursor.getBlob(0);
            byte[] data = contacts.get(0).get(5).getBytes();
            if (data != null) {
                return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
            }
        }
        return null;
    }*/

    static class ViewHolder {
        protected TextView name;
        protected TextView number;
        //        protected CheckBox check;
        protected PolygonImageView image;
        private TextView hkID;
    }

}
