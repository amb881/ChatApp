package com.andre.chatapp.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.messages.LatestMessagesActivity.Companion.currentUserUID
import com.andre.chatapp.models.ChatMessage
import com.andre.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
       const val TAG = "Testes"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        reciclerView_chatLog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username
        listenForMessages()

        sendButton_chatLog.setOnClickListener {
            sendMessage()
        }
    }

    private fun listenForMessages() {
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$currentUserUID/$toId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
                    reciclerView_chatLog.scrollToPosition(adapter.itemCount -1)
            }

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }


    //enviar mensagens para o database firebase
    private fun sendMessage() {
        val text = editTextText_chatLog.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid.toString()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$currentUserUID/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$currentUserUID").push()
        val chatMessage = ChatMessage(reference.key!!, text, currentUserUID, toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Mensagem do chat gravada na ref: ${reference.key}")
                    editTextText_chatLog.text.clear()
                    reciclerView_chatLog.scrollToPosition(adapter.itemCount -1)
                }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$currentUserUID/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$currentUserUID")
        latestMessageToRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
        //carregar imagem de perfil do utilizador logado no chat
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
        //carregar imagem de perfil do utilizador com quem está a falar no chat
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}