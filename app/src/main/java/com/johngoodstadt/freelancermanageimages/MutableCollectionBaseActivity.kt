/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.johngoodstadt.freelancermanageimages

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_manage_images.*

/**
 * Shows how to use notifyDataSetChanged with [ViewPager2]
 */
abstract class MutableCollectionBaseActivity : FragmentActivity() {
    private lateinit var buttonAddAfter: Button
    private lateinit var buttonAddBefore: Button
    private lateinit var buttonGoTo: Button
    private lateinit var buttonRemove: Button
    private lateinit var itemSpinner: Spinner
    private lateinit var checkboxDiffUtil: CheckBox
    private lateinit var viewPager: ViewPager2
    val UID = "BACH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_images)

        buttonAddAfter = findViewById(R.id.buttonAddAfter)
        buttonAddBefore = findViewById(R.id.buttonAddBefore)
        buttonGoTo = findViewById(R.id.buttonGoTo)
        buttonRemove = findViewById(R.id.buttonRemove)
        itemSpinner = findViewById(R.id.itemSpinner)
        checkboxDiffUtil = findViewById(R.id.useDiffUtil)
        viewPager = findViewById(R.id.viewPager)
//////////////////from manger

//        val intent = intent
//
//
//        UID = intent.getStringExtra("UID")
        println("optiid"+UID)

        done.setOnClickListener {

//            setResult(Activity.RESULT_OK) //signal something changed
//            finish()

        }

        cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED) //nothing changed
            finish()
        }
        //add file at end
        addfilatend.setOnClickListener {
            //add file at end
            val number = LibraryFilesystem.getCountOfPhotoScorePages(UID)
            val filenumber = number + 1
            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())

            val myDrawable = resources.getDrawable(R.drawable.green_circle)
            val anImage = (myDrawable as BitmapDrawable).bitmap

            LibraryFilesystem.writeImageFileToFileSystemFiles(anImage, filename)

            writeDebug()
        }


        //add file at 2
        addfileat2.setOnClickListener {

            val filenumber = 2
            val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)

            if(count == 0){
                return@setOnClickListener
            }

            renameFilesUp(UID,filenumber, count)

            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())

            val myDrawable = resources.getDrawable(R.drawable.green_circle)
            val anImage = (myDrawable as BitmapDrawable).bitmap

            LibraryFilesystem.writeImageFileToFileSystemFiles(anImage, filename)

            writeDebug()
        }

        //remove at 1
        removefileat1.setOnClickListener {
            val filenumber = 1
            val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)

            if(count == 0){
                return@setOnClickListener
            }

            removeFileAndRenameDown(UID, filenumber, count)

            writeDebug()
        }
        //remove file from end
        removefileatend.setOnClickListener {

            val filenumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)
            if(filenumber == 0){
                return@setOnClickListener
            }

            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())
            LibraryFilesystem.removeFile(filename)

            writeDebug()
        }

        //crop image 2
        cropimg2.setOnClickListener {

            if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                return@setOnClickListener
            }

            val filenumber = 2
            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())
            val uri = LibraryFilesystem.getUriFromFilename( filename)
            CropImage.activity(uri).start(this);

            writeDebug()
        }

        imginsertat2.setOnClickListener {

            if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                return@setOnClickListener
            }


            CropImage.activity().start(this);

            writeDebug()
        }
        writeDebug()

///////////////////////////end manager

        //create a folder for this app com.johngoodstadt.freelancermanageimages/files/test_image.png
        val mBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val filename = "test_image"
        LibraryFilesystem.writeImageFileToFileSystemFiles(mBitmap, filename)


        //examines files present
        val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)
        println("count of files:${count}")

        for (x in 1 .. count){
            println("file:${x} ${LibraryFilesystem.getFileNameByUID(UID,x.toString())}")
        }

        val file1 = LibraryFilesystem.getFileNameByUID(UID,"1")


        viewPager.adapter = createViewPagerAdapter()

        itemSpinner.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                ((convertView as TextView?) ?: TextView(parent.context)).apply {
                    if (Build.VERSION.SDK_INT >= 17) {
                        textDirection = View.TEXT_DIRECTION_LOCALE
                    }
                    text = getItem(position)
                }

            override fun getItem(position: Int): String = items.getItemById(getItemId(position))
            override fun getItemId(position: Int): Long = items.itemId(position)
            override fun getCount(): Int = items.size
        }

        buttonGoTo.setOnClickListener {
            viewPager.setCurrentItem(itemSpinner.selectedItemPosition, true)
        }

        fun changeDataSet(performChanges: () -> Unit) {

            /** without [DiffUtil] */
            val oldPosition = viewPager.currentItem
            val currentItemId = items.itemId(oldPosition)
            performChanges()
            viewPager.adapter!!.notifyDataSetChanged()
            if (items.contains(currentItemId)) {
                val newPosition =
                    (0 until items.size).indexOfFirst { items.itemId(it) == currentItemId }
                viewPager.setCurrentItem(newPosition, false)



//            if (checkboxDiffUtil.isChecked) {
//                /** using [DiffUtil] */
//                val idsOld = items.createIdSnapshot()
//                performChanges()
//                val idsNew = items.createIdSnapshot()
//                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
//                    override fun getOldListSize(): Int = idsOld.size
//                    override fun getNewListSize(): Int = idsNew.size
//
//                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
//                        idsOld[oldItemPosition] == idsNew[newItemPosition]
//
//                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
//                        areItemsTheSame(oldItemPosition, newItemPosition)
//                }, true).dispatchUpdatesTo(viewPager.adapter!!)
//            } else {
//                /** without [DiffUtil] */
//                val oldPosition = viewPager.currentItem
//                val currentItemId = items.itemId(oldPosition)
//                performChanges()
//                viewPager.adapter!!.notifyDataSetChanged()
//                if (items.contains(currentItemId)) {
//                    val newPosition =
//                        (0 until items.size).indexOfFirst { items.itemId(it) == currentItemId }
//                    viewPager.setCurrentItem(newPosition, false)
//                }

            }

            // item spinner update
            (itemSpinner.adapter as BaseAdapter).notifyDataSetChanged()
        }

        buttonRemove.setOnClickListener {
            changeDataSet { items.removeAt(itemSpinner.selectedItemPosition) }
        }

        buttonAddBefore.setOnClickListener {
            changeDataSet { items.addNewAt(itemSpinner.selectedItemPosition) }
        }

        buttonAddAfter.setOnClickListener {
            changeDataSet { items.addNewAt(itemSpinner.selectedItemPosition + 1) }
        }
    }

    abstract fun createViewPagerAdapter(): RecyclerView.Adapter<*>

    val items: ItemsViewModel by viewModels()

    /*//////////////////////from manager=====================*/

    private fun writeDebug(){

        val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)
        println("count of files:${count}")

        val textview: TextView = findViewById<TextView>(R.id.textview2)
        textview.text = "File count:${count}"

        for (x in 1 .. count){
            println("file:${x} ${LibraryFilesystem.getFileNameByUID(UID,x.toString())}")
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                println("Something changedmanage")
                data?.let {
                    val result = CropImage.getActivityResult(it)
                    val bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), result.uri)


                    val startnumber = 2
                    val lastNumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)

                    renameFilesUp(UID,startnumber, lastNumber)

                    val filename = LibraryFilesystem.getFileNameByUID(UID,startnumber.toString())

                    LibraryFilesystem.writeImageFileToFileSystemFiles(bitmap, filename)



                }
            }
        }

    }

    /*=====================end manager*/

}

/** A very simple collection of items. Optimized for simplicity (i.e. not performance). */
class ItemsViewModel : ViewModel() {
    private var nextValue = 1L
   /* val categories = listOf(
        Category(1, "Your Recording"),
        Category(2, "Film"),
        Category(3, "Series"),
        Category(4, "Kids"),
        Category(5, "Sport")
    )
   */ private val items = (1..9).map { longToItem(nextValue++) }.toMutableList()

    fun getItemById(id: Long): String = items.first { itemToLong(it) == id }
    fun itemId(position: Int): Long = itemToLong(items[position])
    fun contains(itemId: Long): Boolean = items.any { itemToLong(it) == itemId }
    fun addNewAt(position: Int) = items.add(position, longToItem(nextValue++))
    fun removeAt(position: Int) = items.removeAt(position)
    fun createIdSnapshot(): List<Long> = (0 until size).map { position -> itemId(position) }
    val size: Int get() = items.size

    private fun longToItem(value: Long): String = "item#$value"
    private fun itemToLong(value: String): Long = value.split("#")[1].toLong()
}
