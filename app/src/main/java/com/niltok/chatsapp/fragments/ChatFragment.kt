package com.niltok.chatsapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

import com.niltok.chatsapp.R
import com.niltok.chatsapp.adapters.ChatAdapter
import com.niltok.chatsapp.models.Message
import com.niltok.chatsapp.toast
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<com.niltok.chatsapp.models.Message> = ArrayList()


    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()

        return _view
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList, currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }

    private fun setUpChatBtn()
    {
        _view.buttonSend.setOnClickListener {
            val messageText = _view.editTextMessage.text.toString()
            if(messageText.isNotEmpty())
            {
                val message = Message(currentUser.uid, messageText, currentUser.photoUrl.toString(), Date())

                _view.editTextMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message)
    {
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageUrl"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage)
            .addOnCompleteListener{
                activity!!.toast("Message added!")
            }
            .addOnFailureListener {
                activity!!.toast("Message error, try again!")
            }
    }

}
