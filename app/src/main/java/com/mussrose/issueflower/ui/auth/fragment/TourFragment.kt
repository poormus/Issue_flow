package com.mussrose.issueflower.ui.auth.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mussrose.issueflower.R
import com.mussrose.issueflower.adapter.AdapterTour
import com.mussrose.issueflower.databinding.FragmentTourBinding
import com.mussrose.issueflower.entities.tour.Tour

class TourFragment: Fragment(R.layout.fragment_tour) {

    lateinit var binding:FragmentTourBinding
    private lateinit var adapterTour:AdapterTour
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentTourBinding.bind(view)
        setViewPager()
    }

    private fun setViewPager() {
        val tourItem=Tour(R.drawable.undraw_create_issue,"Create issues that you want to get help with.")
        val tourItem2=Tour(R.drawable.undraw_comment,"Comment on open issues.")
        val tourItem3=Tour(R.drawable.undraw_help,"Help fellow developers solve their problems.")
        val tourItem4=Tour(R.drawable.undraw_search,"Not the answer you are looking for?" +
                " Search by clicking on tags.")
        val tourItem5=Tour(R.drawable.undraw_bookmark,"Bookmark any issue to follow them later on")
        val tourItem6=Tour(R.drawable.undraw_markdown,"Use markdown support to give clear answers.")
        val tourItems= listOf(tourItem,tourItem2,tourItem3,tourItem4,tourItem5,tourItem6)
        adapterTour= AdapterTour(tourItems)
        binding.viewPagerTour.adapter=adapterTour
        binding.dotsIndicator.setViewPager2(binding.viewPagerTour)
    }
}