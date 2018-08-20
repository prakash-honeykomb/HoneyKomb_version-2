package com.honeykomb.honeykomb.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.honeykomb.honeykomb.R;
import com.honeykomb.honeykomb.dao.GroupObject;
import com.honeykomb.honeykomb.utils.Util;

import java.util.List;

public class GroupsRCVAdapter extends RecyclerView.Adapter<GroupsRCVAdapter.myViewHolder>{
    public GroupsRCVAdapter(Context context, List<GroupObject> groupObjectList) {
        this.context = context;
        this.groupObjectList = groupObjectList;
     //   txtView = (TextView) ((Activity)context).findViewById(R.id.memberListTV);
        linearLayoutNoGroup = ((Activity)context).findViewById(R.id.noGroupsLL);
    }

    Context context;
    public GroupsRCVAdapter(List<GroupObject> groupObjectList) {
        this.groupObjectList = groupObjectList;
    }


    public boolean dataChangedInviteeAdapter = false;
    List<GroupObject> groupObjectList;
    LinearLayout linearLayoutNoGroup;
    public class myViewHolder extends RecyclerView.ViewHolder
    {
        TextView title,members;
        protected SwipeLayout swipeLayout;
        public myViewHolder(View itemView) {
            super(itemView);

            title=(TextView)itemView.findViewById(R.id.group_title_TV);
            members= (TextView) itemView .findViewById(R.id.group_members_TV);
            title.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-SemiBold.otf"));
            members.setTypeface(Util.setTextViewTypeFace(context, "FiraSans-Regular.otf"));
            swipeLayout = itemView.findViewById(R.id.swipeInvitee);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.ll_delete_invitee));
           swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.ll_contact_reinvite));

        }
    }
    @NonNull
    @Override
    public GroupsRCVAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_row,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupsRCVAdapter.myViewHolder holder, int position) {
        GroupObject groupObject= groupObjectList.get(position);
        holder.title.setText(groupObject.getGroupName());
        holder.members.setText(groupObject.getGroupMembers());
       // final GroupObject contactObject = groupUsersList.get(position);

         holder.swipeLayout.setTag(groupObject);

        holder.swipeLayout.setId(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                GroupObject obj = (GroupObject) layout.getTag();
                if (layout.getDragEdge() == SwipeLayout.DragEdge.Left) {
                    if(!dataChangedInviteeAdapter)
                        holder.swipeLayout.setSwipeEnabled(false);


                    //  String hK_UUID = obj.getHkID();


                    //arrayList = Util._db.getActivityUsers(activityID);
//
                    // TODO adding user to existing group.
                 /*   if (qbGroupID != null && !qbGroupID.equalsIgnoreCase("0") && qbGroupID.length() > 2) {
                        String UniqueID = UUID.randomUUID().toString();
                        Util._db.saveUser(UniqueID, Integer.parseInt(qbGroupID), obj.getHkID(), "ADD", "NO");
//                        UtilityHelper.addUserToGroup(UniqueID, mContext, Integer.parseInt(qbGroupID), obj.getHkID());
                        if (Util._db.getActionTypeBasedOnActivityID(activityID)) {
                            Intent i = new Intent(Intent.ACTION_SYNC, null, mContext, SendAllActivityToServer.class);
                            mContext.startService(i);
                        }
                    }*/
                    Log.i("FAVGroup: ", "onActivityResult clled: if "  );
                    Toast.makeText(context,"Swipe left",Toast.LENGTH_LONG).show();

                    dataChangedInviteeAdapter = true;
                    holder.swipeLayout.close();


                } else if (layout.getDragEdge() == SwipeLayout.DragEdge.Right) {

                    Log.i("FAVGroup: ", "onActivityResult clled: else "  );
                     String number = obj.getGroupID();
                    //  if (dataChangedInvitee || groupUsersAdapter.dataChangedInviteeAdapter) {
              //TODO : Delete all user associated with particular group - getGroupID()
                     Util._db.deleteGroupSelectedListOfUsers(number);

                    //TODO : Delete particular group - getGroupID()

                    Util._db.deleteUserGroup(number);


                    // Intent intent = new Intent(mContext, FAVGroup.class);
                    // setResult(Activity.RESULT_OK, intent);
                    // }
                   // Toast.makeText(context,"Swipe right",Toast.LENGTH_LONG).show();
                    holder.swipeLayout.close();



                    groupObjectList.remove(obj);
                    if(groupObjectList.size()<1) {

                        linearLayoutNoGroup.setVisibility(View.VISIBLE);
                    }
                   // txtView.setText(groupUsersList.size()+" Users");
                    //FAVGroupDetails.grupSizeTV.setText(groupUsersList.size());
                    dataChangedInviteeAdapter = true;
                }
                notifyDataSetChanged();
//              notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupObjectList.size();
    }


}
