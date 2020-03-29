package com.johngoodstadt.freelancermanageimages

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_manage_images.*


class ManageImagesActivity : AppCompatActivity() {

    var UID = "BACH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_images)


        val intent = intent


        UID = intent.getStringExtra("UID")
        println(UID)

        button2.setOnClickListener {

            setResult(Activity.RESULT_OK) //signal something changed
            finish()

        }

        button3.setOnClickListener {
            setResult(Activity.RESULT_CANCELED) //nothing changed
            finish()
        }
        //add file at end
        button4.setOnClickListener {
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
        button5.setOnClickListener {

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
        button6.setOnClickListener {
            val filenumber = 1
            val count = LibraryFilesystem.getCountOfPhotoScorePages(UID)

            if(count == 0){
                return@setOnClickListener
            }

            removeFileAndRenameDown(UID, filenumber, count)

            writeDebug()
        }
        //remove file from end
        button7.setOnClickListener {

            val filenumber = LibraryFilesystem.getCountOfPhotoScorePages(UID)
            if(filenumber == 0){
                return@setOnClickListener
            }

            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())
            LibraryFilesystem.removeFile(filename)

            writeDebug()
        }

        //crop image 2
        button8.setOnClickListener {

            if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                return@setOnClickListener
            }

            val filenumber = 2
            val filename = LibraryFilesystem.getFileNameByUID(UID,filenumber.toString())
            val uri = LibraryFilesystem.getUriFromFilename( filename)
            CropImage.activity(uri).start(this);

            writeDebug()
        }

        button9.setOnClickListener {

            if(LibraryFilesystem.getCountOfPhotoScorePages(UID) == 0){
                return@setOnClickListener
            }


            CropImage.activity().start(this);

            writeDebug()
        }
        writeDebug()



    }
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
                println("Something changed")
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
}
