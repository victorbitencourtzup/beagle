/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.data

import br.com.zup.beagle.android.factory.networking.HttpClientFactory
import br.com.zup.beagle.android.networking.HttpClient
import br.com.zup.beagle.android.networking.RequestData
import br.com.zup.beagle.android.networking.ResponseData
import br.com.zup.beagle.android.networking.exception.BeagleApiException
import br.com.zup.beagle.android.networking.logger.BeagleNetworkingLogs
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


//TODO REMOVED INTERNAL
class BeagleApi(
    private val httpClient: HttpClient = HttpClientFactory().make()
) {
    companion object {
        const val BEAGLE_PLATFORM_HEADER_KEY = "beagle-platform"
        const val BEAGLE_PLATFORM_HEADER_VALUE = "ANDROID"
        const val CONTENT_TYPE = "Content-Type"
        const val APP_JSON = "application/json"
        val FIXED_HEADERS = mapOf(CONTENT_TYPE to APP_JSON, BEAGLE_PLATFORM_HEADER_KEY to BEAGLE_PLATFORM_HEADER_VALUE)
    }

    @Throws(BeagleApiException::class)
    suspend fun fetchData(request: RequestData): ResponseData = suspendCancellableCoroutine { cont ->
        val transformedRequest = request.let { it.copy(headers = it.headers + FIXED_HEADERS) }
        BeagleNetworkingLogs.logHttpRequestData(transformedRequest)
        val call = httpClient.execute(
            request = transformedRequest,
            onSuccess = { response ->
                BeagleNetworkingLogs.logHttpResponseData(response)
                cont.resume(response)
            }, onError = { response ->
            val exception = BeagleApiException(response, genericErrorMessage(transformedRequest.uri.toString()))

            BeagleNetworkingLogs.logUnknownHttpError(exception)
            cont.resumeWithException(
                exception
            )
        })
        cont.invokeOnCancellation {
            call.cancel()
        }
    }

    private fun genericErrorMessage(url: String) = "fetchData error for url $url"
}