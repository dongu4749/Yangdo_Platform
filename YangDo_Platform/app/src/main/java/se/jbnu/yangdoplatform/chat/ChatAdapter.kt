package se.jbnu.yangdoplatform.chat

import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import se.jbnu.yangdoplatform.R
import se.jbnu.yangdoplatform.chat.ChatAdapter.MyViewHolder

class ChatAdapter(private val chatList: MutableList<Chat>?, private val name: String) : RecyclerView.Adapter<MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameText: TextView
        var msgText: TextView
        var rootView: View
        var msgLinear: LinearLayout

        init {
            msgLinear = itemView.findViewById(R.id.msgLinear)
            nameText = itemView.findViewById(R.id.nameText)
            msgText = itemView.findViewById(R.id.msgText)
            rootView = itemView
            itemView.isEnabled = true
            itemView.isClickable = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_msg, parent, false) as LinearLayout
        return MyViewHolder(linearLayout)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = chatList!![position]
        holder.nameText.text = chat.name
        holder.msgText.text = chat.msg
        if (chat.name == name) {
            holder.nameText.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            holder.msgText.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            holder.msgLinear.gravity = Gravity.RIGHT
        } else {
            holder.nameText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            holder.msgText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            holder.msgLinear.gravity = Gravity.LEFT
        }
    }

    override fun getItemCount(): Int {
        return chatList?.size ?: 0
    }

    fun addChat(chat: Chat) {
        chatList!!.add(chat)
        notifyItemInserted(chatList.size - 1)
    }
}