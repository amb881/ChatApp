package com.andre.chatapp.messages

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.andre.chatapp.R
import com.andre.chatapp.models.ChatMessage
import com.andre.chatapp.models.User
import com.andre.chatapp.registerlogin.ProfileSettingsActivity
import com.andre.chatapp.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.nav_header.*


class LatestMessagesActivity : AppCompatActivity() {

    lateinit var  toggle: ActionBarDrawerToggle

    companion object{
        var currentUser: User? = null
        const val TAG = "Testes"
        lateinit var currentUserEmail: String
        lateinit var currentUserUsername: String
        lateinit var currentUserProfileImageUrl: String
        lateinit var currentUserUID: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.menu_new_message ->{
                    val intent = Intent(this, NewMessageActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_profile_settings ->{
                    val intent = Intent(this, ProfileSettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_sign_out ->{
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            true
        }

        recyclerView_latestMessages.adapter = adapter
        recyclerView_latestMessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        fab.setOnClickListener {
            val intent =Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        verifyUserIsLoggedIn()
        fetchCurrentUser()
        listenForLatestMessages()


    }

    //------------------------Navigation Menu--------------------------------------

    //ação dos items do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //-----------------------------------------------------------------------

    val latestMessagesMap = HashMap<String, ChatMessage>()

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
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
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

    private val adapter = GroupAdapter<GroupieViewHolder>()

    class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
        var chatPartnerUser: User? = null
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_latesMessage_message.text = chatMessage.text

            val chatPartnerID: String = if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatMessage.toId
            } else {
                chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)
                    viewHolder.itemView.textView_latesMessage_username.text = chatPartnerUser?.username

                    val targetImageView = viewHolder.itemView.imageView_latestMessage
                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun verifyUserIsLoggedIn() {
        currentUserUID = FirebaseAuth.getInstance().uid.toString()
        if(currentUserUID == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun fetchCurrentUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$currentUserUID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                currentUserUsername = currentUser?.username.toString()
                header_menu_textView.text = currentUserUsername

                currentUserEmail = currentUser?.email.toString()

                Picasso.get().load(currentUser?.profileImageUrl).into(header_menu_imageView)
                currentUserProfileImageUrl = currentUser?.profileImageUrl.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }




}