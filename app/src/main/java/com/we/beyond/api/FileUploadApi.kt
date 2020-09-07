package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.FileUploadPojo
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploadApi
{
    /** Upload image */
    @Multipart
    @POST("api/files/file-upload")
    fun uploadImage(@Part file: MultipartBody.Part): Single<FileUploadPojo>

    /** Upload video */
    @Multipart
    @POST("api/videos/video-upload")
    fun uploadVideo(@Part file: MultipartBody.Part): Single<FileUploadPojo>

    /** Delete media  */
    @POST("api/files/file-delete")
    fun deleteMedia(@Body jsonObject: JsonObject): Single<FileUploadPojo>
}