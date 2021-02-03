package com.andre.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.lateste_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerView_latestMessages.adapter = adapter

        listenForLatestMessages()
        fetchCurrentUser()
        verifyUserIsLoggedIn()
    }

    val latestMessagesMap = HashMap<String, ChatLogActivity.ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{ it ->
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }


            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    class LatestMessageRow(val chatMessage: ChatLogActivity.ChatMessage): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.lateste_message_row
        }
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_latesMessage_message.text = chatMessage.text

            val chatPartnerID: String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerID = chatMessage.toId
            } else {
                chatPartnerID = chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    viewHolder.itemView.textView_latesMessage_username.text = user?.username

                    val targetImageView = viewHolder.itemView.imageView_latestMessage
                    Picasso.get().load(user?.profileImageUrl).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("Latest Messages","Currente user: ${currentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    //verificar se o utilizador está logado senão abrir RegisterActivity
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    //criação do menu da navigation bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu( menu)
    }
    //ação dos items do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_new_message -> {
                val intent =Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

}