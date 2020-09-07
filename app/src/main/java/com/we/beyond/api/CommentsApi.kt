package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.*

interface CommentsApi
{
    /** Get all comments by id  */
    @GET("api/comment/getCommentsByPost")
    fun getCommentsById(@Query("id")id : String, @Query("type")commentType : String , @Query("pageNo") pageNo: Int,
                     @Query("size")pageSize : Int,  @Query("projection") projection : String) : Single<CommentsPojo>


    /** Create comment with data */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/comment")
    fun postComment(@Body jsonObject: JsonObject): Single<CreateCommentPojo>

    /** Get comment details by using comment id */
    @GET("api/comment/getAllCommentsByCommentId")
    fun getCommentsHistory(@Query("commentId")id : String, @Query("projection") projection : String) : Single<CommentDetailsPojo>

   /* @PATCH("api/comment/deleteComment")
    fun deleteComment(@Query ("commentId")commentId: String) : Single<DeleteCommentPojo>*/

    /** Update comment  */
    @PATCH("api/comment/deleteComment")
    fun deleteComment(@Body jsonObject: JsonObject) : Single<DeleteCommentPojo>

    /** Update comments  */
    @PATCH("api/comment/updateComment")
    fun updateComment(@Body jsonObject: JsonObject) : Single<UpdateCommentPojo>


}


