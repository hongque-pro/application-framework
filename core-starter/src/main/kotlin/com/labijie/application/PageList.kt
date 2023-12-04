package com.labijie.application

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-05
 */
data class PageList<TModel>(
    var items: Collection<TModel> = setOf(),
    var pageIndex: Long = 0,
    var pageSize: Long = 0,
    var totalItemCount: Long = 0
) {

    val pageCount: Long
        get() {
            if (pageSize <= 0 || totalItemCount <= 0) {
                return 0;
            }
            return totalItemCount / pageSize + (if ((totalItemCount % pageSize) > 0) 1 else 0)
        }
}