package tk.zedlabs.wallportal.repository

import android.util.Log
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import tk.zedlabs.wallportal.data.JsonApi
import tk.zedlabs.wallportal.data.RetrofitService
import tk.zedlabs.wallportal.models.WallHavenResponse

class PopularDataSource(private val scope: CoroutineScope) : PageKeyedDataSource<Int, WallHavenResponse>() {

    companion object {
        private const val FIRST_PAGE = 1
    }

    private val queryParam = "minimal||vaporwave||retrowave||noir"
    private val sorting = "views"
    var jsonApi: JsonApi = RetrofitService.createService(JsonApi::class.java)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, WallHavenResponse>
    ) {

        scope.launch {
            try {
                val response = jsonApi.getImageList(queryParam, sorting, FIRST_PAGE)
                when {
                    response.isSuccessful -> {
                        callback.onResult(response.body()?.data ?: emptyList(), null, FIRST_PAGE + 1)
                    }
                }
            } catch (exception: Exception) {
                Log.e("repository->Posts", "1" + exception.message)
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, WallHavenResponse>
    ) {

        scope.launch {
            try {
                val response = jsonApi.getImageList(queryParam, sorting, params.key)
                when {
                    response.isSuccessful -> {
                        val key: Int?
                        if (response.body()?.data?.isNotEmpty() == true) key = params.key + 1
                        else key = null
                        callback.onResult(response.body()?.data ?: emptyList(), key)
                    }
                }
            } catch (exception: Exception) {
                Log.e("repository->Popular", "1" + exception.message)
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, WallHavenResponse>
    ) {

        scope.launch {
            try {
                val response = jsonApi.getImageList(queryParam, sorting, params.key)
                val key: Int? = if (params.key > 1) params.key - 1
                else null
                when {
                    response.isSuccessful -> {
                        callback.onResult(response.body()?.data ?: emptyList(), key)
                    }
                }
            } catch (exception: Exception) {
                Log.e("repository->Popular", "1" + exception.message)
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }
}