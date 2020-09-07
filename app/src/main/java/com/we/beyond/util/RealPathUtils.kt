package com.we.beyond
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi

import java.text.DecimalFormat
import okhttp3.ResponseBody
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

object RealPathUtils {

    val DOCUMENTS_DIR = "documents"
    // configured android:authorities in AndroidManifest (https://developer.android.com/reference/android/support/v4/content/FileProvider)
    val AUTHORITY = "\${applicationId}.my.package.name.provider"
    val HIDDEN_PREFIX = "."
    /**
     * TAG for log messages.
     */
    internal val TAG = "FileUtils"
    private val DEBUG = false // Set to true to enable logging
    /**
     * File and folder comparator. TODO Expose sorting option method
     */
    var sComparator:Comparator<File> = object:Comparator<File> {
        public override fun compare(f1:File, f2:File):Int {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                f2.getName().toLowerCase())
        }
    }
    /**
     * File (not directories) filter.
     */
    var sFileFilter:FileFilter = object:FileFilter {
        public override fun accept(file:File):Boolean {
            val fileName = file.getName()
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX)
        }
    }
    /**
     * Folder (directories) filter.
     */
    var sDirFilter:FileFilter = object:FileFilter {
        public override fun accept(file:File):Boolean {
            val fileName = file.getName()
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX)
        }
    }
    val downloadsDir:File
        get() {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }
    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    fun getExtension(uri:String):String? {
        if (uri == null)
        {
            return null
        }
        val dot = uri.lastIndexOf(".")
        if (dot >= 0)
        {
            return uri.substring(dot)
        }
        else
        {
            // No extension.
            return ""
        }
    }
    /**
     * @return Whether the URI is a local one.
     */
    fun isLocal(url:String):Boolean {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://"))
        {
            return true
        }
        return false
    }
    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    fun isMediaUri(uri:Uri):Boolean {
        return "media".equals(uri.getAuthority(), ignoreCase = true)
    }
    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    fun getUri(file:File):Uri? {
        if (file != null)
        {
            return Uri.fromFile(file)
        }
        return null
    }
    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    fun getPathWithoutFilename(file:File):File? {
        if (file != null)
        {
            if (file.isDirectory())
            {
                // no file to be split off. Return everything
                return file
            }
            else
            {
                val filename = file.getName()
                val filepath = file.getAbsolutePath()
                // Construct path without file name.
                var pathwithoutname = filepath.substring(0,
                    filepath.length - filename.length)
                if (pathwithoutname.endsWith("/"))
                {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
                }
                return File(pathwithoutname)
            }
        }
        return null
    }
    /**
     * @return The MIME type for the given file.
     */
    fun getMimeType(file:File):String {
        val extension = getExtension(file.name)
        if (extension!!.length > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1)).toString()
        return "application/octet-stream"
    }
    /**
     * @return The MIME type for the give Uri.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getMimeType(context:Context, uri:Uri):String {
        val file = File(getPath(context, uri))
        return getMimeType(file)
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri:Uri):Boolean {
        return AUTHORITY == uri.getAuthority()
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri:Uri):Boolean {
        return "com.android.externalstorage.documents" == uri.getAuthority()
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri:Uri):Boolean {
        return "com.android.providers.downloads.documents" == uri.getAuthority()
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri:Uri):Boolean {
        return "com.android.providers.media.documents" == uri.getAuthority()
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri:Uri):Boolean {
        return "com.google.android.apps.photos.content" == uri.getAuthority()
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return contentUri?.let {
                    getDataColumn(
                        context,
                        it,
                        selection,
                        selectionArgs
                    )
                }
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }
    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getFile(context:Context, uri:Uri):File? {
        if (uri != null)
        {
            val path = getPath(context, uri)
            if (path != null && isLocal(path))
            {
                return File(path)
            }
        }
        return null
    }
    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    fun getReadableFileSize(size:Int):String {
        val BYTES_IN_KILOBYTES = 1024
        val dec = DecimalFormat("###.#")
        val KILOBYTES = " KB"
        val MEGABYTES = " MB"
        val GIGABYTES = " GB"
        var fileSize = 0f
        var suffix = KILOBYTES
        if (size > BYTES_IN_KILOBYTES)
        {
            fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
            if (fileSize > BYTES_IN_KILOBYTES)
            {
                fileSize = fileSize / BYTES_IN_KILOBYTES
                if (fileSize > BYTES_IN_KILOBYTES)
                {
                    fileSize = fileSize / BYTES_IN_KILOBYTES
                    suffix = GIGABYTES
                }
                else
                {
                    suffix = MEGABYTES
                }
            }
        }
        return (dec.format(fileSize.toDouble()) + suffix).toString()
    }
    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     */
    fun createGetContentIntent():Intent {
        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.setType("*/*")
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }
    /**
     * Creates View intent for given file
     *
     * @param file
     * @return The intent for viewing file
     */
    fun getViewIntent(context:Context, url:String):Intent {
        val uri = Uri.parse(url)
       // val uri = FileProvider.getUriForFile(context, AUTHORITY, file)

        val intent = Intent(Intent.ACTION_VIEW)
       // val url = file.toString()
        if (url.contains(".doc") || url.contains(".docx"))
        {
            // Word document
            intent.setDataAndType(uri, "application/msword")
        }
        else if (url.contains(".pdf"))
        {
            // PDF file
            intent.setDataAndType(uri, "application/pdf")
        }
        else if (url.contains(".ppt") || url.contains(".pptx"))
        {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        }
        else if (url.contains(".xls") || url.contains(".xlsx"))
        {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        }
        else if (url.contains(".zip") || url.contains(".rar"))
        {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav")
        }
        else if (url.contains(".rtf"))
        {
            // RTF file
            intent.setDataAndType(uri, "application/rtf")
        }
        else if (url.contains(".wav") || url.contains(".mp3"))
        {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav")
        }
        else if (url.contains(".gif"))
        {
            // GIF file
            intent.setDataAndType(uri, "image/gif")
        }
        else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png"))
        {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg")
        }
        else if (url.contains(".txt"))
        {
            // Text file
            intent.setDataAndType(uri, "text/plain")
        }
        else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi"))
        {
            // Video files
            intent.setDataAndType(uri, "video/*")
        }
        else
        {
            intent.setDataAndType(uri, "*/*")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return intent
    }
    fun getDocumentCacheDir(@NonNull context:Context):File {
        val dir = File(context.getCacheDir(), DOCUMENTS_DIR)
        if (!dir.exists())
        {
            dir.mkdirs()
        }
        logDir(context.getCacheDir())
        logDir(dir)
        return dir
    }
    private fun logDir(dir:File) {
        if (!DEBUG) return
        Log.d(TAG, "Dir=" + dir)
        val files = dir.listFiles()
        for (file in files)
        {
            Log.d(TAG, "File=" + file.getPath())
        }
    }
    @Nullable
    fun generateFileName(@Nullable name:String, directory:File):File? {
        if (name == null)
        {
            return null
        }
        var file = File(directory, name)
        if (file.exists())
        {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0)
            {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists())
            {
                index++
                fileName = fileName + '('.toString() + index + ')'.toString() + extension
                file = File(directory, fileName)
            }
        }
        try
        {
            if (!file.createNewFile())
            {
                return null
            }
        }
        catch (e:IOException) {
            Log.w(TAG, e)
            return null
        }
        logDir(directory)
        return file
    }
    /**
     * Writes response body to disk
     *
     * @param body ResponseBody
     * @param path file path
     * @return File
     */
    fun writeResponseBodyToDisk(body:ResponseBody, path:String):File? {
        try
        {
            val target = File(path)
            var inputStream:InputStream? = null
            var outputStream:OutputStream? = null
            try
            {
                val fileReader = ByteArray(4096)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(target)
                while (true)
                {
                    val read = inputStream.read(fileReader)
                    if (read == -1)
                    {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
                return target
            }
            catch (e:IOException) {
                return null
            }
            finally
            {
                if (inputStream != null)
                {
                    inputStream.close()
                }
                if (outputStream != null)
                {
                    outputStream.close()
                }
            }
        }
        catch (e:IOException) {
            return null
        }
    }
    private fun saveFileFromUri(context:Context, uri:Uri, destinationPath:String) {
        var `is`:InputStream? = null
        var bos:BufferedOutputStream? = null
        try
        {
            `is` = context.getContentResolver().openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            if (`is` != null) {
                `is`.read(buf)
            }
            if (`is` != null) {
                do {
                    bos.write(buf)
                } while (`is`.read(buf) != -1)
            }
        }
        catch (e:IOException) {
            e.printStackTrace()
        }
        finally
        {
            try
            {
                if (`is` != null) `is`.close()
                if (bos != null) bos.close()
            }
            catch (e:IOException) {
                e.printStackTrace()
            }
        }
    }
    fun readBytesFromFile(filePath:String):ByteArray {
        var fileInputStream:FileInputStream? = null
        var bytesArray:ByteArray? = null
        try
        {
            val file = File(filePath)
            bytesArray = ByteArray(file.length().toInt())
            //read file into bytes[]
            fileInputStream = FileInputStream(file)
            fileInputStream.read(bytesArray)
        }
        catch (e:IOException) {
            e.printStackTrace()
        }
        finally
        {
            if (fileInputStream != null)
            {
                try
                {
                    fileInputStream.close()
                }
                catch (e:IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bytesArray!!
    }
    @Throws(IOException::class)
    fun createTempImageFile(context:Context, fileName:String):File {
        // Create an image file name
        val storageDir = File(context.getCacheDir(), DOCUMENTS_DIR)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getFileName(@NonNull context:Context, uri:Uri):String {
        val mimeType = context.getContentResolver().getType(uri)
        var filename:String? = null
        if (mimeType == null && context != null)
        {
            val path = getPath(context, uri)
            if (path == null)
            {
                filename = getName(uri.toString())
            }
            else
            {
                val file = File(path)
                filename = file.getName()
            }
        }
        else
        {
            val returnCursor = context.getContentResolver().query(uri, null, null, null, null)
            if (returnCursor != null)
            {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename!!
    }
    fun getName(filename:String):String? {
        if (filename == null)
        {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }



    // code

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

    fun imageOrientationValidator(bitmap: Bitmap?, path: String): Bitmap? {
        var bitmap = bitmap

        val ei: ExifInterface
        try {
            ei = ExifInterface(path)

            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap!!, 90f)

                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap!!, 180f)

                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap!!, 270f)
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
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {

        var bitmap: Bitmap? = null
        val matrix = Matrix()
        matrix.postRotate(angle)
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        } catch (err: OutOfMemoryError) {
            err.printStackTrace()
        }

        return bitmap
    }
    fun compressImage(context: Context, bitmap: Bitmap): File {
        val tempUri = getImageUri(context, bitmap)
        val compress = compressImageBitmap(getRealPathFromURI(context, tempUri)!!)
        val compressUri = getImageUri(context, compress)
        return File(getRealPathFromURI(context, compressUri))
    }



    fun getRealPathFromURI(context: Context,uri: Uri): String? {

        var path = ""
        if (context!!.contentResolver != null) {
            val cursor = context!!.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    fun getImageUri(context: Context, thumbnail: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, thumbnail, "Title", null)
        return Uri.parse(path.toString())

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

    fun getFileNameByUri(context: Context, uri: Uri): String {
        var filepath = ""//default fileName
        //Uri filePathUri = uri;
        var file: File? = null
        if (uri.scheme.toString().compareTo("content") == 0) {
            val cursor = context.contentResolver.query(
                uri,
                arrayOf<String>(
                    android.provider.MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.Media.ORIENTATION
                ),
                null,
                null,
                null
            )
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val mImagePath = cursor.getString(column_index)
            cursor.close()
            filepath = mImagePath
        } else if (uri!!.scheme!!.compareTo("file") == 0) {
            try {
                file = File(URI(uri.toString()))
                if (file.exists())
                    filepath = file.absolutePath
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            filepath = uri.path!!
        }


        return filepath

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

}//private constructor to enforce Singleton pattern