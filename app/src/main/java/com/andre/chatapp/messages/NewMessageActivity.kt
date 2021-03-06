package com.andre.chatapp.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andre.chatapp.R
import com.andre.chatapp.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
        fetchUsers()
    }

    companion object{
        val USER_KEY = "USER_KEY"
        val TAG = "Testes"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    Log.d(TAG,it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                recyclerView_newMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

//conteudo a adicionar no group adapter
class UserItem(val user: User): Item<GroupieViewHolder>(){
    //renderização de cada linha de mensagem diferente
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textView_newMessage.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_newMessageRow)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}