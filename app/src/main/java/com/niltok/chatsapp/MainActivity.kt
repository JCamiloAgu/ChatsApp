package com.niltok.chatsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.alejandrolora.mylibrary.ToolbarActivity
import com.google.firebase.auth.FirebaseAuth
import com.niltok.chatsapp.activities.LoginActivity
import com.niltok.chatsapp.adapters.PagerAdapter
import com.niltok.chatsapp.fragments.ChatFragment
import com.niltok.chatsapp.fragments.InfoFragment
import com.niltok.chatsapp.fragments.RatesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolbarActivity() {

    private var prevBottomSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarToLoad(toolbarView as Toolbar)

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()
    }

    private fun getPagerAdapter(): PagerAdapter{
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(RatesFragment())
        adapter.addFragment(ChatFragment())
        return adapter
    }

    private fun setUpViewPager(adapter: PagerAdapter){
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                if (prevBottomSelected == null)
                    bottomNavigation.menu.getItem(0).isChecked = false
                else
                    prevBottomSelected!!.isChecked = false

                bottomNavigation.menu.getItem(position).isChecked = true
                prevBottomSelected = bottomNavigation.menu.getItem(position)
            }

        })
    }

    private fun setUpBottomNavigationBar(){
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId)
            {
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0; true
                }

                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1; true
                }

                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2; true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
