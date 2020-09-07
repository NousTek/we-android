package com.we.beyond.presenter.gathering.gatheringByIssueId

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ConnectApi
import com.we.beyond.api.GatheringApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ConnectPojo
import com.we.beyond.model.DeleteGatheringPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.GatheringListPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Gathering Activity
 * which implement all api regarding get all gathering, deletion etc  */
class GatheringImpl (gathering : GatheringPresenter.IGatheringView) : GatheringPresenter.IGatheringPresenter {

    var gathering = gathering

    /** It calls getDataToPost method which takes below inputs  */
    override fun getGatheringList(context: Context, pageNo: Int, size: Int, startDate: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context,pageNo,size,startDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getGatheringList api which takes json object as parameter
     * and set gathering adapter
     */
    private fun getDataToPost(context: Context, pageNo: Int, size: Int, startDate: String)
    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getGatheringList(pageNo,size,startDate)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringPojo != null) {


                            gathering.setGatheringAdapter(gatheringPojo)

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

    /** It calls getDataToPostOnCriteria method which takes below inputs  */
    override fun getGatheringWithCriteriaList(
        context: Context,
        pageNo: Int,
        size: Int,
        startDate: String,
        endDate: String
    )
    {
        try{
            getDataToPostOnCriteria(context,pageNo,size,startDate,endDate)

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getGatheringListByCriteria api which takes below parameter
     * and set gathering adapter
     */
    private fun getDataToPostOnCriteria(context: Context, pageNo: Int, size: Int, startDate: String, endDate: String)
    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getGatheringListByCriteria(pageNo,size,startDate,endDate)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringPojo != null) {

                            gathering.setGatheringAdapter(gatheringPojo)

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
    override fun getGatheringListOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        startDate: String
    ) {

        try{
            getDataToPostOnLoadMore(context,pageNo,size,startDate)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getGatheringList api which takes below parameter
     * and set GatheringLoadMoreAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo: Int, size: Int, startDate: String)
    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getGatheringList(pageNo,size,startDate)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringPojo != null) {

                            gathering.setGatheringLoadMoreAdapter(gatheringPojo)

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

    /** It calls getDataToPostWithCriteriaOnLoadMore method which takes below inputs  */
    override fun getGatheringWithCriteriaListOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        startDate: String,
        endDate: String
    ) {
            try{
                getDataToPostWithCriteriaOnLoadMore(context,pageNo,size,startDate,endDate)

            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
    }

    /** It calls the getGatheringListByCriteria api which takes below parameter
     * and set GatheringLoadMoreAdapter
     */
    private fun getDataToPostWithCriteriaOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        startDate: String,
        endDate: String
    ) {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getGatheringListByCriteria(pageNo,size,startDate,endDate)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringPojo != null) {


                            gathering.setGatheringLoadMoreAdapter(gatheringPojo)

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

    /** It calls the deleteGathering api which takes json object as parameter
     * and notify Gathering Adapter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<DeleteGatheringPojo> = gatheringApi.deleteGathering(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteGatheringPojo>() {
                    override fun onSuccess(gatheringDetailsPojoPojo: DeleteGatheringPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringDetailsPojoPojo != null) {

                            gathering.setOnDelete(gatheringDetailsPojoPojo.data._id)

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