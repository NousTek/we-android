package com.we.beyond.presenter.connect.connectIssue

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ConnectApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Connect Issue Activity
 * which implement api regarding get all categories and connect, delete connect */
class ConnectIssueImpl (connect : ConnectIssuePresenter.IConnectIssueView) : ConnectIssuePresenter.IConnectIssuePresenter
{


    var connect = connect

    /** It calls getConnectCategories method   */
    override fun onRequestConnectCategory(context: Context)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getConnectCategories(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls the getCategories api
     * and set Connect Category adapter
     */
    private fun getConnectCategories(context: Context)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectCategoriesPojo> = connectApi.getCategories()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectCategoriesPojo>() {
                    override fun onSuccess(connectCategoryPojo: ConnectCategoriesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectCategoryPojo != null) {

                            connect.setConnectCategoryAdapter(connectCategoryPojo.data)

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

        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }

    }

    /** It calls getDataToPost method which takes below inputs  */
    override fun getConnectList(context: Context, pageNo: Int, size: Int, sortBy: String)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPost(context,pageNo,size,sortBy)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls the getConnectList api which takes below parameter
     * and set Connect adapter
     */
    private fun getDataToPost(context: Context, pageNo: Int, size: Int, sortBy: String)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectList(pageNo,size,sortBy)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {

                            connect.setConnectAdapter(connectPojo)

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


        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }

    }

    /** It calls getDataToPostWithId method which takes below inputs  */
    override fun getConnectWithCategoryList(
        context: Context,
        pageNo: Int,
        size: Int,
        sortBy: String,
        connectCategoryId: String
    ) {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostWithId(context,pageNo,size,sortBy,connectCategoryId)
            }
        }
        catch (e : Exception)
        {
                e.printStackTrace()
        }
    }

    /** It calls the getConnectListByCategoryId api which takes json object as parameter
     * and set Connect adapter
     */
    private fun getDataToPostWithId(context: Context, pageNo: Int, size: Int, sortBy: String, connectCategoryId: String)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectListByCategoryId(pageNo,size,sortBy,connectCategoryId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (connectPojo != null) {


                            connect.setConnectAdapter(connectPojo)

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


        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }


    }

    /** It calls getDataToPostOnLoadMore method which takes below inputs  */
    override fun getConnectListOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        sortBy: String
    ) {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnLoadMore(context,pageNo,size,sortBy)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()

        }

    }

    /** It calls the getConnectList api which takes below parameter
     * and notify Connect adapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo: Int, size: Int, sortBy: String)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectList(pageNo,size,sortBy)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {

                            connect.setConnectLoadMoreAdapter(connectPojo)

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



        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }

    }

    /** It calls getDataToPostWithIdOnLoadMore method which takes below inputs  */
    override fun getConnectWithCategoryListOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        sortBy: String,
        connectCategoryId: String
    ) {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostWithIdOnLoadMore(context,pageNo,size,sortBy,connectCategoryId)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getConnectListByCategoryId api which takes below parameter
     * and notify Connect adapter
     */
    private fun getDataToPostWithIdOnLoadMore(context: Context, pageNo: Int, size: Int, sortBy: String, connectCategoryId: String)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectListByCategoryId(pageNo,size,sortBy,connectCategoryId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {

                            connect.setConnectLoadMoreAdapter(connectPojo)

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


        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }


    }

    /** It calls getDataToDelete method which takes context and json object as input  */
    override fun onDelete(
        context: Context,
        jsonObject: JsonObject
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToDelete(context,jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the deleteConnect api which takes json object as parameter
     * and notify connect Adapter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<DeleteConnectDetailsPojo> = connectApi.deleteConnect(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteConnectDetailsPojo>() {
                    override fun onSuccess(connectDetailsPojo: DeleteConnectDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectDetailsPojo != null) {


                            connect.setOnDelete(connectDetailsPojo.data._id)

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

    /** It calls getDataToPostOnSearch method which takes below inputs  */
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

    /** It calls the getConnectOnSearch api with below inputs
     * and notify connect Adapter
     */
    private fun getDataToPostOnSearch(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectOnSearch(searchText,pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {


                            connect.setConnectOnSearchAdapter(connectPojo)
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


    /** It calls getDataToPostWithIdOnLoadMore method which takes below inputs  */
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

    /** It calls the getConnectOnSearch api with below inputs
     * and notify connect Adapter
     */
    private fun getDataToPostOnSearchOnLoadMore(context: Context, searchText: String, pageNo: Int, size: Int)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectPojo> = connectApi.getConnectOnSearch(searchText,pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {


                            connect.setConnectOnSearchAdapterOnLoadMore(connectPojo)
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