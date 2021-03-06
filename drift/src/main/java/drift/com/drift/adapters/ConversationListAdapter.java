package drift.com.drift.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import drift.com.drift.R;
import drift.com.drift.helpers.DateHelper;
import drift.com.drift.helpers.UserPopulationHelper;
import drift.com.drift.managers.UserManager;
import drift.com.drift.model.Conversation;
import drift.com.drift.model.ConversationExtra;
import drift.com.drift.model.User;



public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ConversationListCell>{

    private List<ConversationExtra> conversationExtraList;
    private Context context;

    public ConversationListAdapter(Context context, List<ConversationExtra> conversationExtras) {
        conversationExtraList = conversationExtras;
        this.context = context;
    }

    public void updateDate(ArrayList<ConversationExtra> conversationExtras) {
        conversationExtraList = conversationExtras;
        notifyDataSetChanged();
    }

    @Override
    public ConversationListCell onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.drift_sdk_conversation_cell, parent, false);
        return new ConversationListCell(itemView);
    }

    @Override
    public void onBindViewHolder(ConversationListCell holder, int position) {
        holder.setupForConversation(conversationExtraList.get(position));
    }

    @Override
    public int getItemCount() {
        return conversationExtraList.size();
    }

    public Conversation getItemAt(int position) {
        return conversationExtraList.get(position).conversation;
    }

    class ConversationListCell extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        TextView conversationTimeTextView;
        TextView conversationPreviewTextView;
        ImageView userImageView;
        TextView unreadCountTextView;


        public ConversationListCell(View view) {
            super(view);
            userNameTextView = view.findViewById(R.id.drift_sdk_conversation_cell_user_name_text_view);
            conversationTimeTextView = view.findViewById(R.id.drift_sdk_conversation_cell_time_text_view);
            conversationPreviewTextView = view.findViewById(R.id.drift_sdk_conversation_cell_preview_text_view);
            userImageView = view.findViewById(R.id.drift_sdk_conversation_cell_image_view);
            unreadCountTextView = view.findViewById(R.id.drift_sdk_conversation_cell_unread_text_view);
        }


        public void setupForConversation(ConversationExtra conversationExtra){

            if (conversationExtra.conversation == null) {
                return;
            }

            Conversation conversation = conversationExtra.conversation;

            if (conversation.preview == null || conversation.preview.isEmpty() || conversation.preview.trim().isEmpty()){
                conversationPreviewTextView.setText(new String(Character.toChars(0x1F4CE))  + " [Attachment]");
            } else {
                String preview = conversation.preview;
                conversationPreviewTextView.setText(preview);
            }

            if (conversation.updatedAt != null){

                Date updatedAt = conversation.updatedAt;

                conversationTimeTextView.setText(DateHelper.formatDateForConversation(updatedAt));

            }else {
                conversationTimeTextView.setText("");
            }


            if (conversationExtra.lastMessage != null) {
                if (conversationExtra.lastMessage.isMessageFromEndUser()) {
                    userNameTextView.setText("You");
                } else {

                    User user = UserManager.getInstance().userMap.get(conversationExtra.lastMessage.authorId);
                    UserPopulationHelper.populateTextAndImageFromUser(context, user, userNameTextView, userImageView);

                }
            }


            if (conversationExtra.unreadMessages == 0){
                unreadCountTextView.setVisibility(View.GONE);
            }else{
                if (conversationExtra.unreadMessages > 100) {
                    unreadCountTextView.setText("99+");
                } else {
                    unreadCountTextView.setText(String.valueOf(conversationExtra.unreadMessages));
                }
                unreadCountTextView.setVisibility(View.VISIBLE);
            }

        }

    }

}
