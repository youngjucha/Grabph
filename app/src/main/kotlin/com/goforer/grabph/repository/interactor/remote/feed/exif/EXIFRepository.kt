/*
 * Copyright 2019 Lukoh Nam, goForer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.goforer.grabph.repository.interactor.remote.feed.exif

import androidx.lifecycle.LiveData
import com.goforer.grabph.presentation.vm.BaseViewModel
import com.goforer.grabph.repository.interactor.remote.Repository
import com.goforer.grabph.repository.model.cache.data.entity.exif.EXIF
import com.goforer.grabph.repository.model.cache.data.entity.exif.PhotoEXIF
import com.goforer.grabph.repository.model.dao.remote.feed.exif.EXIFDao
import com.goforer.grabph.repository.network.response.ApiResponse
import com.goforer.grabph.repository.network.response.Resource
import com.goforer.grabph.repository.network.resource.NetworkBoundResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EXIFRepository
@Inject
constructor(private val dao: EXIFDao): Repository() {
    companion object {
        const val METHOD = "flickr.photos.getexif"
    }

    override suspend fun load(viewModel: BaseViewModel, query1: String, query2: Int, loadType: Int,
                              boundType: Int, calledFrom: Int): LiveData<Resource> {
        return object: NetworkBoundResource<MutableList<EXIF>, List<EXIF>, PhotoEXIF>(loadType,
                                                                                        boundType) {
            override suspend fun saveToCache(item: MutableList<EXIF>) =  dao.insert(item)

            // This function had been blocked at this time but it might be used in the future
            /*
            override fun shouldFetch(): Boolean {
                return true
            }
            */

            override suspend fun loadFromCache(isLatest: Boolean, itemCount: Int, pages: Int): LiveData<List<EXIF>> = dao.getEXIF()

            override suspend fun loadFromNetwork(): LiveData<ApiResponse<PhotoEXIF>> = searpService.getPhotoEXIF(KEY, query1, METHOD, FORMAT_JSON, INDEX)

            override fun onNetworkError(errorMessage: String?, errorCode: Int) {}

            override fun onFetchFailed(failedMessage: String?) = repoRateLimit.reset(query1)

            override suspend fun clearCache() = dao.clearAll()
        }.getAsLiveData()
    }

    internal fun removeEXIF() =  dao.clearAll()
}