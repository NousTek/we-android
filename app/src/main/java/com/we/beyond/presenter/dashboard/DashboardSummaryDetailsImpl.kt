package com.we.beyond.presenter.dashboard

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.DashboardApi
import com.we.beyond.api.NearByIssueApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of SummaryDetails Activity
 * which implement all api regarding get unresolved, resolved, up coming gathering and publist connect list  */
class DashboardSummaryDetailsImpl (summary : DashBoardSummaryDetailsPresenter.IDashBoardSummaryDetailsView) : DashBoardSummaryDetailsPresenter.IDashBoardSummaryDetailsPresenter
{
    var summary = summary

    /** It calls getDataToPost method which takes below inputs  */
    override fun getUnresolvedIssues(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPost(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getUnresolvedList api which takes below parameters
     * and set UnresolvedIssueData
     */
    private fun getDataToPost(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val unresolvedIssueApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByIssuesPojo> = unresolvedIssueApi.getUnresolvedList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByIssuesPojo != null) {

                            summary.setUnresolvedIssueData(nearByIssuesPojo)

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
    override fun getUnresolvedIssuesOnLoadMore(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnLoadMore(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
                e.printStackTrace()
        }
    }

    /** It calls the getUnresolvedList api which takes below parameters
     * and set UnresolvedIssueData
     */
    private fun getDataToPostOnLoadMore(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val unresolvedIssueApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByIssuesPojo> = unresolvedIssueApi.getUnresolvedList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByIssuesPojo != null) {

                            summary.setUnresolvedIssueDataOnLoadMore(nearByIssuesPojo)

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

    /** It calls getDataToPostResolvedIssues method which takes below inputs  */
    override fun getResolvedIssues(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostResolvedIssues(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getResolvedList api which takes below parameters
     * and set ResolvedIssueData
     */
    private fun getDataToPostResolvedIssues(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val unresolvedIssueApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByIssuesPojo> = unresolvedIssueApi.getResolvedList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByIssuesPojo != null) {

                            summary.setResolvedIssueData(nearByIssuesPojo)

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

    /** It calls getDataToPostOnResolvedIssuesLoadMore method which takes below inputs  */
    override fun getResolvedIssuesOnLoadMore(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnResolvedIssuesLoadMore(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getResolvedList api which takes below parameters
     * and set ResolvedIssueData
     */
    private fun getDataToPostOnResolvedIssuesLoadMore(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val unresolvedIssueApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByIssuesPojo> = unresolvedIssueApi.getResolvedList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuesPojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()
                        if (nearByIssuesPojo != null) {

                            summary.setResolvedIssueDataOnLoadMore(nearByIssuesPojo)

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

    /** It calls getDataToPostUpcomingGathering method which takes below inputs  */
    override fun getUpcomingGathering(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostUpcomingGathering(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getUpcomingGatheringList api which takes below parameters
     * and set UpcomingGatheringData
     */
    private fun getDataToPostUpcomingGathering(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getUpcomingGatheringList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringListPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringListPojo != null) {

                            summary.setUpcomingGatheringData(gatheringListPojo)

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

    /** It calls getDataToPostOnUpcomingGatheringLoadMore method which takes below inputs  */
    override fun getUpcomingGatheringOnLoadMore(
        context: Context,
        latitude: String,
        longitude: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnUpcomingGatheringLoadMore(context,latitude,longitude,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getUpcomingGatheringList api which takes below parameters
     * and set UpcomingGatheringData
     */
    private fun getDataToPostOnUpcomingGatheringLoadMore(context: Context, latitude: String, longitude: String,pageNo: Int,pageSize: Int)
    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<GatheringListPojo> = gatheringApi.getUpcomingGatheringList(latitude,longitude,pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringListPojo>() {
                    override fun onSuccess(gatheringListPojo: GatheringListPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringListPojo != null) {

                            summary.setUpcomingGatheringDataOnLoadMore(gatheringListPojo)

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

    /** It calls getDataToPostNewInfoPublished method which takes below inputs  */
    override fun getNewPublished(context: Context, pageNo: Int, pageSize: Int) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostNewInfoPublished(context,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getPublishedConnectList api which takes below parameters
     * and set NewPublishedData
     */
    private fun getDataToPostNewInfoPublished(context: Context,pageNo: Int,pageSize: Int)
    {
        try {
            val publishedApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<ConnectPojo> = publishedApi.getPublishedConnectList(pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {

                            summary.setNewPublishedData(connectPojo)

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

    /** It calls getDataToPostOnNewPublishedLoadMore method which takes below inputs  */
    override fun getNewPublishedOnLoadMore(context: Context, pageNo: Int, pageSize: Int) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnNewPublishedLoadMore(context,pageNo,pageSize)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getPublishedConnectList api which takes below parameters
     * and set NewPublishedData
     */
    private fun getDataToPostOnNewPublishedLoadMore(context: Context,pageNo: Int,pageSize: Int)
    {
        try {
            val publishedApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<ConnectPojo> = publishedApi.getPublishedConnectList(pageNo,pageSize)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectPojo>() {
                    override fun onSuccess(connectPojo: ConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectPojo != null) {


                            summary.setNewPublishedDataOnLoadMore(connectPojo)

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


}