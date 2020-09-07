package com.we.beyond

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface

import com.theartofdev.edmodo.cropper.CropImageView
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import com.we.beyond.util.FileUtils
import java.io.*
import java.util.*

/** It is used to set photo with crop */
class PhotoActivity : AppCompatActivity(), View.OnClickListener {

    /** init crop image view library*/
    var cropImageView: CropImageView? = null

    /** init text view */
    var tvCropPhotoTitle: TextView? = null

    /** init image view */
    var imgRotate: ImageView? = null
    var imgBack: ImageView? = null
    var btnDone: Button? = null

    /** init width and height */
    val maxWidth = 1280.0f
    val maxHeight = 1280.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo)

        getControls()
    }

    /** ui initialization
     * ui listeners
     * set fonts */
    private fun getControls() {
        try {
            cropImageView = findViewById(R.id.cropImageView)
            imgRotate = findViewById(R.id.imgRotate)
            imgBack = findViewById(R.id.img_back)
            tvCropPhotoTitle = findViewById(R.id.txt_title)
            btnDone = findViewById(R.id.btnDone)
            btnDone!!.setOnClickListener(this)
            imgRotate!!.setOnClickListener(this)

            //set Font
            tvCropPhotoTitle!!.setTypeface(ConstantFonts.raleway_semibold)
            btnDone!!.setTypeface(ConstantFonts.raleway_semibold)


            val uriString = getIntent().getStringExtra(Constants.CONSTANT_URI)
            val myUri = Uri.parse(uriString)

           // val path = RealPathUtils.getPath(this,myUri)
            val path = FileUtils().getRealPath(this,myUri)
            if (path != null){
                try {
                    val bitmap = compressImage(this,path)
                    if (bitmap != null){
                        cropImageView!!.setImageBitmap(bitmap)
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            // Bundle bundle = getIntent().getBundleExtra("imageData");
            // Bitmap imageBitmap = (Bitmap) bundle.get("data");
            // cropImageView.setImageUriAsync(myUri);
            cropImageView!!.setOnCropImageCompleteListener(object :
                CropImageView.OnCropImageCompleteListener {
                override fun onCropImageComplete(
                    view: CropImageView,
                    result: CropImageView.CropResult
                ) {
                    Toast.makeText(this@PhotoActivity, "done", Toast.LENGTH_SHORT).show()
                }
            })
            imgBack!!.setOnClickListener {
                finish()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        try {
            /** It will crop the image using library */
            if (v.getId() == R.id.btnDone) {
                val cropped = cropImageView!!.croppedImage
                val newCropped = getResizedBitmap(cropped, 800)
                val stream = ByteArrayOutputStream()
                newCropped.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                //val uri = getImageUriFromBitmap(this, newCropped)
                val uri = bitmapToUriConverter(this,newCropped)
                val intent = Intent()
                intent.putExtra(Constants.CROPPED_URI, uri.toString())
                // Constants.IN_PROFILE_PICTURE = new ByteArrayInputStream(stream.toByteArray());
                setResult(Activity.RESULT_OK, intent)
                finish()
                //cropImageView.setImageBitmap(cropped);
            } else if (v.id == R.id.imgRotate) {
                cropImageView!!.rotateImage(90)
            }
        }catch (e:Exception){
                e.printStackTrace()
        }
    }

    /** It resize the image */
    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.getWidth()
        var height = image.getHeight()
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun bitmapToUriConverter(context:Context,mBitmap:Bitmap):Uri {
        var uri:Uri?= null
        try
        {
            val options = BitmapFactory.Options()
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            val newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                true)
            val file = File(context.getFilesDir(), ("Image"
                    + Random().nextInt() + ".jpeg"))
            val out = context.openFileOutput(file.getName(),
                Context.MODE_PRIVATE)
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            //get absolute path
            val realPath = file.getAbsolutePath()
            val f = File(realPath)
            uri = Uri.fromFile(f)
        }
        catch (e:Exception) {
            Log.e("Your Error Message", e.message)
        }
        return uri!!
    }

    fun calculateInSampleSize(
        options:BitmapFactory.Options, reqWidth:Int, reqHeight:Int):Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth)
        {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth))
            {
                inSampleSize *= 2
            }
        }
        return inSampleSize


    }



    fun compressImage(context: Context, imagePath: String): Bitmap? {
        var scaledBitmap: Bitmap?

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
        var bmp: Bitmap? = BitmapFactory.decodeFile(imagePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()

            }
        }

        options.inSampleSize = calculateSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            return null
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            return null
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp, middleX - bmp!!.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

        bmp.recycle()

        val exif: ExifInterface
        try {
            exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val out: FileOutputStream?
        val filepath = getFilename(context)
        try {
            out = FileOutputStream(filepath)
            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return scaledBitmap
    }

    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    /** It create folder in internal storage and store image */
    private fun getFilename(context: Context): String {
        val mediaStorageDir = File("${Environment.getExternalStorageDirectory()}/Android/data/${context.applicationContext.packageName}/Files/Compressed")
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }

        val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        return mediaStorageDir.getAbsolutePath() + "/" + mImageName
    }

//    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
//        val bytes = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
//        return Uri.parse(path.toString())
//    }

}

