package com.niltok.chatsapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

import com.niltok.chatsapp.R
import com.niltok.chatsapp.adapters.RatesAdapter
import com.niltok.chatsapp.dialogues.RateDialog
import com.niltok.chatsapp.models.NewRateEvent
import com.niltok.chatsapp.models.Rate
import com.niltok.chatsapp.toast
import com.niltok.chatsapp.utils.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rates.view.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RatesFragment : Fragment() {

    private lateinit var _view: View

    private lateinit var adapter: RatesAdapter
    private val ratesList: ArrayList<Rate> = ArrayList()
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var ratesDBRef: CollectionReference

    private var ratesSubscription: ListenerRegistration? = null
    private lateinit var ratesBusListener: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_rates, container, false)

        setUpRatesDB()
        setUpCurrentUser()

        setUpRecyclerView()
        setUpFab()

        subscribeToRatings()
        subscribeToNewRatings()

        return _view
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = RatesAdapter(ratesList)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter

        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(dy != 0 && _view.fabRating.isShown)
                    _view.fabRating.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    _view.fabRating.show()
            }
        }

        _view.recyclerView.addOnScrollListener(scrollListener)

    }

    private fun setUpFab() {
        _view.fabRating.setOnClickListener { RateDialog().show(fragmentManager!!, "") }
    }

    private fun hashUserRated(rates: ArrayList<Rate>): Boolean{
        var result = false

        rates.forEach {
            if(it.userId == currentUser.uid)
                result = true
        }

        return result
    }

    private fun removeFABIfRated(rated: Boolean)
    {
        if(rated)
            _view.fabRating.hide()
            _view.recyclerView.removeOnScrollListener(scrollListener)
    }

    private fun setUpRatesDB(){
        ratesDBRef = store.collection("rates")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun saveRate(rate: Rate){
        val newRating = HashMap<String, Any>()
        newRating["userId"] = rate.userId
        newRating["text"] = rate.text
        newRating["rate"] = rate.rate
        newRating["createdAt"] = rate.createdAt
        newRating["profileImgURL"] = rate.profileImgURL

        ratesDBRef.add(newRating)
            .addOnCompleteListener{
                activity!!.toast("Rating added!")
            }
            .addOnFailureListener{
                activity!!.toast("Rating error, try again!!")
            }
    }

    private fun subscribeToRatings(){
        ratesSubscription = ratesDBRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        activity!!.toast("Exception!")
                        return
                    }

                    snapshot?.let{
                        ratesList.clear()
                        val rates = it.toObjects(Rate::class.java)
                        ratesList.addAll(rates)
                        removeFABIfRated(hashUserRated(ratesList))
                        adapter.notifyDataSetChanged()
                        _view.recyclerView.smoothScrollToPosition(0)
                    }
                }

            })
    }

    private fun subscribeToNewRatings(){
        ratesBusListener = RxBus.listen(NewRateEvent::class.java).subscribe {
            saveRate(it.rate)
        }
    }

    override fun onDestroyView() {
        _view.recyclerView.removeOnScrollListener(scrollListener)
        ratesBusListener.dispose()
        ratesSubscription?.remove()
        super.onDestroyView()
    }


}
