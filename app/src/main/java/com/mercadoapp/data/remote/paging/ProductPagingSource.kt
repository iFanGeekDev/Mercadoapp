package com.mercadoapp.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mercadoapp.data.remote.api.MercadoApiService
import com.mercadoapp.data.remote.mapper.toDomain
import com.mercadoapp.domain.model.Product

class ProductPagingSource(
    private val api: MercadoApiService,
    private val category: String? = null,
    private val search: String? = null
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 1
        return try {
            val response = api.getProducts(
                page = page, 
                size = params.loadSize, 
                category = category,
                search = search
            )
            LoadResult.Page(
                data = response.items.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
