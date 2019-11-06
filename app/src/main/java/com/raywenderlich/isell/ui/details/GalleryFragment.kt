package com.raywenderlich.isell.ui.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.raywenderlich.isell.R
import com.raywenderlich.isell.data.Item
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item: Item? = requireActivity().intent.getParcelableExtra(getString(R.string.bundle_extra_item))
        item?.let {
            populateDetails(item)
        }
    }

    private fun populateDetails(item: Item) {
     itemImageView.setImageResource(item.imageId)
        titleTextView.text = item.title
    }
}