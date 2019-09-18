package com.niltok.chatsapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

import com.niltok.chatsapp.R
import com.niltok.chatsapp.toast
import com.niltok.chatsapp.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info.view.*
import java.lang.Exception
import java.util.*
import java.util.EventListener

class InfoFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInfoUI()

        subscribeToTotalMessagesFirebaseStyle()

        return _view
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInfoUI()
    {
        _view.textViewInfoEmail.text = currentUser.email
//        _view.textViewInfoName.text = if(currentUser.displayName == null)currentUser.displayName  else getString(R.string.info_no_name)



//        _view.textViewInfoName.text = currentUser.displayName?.let { it } ?: run { getString(R.string.info_no_name) }


        currentUser.photoUrl?.let {
            Picasso.get().load(currentUser.photoUrl).resize(300, 300).centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        } ?: run {
            Picasso.get().load(R.drawable.ic_person).resize(300, 300).centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)
        }
    }


    private fun subscribeToTotalMessagesFirebaseStyle()
    {
        chatDBRef.addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>
        {
            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception")
                    return
                }

                querySnapshot?.let { _view.textViewInfoMessages.text = "${it.size()}" }
            }

        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }
}
