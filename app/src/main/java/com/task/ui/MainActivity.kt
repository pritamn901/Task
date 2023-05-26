package com.task.ui

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.task.*
import com.task.databinding.ActivityMainBinding
import com.task.databinding.CustomTabBinding

class MainActivity : AppCompatActivity() {
    lateinit var bind:ActivityMainBinding
    var locationList=ArrayList<LocationListData>()
    var mainViewModal: MainActivityViewModal?=null
    var headings= arrayOf("Map","List","Login")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind=ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        init()
        setObserver()
    }
    private fun init(){
        val commonViewModelFactoy = CommonViewModelFactory(Application(), Repository())
        mainViewModal= ViewModelProvider(this,commonViewModelFactoy)[MainActivityViewModal::class.java]
        mainViewModal?.getLocationList()
    }

    private fun setTabs(){
        bind.viewPager.adapter = ViewPagerAdapter(this)
        bind.viewPager.isUserInputEnabled = false

        TabLayoutMediator(bind.tabLayout, bind.viewPager) { tab, position ->
            tab.customView = (bind.viewPager.adapter as ViewPagerAdapter).addView(position)
        }.attach()

        bind.viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bind.tvHeading.text=headings[position]
            }
        })

        bind.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val view = tab!!.customView
                val textView=view!!.findViewById<TextView>(R.id.tvText) as TextView
                textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab!!.customView
                val textView=view!!.findViewById<TextView>(R.id.tvText) as TextView
                textView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.gray))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setObserver(){
        mainViewModal?.locationListResponse?.observe(this) {
            it.let {
                locationList.clear()
                locationList.addAll(it)
                setTabs()
            }
        }
    }

    inner class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

        private val imageResId = intArrayOf(R.drawable.ic_map,R.drawable.ic_list_icon,R.drawable.ic_login)

        fun addView(position: Int): View {
            val v = CustomTabBinding.inflate(layoutInflater)
            v.tabicon.setImageResource(imageResId[position])
            v.tvText.text=headings[position]
            return v.root
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MapsFragment()
                1 -> ListFragment()
                else -> LoginFragment()
            }
        }
    }
}