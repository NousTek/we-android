package com.we.beyond.presenter.mediaUpload

import android.content.Context

interface MediaPresenter
{
    /** this interface and functions are showing result on view */
    interface IMediaView {
        fun setImageAdapter(imagesSet: HashSet<String>,mimeType : HashSet<String>)

    }

    /** this interface is working for calling respected apis */
    interface  IMediaPresenter {
        fun onFileUpload(context: Context, filePath: String)
        fun onVideoUpload(context: Context, filePath: String)
    }
}