package com.we.beyond.presenter.issues.nearByIssues

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ConnectApi
import com.we.beyond.api.NearByIssueApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteConnectDetailsPojo
import com.we.beyond.model.DeleteNearByIssueByIdDetailsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByIssuesPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Near by Issue Activity
 * which implement api regarding  get all issues, deletion,search */
class NearByIssueImpl (nearByIssue : NearByIssuePresenter.INearByIssueView) :
    NearByIssuePresenter.INearByIssuePresenter
{



    var nearByIssue = nearByIssue

    /** It calls getDataToPost method which takes context and json object as input  */
    override fun onNearByIssue(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls getDataToPostOnLoadMore method which takes context and json object as input  */
    override fun onNearByIssueOnLoadMore(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getNearByIssues api which takes json object as parameter
     * and sets NearByIssueAdapter
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject)
    {

        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<NearByIssuesPojo> = issueApi.getNearByIssues(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {
                        //if (nearByIssuePojo != null) {


                            ConstantMethods.cancleProgessDialog()

                             nearByIssue.setNearByIssueAdapter(nearByIssuePojo)

                       // }
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


        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }
    }

    /** It calls the getNearByIssues api which takes json object as parameter
     * and notify NearByIssueAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, jsonObject: JsonObject)
    {

        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<NearByIssuesPojo> = issueApi.getNearByIssues(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {
                        //if (nearByIssuePojo != null) {


                        ConstantMethods.cancleProgessDialog()

                        nearByIssue.setNearByIssueAdapterOnLoadMore(nearByIssuePojo)

                        // }
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

        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }
    }

    /** It calls getDataToDelete method which takes context and json object as input  */
    override fun onDelete(context: Context, jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToDelete(context,jsonObject)
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It calls the deleteIssue api which takes json object as parameter
     * and notify NearByIssueAdapter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<DeleteNearByIssueByIdDetailsPojo> = issueApi.deleteIssue(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteNearByIssueByIdDetailsPojo>() {
                    override fun onSuccess(deleteNearByIssueByIdDetailsPojo: DeleteNearByIssueByIdDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (deleteNearByIssueByIdDetailsPojo != null) {

                            nearByIssue.setOnDelete(deleteNearByIssueByIdDetailsPojo.data._id)

                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(R.string.no_internet_sub_title)
                                )
                            } else {
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

                })


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

    /** It calls getDataToPostOnSearch method which takes context and json object as input  */
    override fun onSearch(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnSearch(context, searchText,pageNo,size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    /** It calls the getIssueOnSearch api which takes json object as parameter
     * and set NearByIssueOnSearchAdapter
     */
    private fun getDataToPostOnSearch(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<NearByIssuesPojo> = issueApi.getIssueOnSearch(searchText,pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByIssuesPojo != null) {


                            nearByIssue.setNearByIssueOnSearchAdapter(nearByIssuesPojo)
                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(R.string.no_internet_sub_title)
                                )
                            } else {
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

                })


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


    /** It calls getDataToPostOnSearchOnLoadMore method which takes context and json object as input  */
    override fun onSearchLoadMore(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnSearchOnLoadMore(context, searchText,pageNo,size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getIssueOnSearch api which takes json object as parameter
     * and notify NearByIssueOnSearchAdapter
     */
    private fun getDataToPostOnSearchOnLoadMore(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<NearByIssuesPojo> = issueApi.getIssueOnSearch(searchText,pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByIssuesPojo != null) {


                            nearByIssue.setNearByIssueOnSearchAdapterOnLoadMore(nearByIssuesPojo)
                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(R.string.no_internet_sub_title)
                                )
                            } else {
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

                })


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