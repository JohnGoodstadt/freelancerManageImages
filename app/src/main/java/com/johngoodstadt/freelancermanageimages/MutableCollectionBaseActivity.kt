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
import android.util.Log
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
import android.R.id.edit
import android.content.SharedPreferences.Editor
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.activity_manage_images.cancel
import kotlinx.android.synthetic.main.activity_manage_images.removefileatend
import kotlinx.android.synthetic.main.noimg.*


/**
 * Shows how to use notifyDataSetChanged with [ViewPager2]
 */
abstract class MutableCollectionBaseActivity : FragmentActivity() {
    private lateinit var buttonAddAfter: Button
    private lateinit var buttonAddnext: Button
    private lateinit var buttonGoTo: Button
    private lateinit var buttonRemove: Button
    private lateinit var buttonCrop: Button
    private lateinit var itemSpinner: Spinner
    private lateinit var checkboxDiffUtil: CheckBox
    private lateinit var viewPager: ViewPager2
    var from: String = "click"
    var changed: String = "notOK"
    var firsttime: String = "no"
    var onactiposi: Int = 1
    var howmanypage: Int = 1
    //////////////////this var is used for set page after clicked image
    var Setviewposi: Int = 0

    companion object {
        public var UID = "Example"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

////////////////////from manger

        try {
            val intent = intent
            UID = intent.getStringExtra("UID")
            firsttime = intent.getStringExtra("firsttime")
            changed = intent.getStringExtra("Changed")
            Setviewposi = intent.getIntExtra("viewposi",0)
            MutableCollectionBaseActivity.UID = UID
            println("optiid" + UID+" "+firsttime)

            getResources().getConfiguration().orientation;
           } catch (e: Exception) {
           }

/////////////////////for intial imag for viewpager
        howmanypage = LibraryFilesystem.getCountOfPhotoScorePages(UID)
        Log.e("sonamany", howmanypage.toString() + "onc")
        if (howmanypage == 0 && firsttime.equals("no")) {
            setContentView(R.layout.noimg);
            buttonAddnextNo.setOnClickListener {
                val intent = Intent(
                    this@MutableCollectionBaseActivity,
                    MutableCollectionViewActivity::class.java
                )
                intent.putExtra("UID", UID)
                intent.putExtra("firsttime", "yes")
                intent.putExtra("Changed", "notOK" )
                startActivity(intent)
                finish()

            }

            gotomanager.setOnClickListener {
                //                setResult(Activity.RESULT_OK) //signal something changed
//                finish()
//                val returnIntent = Intent()
//                returnIntent.putExtra("result", "chk")
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()

                try {
                    val intent =
                        Intent(this@MutableCollectionBaseActivity, MainActivity::class.java)
                    intent.putExtra("Changed", changed)
                    startActivity(intent)
                    finish()
                }catch (e: Exception){}
            }

        } else {
            howmanypage = LibraryFilesystem.getCountOfPhotoScorePages(UID)
            setContentView(R.layout.activity_manage_images)

            buttonAddAfter = findViewById(R.id.buttonAddAfter)
            buttonAddnext = findViewById(R.id.buttonAddnext)
            buttonGoTo = findViewById(R.id.buttonGoTo)
            buttonCrop = findViewById(R.id.buttonCrop)
            buttonRemove = findViewById(R.id.buttonRemove)
            itemSpinner = findViewById(R.id.itemSpinner)
            checkboxDiffUtil = findViewById(R.id.useDiffUtil)
            viewPager = findViewById(R.id.viewPager)
//        }

            var orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
                textview2.visibility = View.GONE
                llbtmbtn.visibility = View.GONE
            } else {
                // In portrait
                textview2.visibility = View.VISIBLE
                llbtmbtn.visibility = View.VISIBLE

            }


            if(firsttime.equals("yes")){

                items.addNewAt(viewPager.currentItem)

                addanother(1)

            }


            cancel.setOnClickListener {
                setResult(Activity.RESULT_CANCELED) //nothing changed
                finish()
            }


            //remove at 1
            fun removeFileAtPosition(posi: Int) {
                val filenumber = posi
                val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)

                /*if(count == 0){
    //                return@setOnClickListener
                }
    */
                removeFileAndRenameDown(UID, filenumber, count)

                writeDebug()
                val intent = Intent(
                    this@MutableCollectionBaseActivity,
                    MutableCollectionViewActivity::class.java
                )
                intent.putExtra("UID", UID)
                intent.putExtra("firsttime", "no")
                intent.putExtra("Changed", "notOK" )
                startActivity(intent)

//            recreate()
                finish()
            }
            //remove file from end
            removefileatend.setOnClickListener {

                val filenumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)
                if (filenumber == 0) {
                    return@setOnClickListener
                }

                val filename = LibraryFilesystem.getFileNameByUID(UID, filenumber.toString())
                LibraryFilesystem.removeFile(filename)

                writeDebug()
            }

            //crop image 2
            fun cropimg2(posi: Int) {

                /*if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                    return@setOnClickListener
                }*/

                val filenumber = posi
//            val filenumber = 2
                onactiposi = posi
                from = "cropimg"

                val filename = LibraryFilesystem.getFileNameByUID(UID, filenumber.toString())
                val uri = LibraryFilesystem.getUriFromFilename(filename)
                CropImage.activity(uri).start(this);

                writeDebug()


            }


            fun imginsertatNext(posi: Int) {
                val number = LibraryFilesystem.getCountOfPhotoScorePages(UID)
                Log.e("sonainsrt", number.toString() + " " + posi);

                /*if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                    return@setOnClickListener
                }*/

                onactiposi = posi
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

            for (x in 1..count) {
                println("file:${x} ${LibraryFilesystem.getFileNameByUID(UID, x.toString())}")
            }

            val file1 = LibraryFilesystem.getFileNameByUID(UID, "1")


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

            viewPager.setCurrentItem((Setviewposi), false)

            buttonGoTo.setOnClickListener {
//                setResult(Activity.RESULT_OK) //signal something changed
//                finish()
//                val returnIntent = Intent()
//                returnIntent.putExtra("result", "chk")
//                setResult(Activity.RESULT_OK, returnIntent)
//                finish()

                try {
                    val intent =
                        Intent(this@MutableCollectionBaseActivity, MainActivity::class.java)
                    intent.putExtra("Changed", changed)
                    startActivity(intent)
                    finish()
                }catch (e: Exception){}
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
//                viewPager.setCurrentItem(newPosition, false)



                }

                // item spinner update
                (itemSpinner.adapter as BaseAdapter).notifyDataSetChanged()
            }

            buttonRemove.setOnClickListener {
                Log.e("sonacurre", viewPager.currentItem.toString());
                try {

                    changeDataSet { items.removeAt(viewPager.currentItem + 1) }
//            changeDataSet { items.removeAt(itemSpinner.selectedItemPosition) }
                    removeFileAtPosition(viewPager.currentItem + 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                    changeDataSet {
                        items.removeAt(viewPager.currentItem)
                        val filenumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)
                        if (filenumber == 0) {
//                        return@setOnClickListener
                        }

                        val filename = LibraryFilesystem.getFileNameByUID(UID, filenumber.toString())
                        LibraryFilesystem.removeFile(filename)

                        writeDebug()
                    }
                }

            }

            buttonCrop.setOnClickListener {
                Log.e("sonacurre", viewPager.currentItem.toString());
//            changeDataSet { items.addNewAt(viewPager.currentItem) }
                cropimg2(viewPager.currentItem + 1)
//            changeDataSet { items.addNewAt(itemSpinner.selectedItemPosition) }

            }

            buttonAddnext.setOnClickListener {
                //                                viewPager.setCurrentItem(4, false)
                from = "click"
                Log.e("sonacurrenext", viewPager.currentItem.toString() + " ");
                changeDataSet { items.addNewAt(viewPager.currentItem + 1) }
                imginsertatNext(viewPager.currentItem + 2)
              }

        }


    }

    fun addanother(filecnt : Int) {
        val number = LibraryFilesystem.getCountOfPhotoScorePages(UID)
        Log.e("sonafirst", number.toString()+" "+viewPager.currentItem);

        /*if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
            return@setOnClickListener
        }*/

        val filenumber = number + 1
//            val filenumber = posi + 1

        onactiposi = filenumber
        CropImage.activity().start(this);

        writeDebug()

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
                var viewposi = onactiposi

                println("Something changedmanage"+viewposi)
                data?.let {
                    val result = CropImage.getActivityResult(it)
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        MyApplication.getAppContext().getContentResolver(),
                        result.uri
                    )
                    val startnumber = onactiposi
                    val lastNumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)
                    Log.e("sonaActifr", startnumber.toString()+" "+lastNumber.toString());

                    renameFilesUp(UID, startnumber, lastNumber)

                    val filename = LibraryFilesystem.getFileNameByUID(UID, startnumber.toString())

                    LibraryFilesystem.writeImageFileToFileSystemFiles(bitmap, filename)
                    if (from.equals("cropimg")){
                        val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)
                        try {
                            removeFileAndRenameDown(UID, onactiposi + 1, count)
                            viewposi = onactiposi - 1

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }else{
                        viewposi = onactiposi - 1

                    }

                    println("gotposi"+viewposi)
                    val intent = Intent(this@MutableCollectionBaseActivity, MutableCollectionViewActivity::class.java)
                    intent.putExtra("UID", UID)
                    intent.putExtra("firsttime", "no" )
                    intent.putExtra("viewposi", viewposi )
                    intent.putExtra("Changed", "OK" )
//                            startActivityForResult(intent, MainActivity.RequestCodes.REQUEST_MANAGE_IMAGES)
                    //                    setResult(MainActivity.RequestCodes.REQUEST_MANAGE_IMAGES,intent);

                    startActivity(intent)
                    finish()
//                recreate()


                }
            }else{
                val intent = Intent(this@MutableCollectionBaseActivity, MutableCollectionViewActivity::class.java)
                intent.putExtra("UID", UID)
//                    setResult(MainActivity.RequestCodes.REQUEST_MANAGE_IMAGES,intent);
                intent.putExtra("firsttime", "no")
                intent.putExtra("Changed", "notOK" )
                startActivity(intent)
                finish()
//                recreate()

            }
        }

    }

    /*=====================end manager*/

}

/** A very simple collection of items. Optimized for simplicity (i.e. not performance). */
class ItemsViewModel : ViewModel() {
    private var nextValue = 1L

    public val UID = MutableCollectionBaseActivity.UID
    public var howmanypage = LibraryFilesystem.getCountOfPhotoScorePages(UID)

    //    private val items = (0..0).map { longToItem(nextValue++) }.toMutableList()
    private val items = (1..howmanypage).map { longToItem(nextValue++) }.toMutableList()
//   private val items = (1..2).map { longToItem(nextValue++) }.toMutableList()
//    private val items = (1..9).map { longToItem(nextValue++) }.toMutableList()

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
