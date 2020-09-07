package com.we.beyond.Interface

interface OnChangeDataFilterListener
{
    fun onDataChangeSortBy(selectedValue : String)
    fun onDataChangeCategorySelected(categorySelectedCount : Int)
    fun onDataChangeKmRadious(kmRadiusSelected : String )
}