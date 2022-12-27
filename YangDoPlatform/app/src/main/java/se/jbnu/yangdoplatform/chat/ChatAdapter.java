package se.jbnu.yangdoplatform.chat;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import se.jbnu.yangdoplatform.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<Chat> chatList;
    private String name;

    public ChatAdapter(List<Chat> chatData, String name){
        chatList = chatData;
        this.name = name;
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView msgText;
        public View rootView;
        public LinearLayout msgLinear;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            msgLinear = itemView.findViewById(R.id.msgLinear);
            nameText = itemView.findViewById(R.id.nameText);
            msgText = itemView.findViewById(R.id.msgText);
            rootView = itemView;

            itemView.setEnabled(true);
            itemView.setClickable(true);
        }
    }
    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);

        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);

        return myViewHolder;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        holder.nameText.setText(chat.getName());
        holder.msgText.setText(chat.getMsg());
        if(chat.getName().equals(this.name)){
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            holder.msgLinear.setGravity(Gravity.RIGHT);
        } else {
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            holder.msgLinear.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return chatList == null ? 0: chatList.size();
    }



    public void addChat(Chat chat){
        chatList.add(chat);
        notifyItemInserted(chatList.size()-1);
    }
}
