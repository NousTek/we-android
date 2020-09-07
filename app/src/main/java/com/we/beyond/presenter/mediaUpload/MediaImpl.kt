package com.we.beyond.presenter.mediaUpload

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.api.FileUploadApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.FileUploadPojo
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

/** It is implementation class which implement
 * api regarding upload image and video  */
class MediaImpl (media : MediaPresenter.IMediaView) : MediaPresenter.IMediaPresenter

{
    var media = media

    val imagesSet = HashSet<String>()
    val videosSet = HashSet<String>()
    val mimeTypeSet = HashSet<String>()


    /** It calls getDataToPostImage method which takes below input  */
    override fun onFileUpload(context: Context, filePath: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostImage(context, filePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the uploadImage api which below inputs
     * and set media adapter
     */
    private fun getDataToPostImage(context: Context, filePath: String) {
        try {

            val image = EasySP.init(context).getString("image")
            val file = File(filePath)
            if (file != null) {
                var mimeType = RealPathUtils.getMimeType(file)

                if (mimeType != null && mimeType.length > 0) {
                    var requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                    var multipartBody =
                        MultipartBody.Part.createFormData("file", file.name, requestFile)

                    if (multipartBody != null) {
                        try {
                            val uploadMediaApi = ApplicationController.retrofit.create(
                                FileUploadApi::class.java)
                            val response: Single<FileUploadPojo> =
                                uploadMediaApi.uploadImage(multipartBody)
                            response.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : DisposableSingleObserver<FileUploadPojo>() {
                                    override fun onSuccess(uploadPojo: FileUploadPojo) {

                                        ConstantMethods.cancleProgessDialog()

                                        if (uploadPojo != null) {
                                            if (uploadPojo.fileUrl != null && uploadPojo.fileUrl.length > 0) {
                                                if (ConstantMethods.checkForInternetConnection(
                                                        context
                                                    )
                                                ) {

                                                    imagesSet.add(uploadPojo.fileUrl)
                                                    mimeTypeSet.add(uploadPojo.fileMimeType)
                                                    println("image set $imagesSet")
                                                    EasySP.init(context).putStringSet("uploadedImage", imagesSet)

                                                    media.setImageAdapter(imagesSet,mimeTypeSet)

                                                }
                                            }
                                        }
                                    }

                                    override fun onError(e: Throwable) {
                                        ConstantMethods.cancleProgessDialog()
                                        try {
                                            if(e is IOException){
                                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                                            }
                                            else {
                                                val exception: HttpException = e as HttpException
                                                val er: String = exception.response()!!.errorBody()!!.string()
                                                val errorPojo: ErrorPojo =
                                                    Gson().fromJson(er, ErrorPojo::class.java)

                                                if (errorPojo != null) {
                                                    if (errorPojo.error.isNotEmpty()) {
                                                        if (errorPojo.message.isNotEmpty()) {
                                                            ConstantMethods.showError(
                                                                context,
                                                                errorPojo.error,
                                                                errorPojo.message
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        catch (e : Exception)
                                        {
                                            ConstantMethods.cancleProgessDialog()
                                            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                                R.string.error_message))

                                        }
                                    }

                                })

                        } catch (e: Exception) {
                            ConstantMethods.cancleProgessDialog()
                            ConstantMethods.showError(
                                context,
                                context.resources.getString(R.string.error_title),
                                "Please try after sometime"
                            )
                        }
                    }
                }
            }

        } catch (e: Exception) {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(
                context,
                context.resources.getString(R.string.error_title),
                context.resources.getString(
                    R.string.error_message
                )
            )
        }
    }

    /** It calls getDataToPostVideo method which takes below input  */
    override fun onVideoUpload(context: Context, filePath: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostVideo(context, filePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the uploadVideo api which below inputs
     * and set media adapter
     */
    private fun getDataToPostVideo(context: Context, filePath: String) {
        try {

            val video = EasySP.init(context).getString("video")
            val file = File(filePath)
            if (file != null) {
                var mimeType = RealPathUtils.getMimeType(file)

                if (mimeType != null && mimeType.length > 0) {
                    var requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                    var multipartBody =
                        MultipartBody.Part.createFormData("file", file.name, requestFile)

                    if (multipartBody != null) {
                        try {
                            val uploadMediaApi = ApplicationController.retrofit.create(
                                FileUploadApi::class.java)
                            val response: Single<FileUploadPojo> =
                                uploadMediaApi.uploadVideo(multipartBody)
                            response.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : DisposableSingleObserver<FileUploadPojo>() {
                                    override fun onSuccess(uploadPojo: FileUploadPojo) {

                                        ConstantMethods.cancleProgessDialog()

                                        if (uploadPojo != null) {
                                            if (uploadPojo.fileUrl != null && uploadPojo.fileUrl.length > 0) {
                                                if (ConstantMethods.checkForInternetConnection(
                                                        context
                                                    )
                                                ) {

                                                    videosSet.add(uploadPojo.fileUrl)
                                                    mimeTypeSet.add(uploadPojo.fileMimeType)
                                                    println("file url ${uploadPojo.fileUrl}")
                                                    println("video set $videosSet")
                                                    EasySP.init(context).putStringSet("uploadedVideo", videosSet)

                                                    media.setImageAdapter(videosSet,mimeTypeSet)

                                                }
                                            }
                                        }
                                    }
                                    override fun onError(e: Throwable) {
                                        ConstantMethods.cancleProgessDialog()
                                        try {
                                            if(e is IOException){
                                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                                            }
                                            else {
                                                val exception: HttpException = e as HttpException
                                                val er: String = exception.response()!!.errorBody()!!.string()
                                                val errorPojo: ErrorPojo =
                                                    Gson().fromJson(er, ErrorPojo::class.java)

                                                if (errorPojo != null) {
                                                    if (errorPojo.error.isNotEmpty()) {
                                                        if (errorPojo.message.isNotEmpty()) {
                                                            ConstantMethods.showError(
                                                                context,
                                                                errorPojo.error,
                                                                errorPojo.message
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        catch (e : Exception)
                                        {
                                            ConstantMethods.cancleProgessDialog()
                                            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                                R.string.error_message))

                                        }
                                    }

                                })

                        } catch (e: Exception) {
                            ConstantMethods.cancleProgessDialog()
                            ConstantMethods.showError(
                                context,
                                context.resources.getString(R.string.error_title),
                                "Please try after sometime"
                            )
                        }
                    }
                }
            }

        } catch (e: Exception) {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(
                context,
                context.resources.getString(R.string.error_title),
                context.resources.getString(
                    R.string.error_message
                )
            )
        }
    }

}