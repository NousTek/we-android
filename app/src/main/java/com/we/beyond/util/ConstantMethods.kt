package com.we.beyond.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import cn.pedant.SweetAlert.SweetAlertDialog
import com.we.beyond.BuildConfig

import com.we.beyond.R
import com.we.beyond.ui.profile.EditProfileActivity
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object ConstantMethods {

    val maxWidth = 1280.0f
    val maxHeight = 1280.0f

    /** Check internet connection  */
    fun checkForInternetConnection(context: Context): Boolean {

        val connectivityManager: ConnectivityManager = context
            .getSystemService(
                android.content.Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null
                && networkInfo.isConnected
        if (isDeviceConnectedToInternet) {

            return true

        } else {
            ClientPlayer.runOnUI(Runnable {
                try {
                    ConstantControls.sadNoInternet =
                        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                    ConstantControls.sadNoInternet.setTitleText("No Internet")
                    ConstantControls.sadNoInternet.setContentText("Please check Internet connection.")
                    ConstantControls.sadNoInternet.show()


                } catch (e: Exception) {
                    Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show()
                }

            })
            return false
        }


    }
    fun bytesToHex(bytes:ByteArray):String {
        val sb = StringBuilder()
        for (b in bytes)
        {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    /** show progress dialog  */
    fun showProgessDialog(context: Context, message: String) {
        ClientPlayer.runOnUI(object : Runnable {
            public override fun run() {
                try {
                    ConstantControls.progressDialog =
                        SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    ConstantControls.progressDialog.getProgressHelper()
                        .setBarColor(Color.parseColor("#A5DC86"))
                    ConstantControls.progressDialog.setTitleText(message)
                    ConstantControls.progressDialog.setCancelable(false)
                    ConstantControls.progressDialog.show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /** show cancel dialog  */
    fun cancleProgessDialog() {
        ClientPlayer.runOnUI(object : Runnable {
            public override fun run() {
                try {
                    if (ConstantControls.progressDialog.isShowing()) {
                        ConstantControls.progressDialog.cancel()
                    }
                } catch (e: Exception) {

                }
            }
        })
    }

    fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob.set(year, month, day)
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        // val ageS = ageInt.toString()
        return ageInt
    }

    fun convertDateToServerDate(dateString: String): String {
        var serverDate = ""

        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        val FORMAT_DATETIME = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd")
//        sdf.setTimeZone(tz)
        try {
            date = sdf.parse(dateString)
            serverDate = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }



        return serverDate

    }

    /** Check email id is valid or not  */
    fun isValidEmail(target: String): Boolean {
        if (target == null) {
            return false
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }



     fun capitalize(capString:String):String {
        var captalisedString =""
        try {
            val capBuffer = StringBuffer()
            val capMatcher =
                Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString)
            while (capMatcher.find()) {
                capMatcher.appendReplacement(
                    capBuffer,
                    capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase()
                )
            }
            captalisedString =capMatcher.appendTail(capBuffer).toString()

        }catch (e:Exception){

        }
        return captalisedString
    }



    /** Convert date to server date  */
    fun convertDateStringToServerDateFull(dateString: String): String {
        var serverDate = ""

        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        val FORMAT_DATETIME = "dd MMM yyyy, hh:mm a"
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf2.setTimeZone(tz)
        try {
            date = sdf.parse(dateString)
            serverDate = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }



        return serverDate

    }

    fun convertDateStringToServerDateTodayFull(dateString: String): String {
        var serverDate = ""

        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        val FORMAT_DATETIME = "yyyy-MM-dd hh:mm:ss"
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf2.setTimeZone(tz)
        try {
            date = sdf.parse(dateString)
            serverDate = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }



        return serverDate

    }

    fun convertStringToDateStringFull(date1: String): String {
        var convertString = ""
        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        //String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        val FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        // String FORMAT_DATETIME = "yyyy-MM-dd'T'hh:mm:ss";
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("dd MMM yyyy, hh:mm a")
        sdf.setTimeZone(tz)
        // sdf2.setTimeZone(tz);
        try {
            date = sdf.parse(date1)
            convertString = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertString
    }



    fun convertStringToDateFull(date1: String): String {
        var convertString = ""
        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        //String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        val FORMAT_DATETIME = "dd MMM yyyy, hh:mm a"
        // String FORMAT_DATETIME = "yyyy-MM-dd'T'hh:mm:ss";
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("dd")
        sdf.setTimeZone(tz)
        // sdf2.setTimeZone(tz);
        try {
            date = sdf.parse(date1)
            convertString = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertString
    }


    fun convertStringToMonthFull(date1: String): String {
        var convertString = ""
        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        //String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        val FORMAT_DATETIME = "dd MMM yyyy, hh:mm a"
        // String FORMAT_DATETIME = "yyyy-MM-dd'T'hh:mm:ss";
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("MMM")
        sdf.setTimeZone(tz)
        // sdf2.setTimeZone(tz);
        try {
            date = sdf.parse(date1)
            convertString = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertString
    }


    fun convertDateStringToShow(dateString: String): String {
        var serverDate = ""

        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        val FORMAT_DATETIME = "dd-MM-yyyy hh:mm a"
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        // sdf2.setTimeZone(tz)
        try {
            date = sdf.parse(dateString)
            serverDate = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        return serverDate

    }

    fun getMediaOutputUri(context: Context): Uri? {
        // check for external storage
        if (isExternalStorageAvailable()) {
            // get URI
            // 1. get external directory
            val mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            // 2. create file name
            var fileName = ""
            var fileType = ""
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            fileName = "IMG_$timestamp"
            fileType = ".jpg"
            // 3. create the file
            var mediaFile: File? = null
            try {
                mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir)
                if (BuildConfig.DEBUG) {
                    Log.d("IMG", "getMediaOutputUri: " + Uri.fromFile(mediaFile))
                }
                return Uri.fromFile(mediaFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            // 4. return file's URI
            return null
        }
        // something went wrong
        return null
    }



    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
    /**
     * This method check the captured image orientation
     *
     * @param bitmap A variable of type Bitmap image.
     * @param path   A variable of type String of image path.
     * @return Bitmap corrected image
     */
    fun imageOrientationValidator(bitmap: Bitmap, path: String): Bitmap {
        var bitmap = bitmap

        val ei: ExifInterface
        try {
            ei = ExifInterface(path)

            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90f)

                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180f)

                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270f)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }


    /**
     * This method is use to rotate Bitmap image
     *
     * @param source A variable of type Bitmap image.
     * @param angle  A variable of type float of image angle.
     * @return Bitmap rotated image
     */
    fun rotateImage(source: Bitmap, angle: Float): Bitmap {

        var bitmap: Bitmap? = null
        val matrix = Matrix()
        matrix.postRotate(angle)
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        } catch (err: OutOfMemoryError) {
            err.printStackTrace()
        }

        return bitmap!!
    }

    fun compressImage(context: Context, bitmap: Bitmap): File {
        val tempUri = getImageUri(context.applicationContext, bitmap)
        val compress =
            compressImageBitmap(getRealPathFromURI(tempUri, context))
        val compressUri = getImageUri(context, compress)
        return File(getRealPathFromURI(compressUri, context))
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri, context: Context): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val ret = cursor.getString(idx)
        cursor.close()
        return ret
    }

    /**
     * This method use for Compressing Image
     *
     * @param path A variable of type String image path
     * @return Bitmap image file
     */
    fun compressImageBitmap(path: String): Bitmap {

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / 1024, photoH / 1024)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        return BitmapFactory.decodeFile(path, bmOptions)
    }


    /** Show warning dialog */
    fun showWarning(context: Context,title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Show error dialog  */
    fun showError(context: Context, title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Hides the keyboard */
    fun hideKeyBoard (context : Context,activity : Activity)
    {
        var view = activity.currentFocus

        if (view == null){
            view = View(activity)
        }
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    /** Show keyboard */
    fun showKeyBoard (context: Context)
    {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
    }


    fun copyFile(inputPath: String, inputFile: String, outputPath: String): String {
        var `in`: InputStream?
        val out: OutputStream?
        var path: String? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream(inputPath)
            path = outputPath + inputFile
            out = FileOutputStream(path)
            val buffer = ByteArray(1024)
            var read: Int = 0
            while ({ read = `in`!!.read(buffer);read }() != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null
            // write the output file (You have now copied the file)
            out.flush()
            out.close()
        } catch (fnfe1: FileNotFoundException) {

        } catch (e: Exception) {
            Log.e("tag", e.message)
        }
        return path!!
    }


    fun checkPermission(context: Context):Boolean {
        val result = ContextCompat.checkSelfPermission(context.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return (result == PackageManager.PERMISSION_GRANTED)
    }



    fun requestPermission(context: Context) {
        ActivityCompat.requestPermissions((context as Activity), arrayOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1)
    }



    fun saveBitmapToFile(file:File):File {
        try
        {
            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)

            inputStream.close()
            // The new size we want to scale to
            val REQUIRED_SIZE = 75
            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while ((o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE))
            {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()
            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            return file
        }
        catch (e:Exception) {
            return null!!
        }
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
        options: BitmapFactory.Options, reqWidth:Int, reqHeight:Int):Int {
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


    fun getCompressImage(context: Context, imagePath: String): String? {
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
            canvas.drawBitmap(
                bmp,
                middleX - bmp!!.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )

            bmp.recycle()


        val exif: androidx.exifinterface.media.ExifInterface
        try {
            exif = androidx.exifinterface.media.ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED)
            val matrix = Matrix()
            when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
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

        return filepath
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

    private fun getFilename(context: Context): String {
        val mediaStorageDir = File("${Environment.getExternalStorageDirectory()}/Android/data/${context.applicationContext.packageName}/Files/Compressed")
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }

        val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
        return mediaStorageDir.getAbsolutePath() + "/" + mImageName
    }

}