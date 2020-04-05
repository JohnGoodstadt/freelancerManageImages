package com.johngoodstadt.freelancermanageimages

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.johngoodstadt.freelancermanageimages.LibraryFilesystem.getUriFromFilename
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    object RequestCodes {
        const val REQUEST_MANAGE_IMAGES = 1000
    }

    val UID = "BACH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textview1.setOnClickListener {
                        val intent = Intent(this@MainActivity, MutableCollectionViewActivity::class.java)
                        startActivity(intent)
        }

        button1.setOnClickListener {
           println("")
//            val intent = Intent(this@MainActivity, ManageImagesActivity::class.java)
//            intent.putExtra("UID", UID)
//            startActivityForResult(intent, RequestCodes.REQUEST_MANAGE_IMAGES)

//            val intent = Intent(this@MainActivity, MutableCollectionViewActivity::class.java)
            val intent = Intent(this@MainActivity, ManageImagesActivity::class.java)
            intent.putExtra("UID", UID)
            startActivityForResult(intent, RequestCodes.REQUEST_MANAGE_IMAGES)


        }


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

        refreshImages()
    }

    private fun refreshImages() {

        imageView1.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "1")))
        imageView2.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "2")))
        imageView3.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "3")))
        imageView4.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "4")))
        imageView5.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "5")))
        imageView6.setImageURI(getUriFromFilename(LibraryFilesystem.getFileNameByUID(UID, "6")))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCodes.REQUEST_MANAGE_IMAGES) {

            if (resultCode == Activity.RESULT_OK){
                println("Something changed")
                refreshImages()
            }else{
                println("Nothing done")
            }

        }
    }
}
