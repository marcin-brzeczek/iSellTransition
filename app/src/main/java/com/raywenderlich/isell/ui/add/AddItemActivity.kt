/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.isell.ui.add

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.Scene
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.raywenderlich.isell.R
import com.raywenderlich.isell.data.Category
import com.raywenderlich.isell.data.Item
import com.raywenderlich.isell.ui.list.ListActivity
import com.raywenderlich.isell.util.DataProvider
import com.raywenderlich.isell.util.ProgressBarTransition
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.scene_item_image.*
import kotlinx.android.synthetic.main.scene_upload.*

class AddItemActivity : AppCompatActivity(),
    TextWatcher, AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        categorySpinner.adapter = ArrayAdapter<Category>(
            this,
            android.R.layout.simple_spinner_item,
            Category.values()
        )
        categorySpinner.onItemSelectedListener = this

        titleEditText.addTextChangedListener(this)
        priceEditText.addTextChangedListener(this)
        detailsEditText.addTextChangedListener(this)
    }

    /**
     * Handles Back button press, relaunch ListActivity for reload data
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateBackToItemList()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        titleTextInput.error = null
        priceTextInput.error = null
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        imageButton.setImageResource(imageFromCategory(categorySpinner.selectedItem as Category))
    }

    fun onClickAddItem(view: View) {
        if (hasValidInput()) {
            val uploadScene: Scene = Scene.getSceneForLayout(sceneContainer, R.layout.scene_upload, this)
            TransitionManager.go(uploadScene, Fade())
            createProgressCounter(3000, 600).start()
            addItemData()
            showAddItemConfirmation()
        }
    }

    private fun createProgressCounter(uploadTime: Long, interval: Long) = object : CountDownTimer(uploadTime, interval) {
        val maxProgress = 100
        var uploadProgress = 0

        override fun onTick(millisUntilFinished: Long) {
            if (uploadProgress < maxProgress) {
                uploadProgress += 20
                TransitionManager.beginDelayedTransition(sceneContainer, ProgressBarTransition())
                progressBar.progress = uploadProgress
            }
        }

        override fun onFinish() {
            uploadStatus.text = getString(R.string.text_uploaded)
            addItemData()
            showAddItemConfirmation()
        }
    }

    fun addItemData() {
        val selectedCategory = categorySpinner.selectedItem as Category
        DataProvider.addItem(Item(
            imageId = imageFromCategory(selectedCategory),
            title = titleEditText.text.toString(),
            details = detailsEditText.text.toString(),
            price = priceEditText.text.toString().toDouble(),
            category = selectedCategory,
            postedOn = System.currentTimeMillis())
        )
    }

    /**
     * Returns image of an item Category
     */
    private fun imageFromCategory(category: Category): Int {
        return when (category) {
            Category.LAPTOP -> R.drawable.ic_laptop
            Category.MONITOR -> R.drawable.ic_monitor
            else -> R.drawable.ic_headphone
        }
    }

    /**
     * Navigates to ListActivity and reloads data
     */
    private fun navigateBackToItemList() {
        val mainIntent = Intent(this, ListActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainIntent)
        finish()
    }

    private fun hasValidInput(): Boolean {
        val title = titleEditText.text.toString()
        if (title.isNullOrBlank()) {
            titleTextInput.error = getString(R.string.error_message_title)
            return false
        }

        val price = priceEditText.text.toString().toDoubleOrNull()
        if (price == null || price <= 0.0) {
            priceTextInput.error = getString(R.string.error_message_price)
            return false
        }

        return true
    }

    private fun showAddItemConfirmation() {
        Snackbar.make(addItemRootView, getString(R.string.add_item_successful), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.ok)) {
                navigateBackToItemList()
            }
            .show()
    }

}
