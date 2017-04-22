package com.github.kittinunf.fuel.toolbox

import com.facebook.stetho.urlconnection.ByteArrayRequestEntity
import com.facebook.stetho.urlconnection.StethoURLConnectionManager
import com.github.kittinunf.fuel.core.*
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.*
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection

class FuelStethoClient(val proxy: Proxy? = null) : Client {

    override fun executeRequest(request: Request): Response {
        val response = Response()
        response.url = request.url

        val connection = establishConnection(request) as HttpURLConnection
        val stetho = StethoURLConnectionManager("StethoFuelConnectionManager")

        try {
            connection.apply {
                val timeout = request.timeoutInMillisecond
                val timeoutRead = request.timeoutReadInMillisecond
                connectTimeout = timeout
                readTimeout = timeoutRead
                doInput = true
                useCaches = false
                requestMethod = request.httpMethod.value
                setDoOutput(connection, request.httpMethod)
                instanceFollowRedirects = false
                for ((key, value) in request.httpHeaders) {
                    setRequestProperty(key, value)
                }
                stetho.preConnect(connection, ByteArrayRequestEntity(request.httpBody))
                setBodyIfAny(connection, request.httpBody)
            }

            return response.apply {

                httpResponseHeaders = connection.headerFields ?: emptyMap()
                httpContentLength = connection.contentLength.toLong()

                val contentEncoding = connection.contentEncoding ?: ""

                val dataStream = if (connection.errorStream != null) {
                    connection.errorStream
                } else {
                    try {
                        val from = connection.inputStream
                        stetho.interpretResponseStream(from)
                    } catch(exception: IOException) {
                        null
                    }
                }

                if (dataStream != null) {
                    data = if (contentEncoding.compareTo("gzip", true) == 0) {
                        GZIPInputStream(dataStream).readBytes()
                    } else {
                        dataStream.readBytes()
                    }
                }
                stetho.postConnect()


                //try - catch just in case both methods throw
                try {
                    httpStatusCode = connection.responseCode
                    httpResponseMessage = connection.responseMessage.orEmpty()
                } catch(exception: IOException) {
                    throw exception
                }
            }
        } catch(exception: Exception) {
            throw FuelError().apply {
                this.exception = exception
                this.errorData = response.data
                this.response = response
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun establishConnection(request: Request): URLConnection {
        val urlConnection = if (proxy != null) request.url.openConnection(proxy) else request.url.openConnection()
        return if (request.url.protocol == "https") {
            val conn = urlConnection as HttpsURLConnection
            conn.apply {
                sslSocketFactory = request.socketFactory
                hostnameVerifier = request.hostnameVerifier
            }
        } else {
            urlConnection as HttpURLConnection
        }
    }

    private fun setBodyIfAny(connection: HttpURLConnection, bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            val outStream = BufferedOutputStream(connection.outputStream)
            outStream.write(bytes)
            outStream.close()
        }
    }

    private fun setDoOutput(connection: HttpURLConnection, method: Method) {
        when (method) {
            Method.GET, Method.DELETE, Method.HEAD -> connection.doOutput = false
            Method.POST, Method.PUT, Method.PATCH -> connection.doOutput = true
        }
    }
}



