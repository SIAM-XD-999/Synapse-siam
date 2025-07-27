package com.synapse.social.studioasinc

import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

//This project is created by @rjfahad_bd71
object ImageUploader {

    interface UploadCallback {
        fun onUploadComplete(imageUrl: String)
        fun onUploadError(errorMessage: String)
    }

    interface UploadProgressListener {
        fun onProgress(filePath: String, progress: Int)
    }

    // Use @JvmStatic to make this method directly accessible as static from Java
    @JvmStatic
    fun uploadImage(filePath: String, callback: UploadCallback) {
        ImageUploadTask(callback, null).execute(filePath)
    }

    // Use @JvmStatic to make this method directly accessible as static from Java
    @JvmStatic
    fun uploadImageWithProgress(filePath: String, listener: UploadProgressListener): Task<Uri> {
        val taskCompletionSource = TaskCompletionSource<Uri>()
        ImageUploadTask(
            object : UploadCallback {
                override fun onUploadComplete(imageUrl: String) {
                    taskCompletionSource.setResult(Uri.parse(imageUrl))
                }

                override fun onUploadError(errorMessage: String) {
                    taskCompletionSource.setException(RuntimeException(errorMessage))
                }
            },
            listener
        ).execute(filePath)
        return taskCompletionSource.task
    }

    private class ImageUploadTask(
        private val callback: UploadCallback,
        private val progressListener: UploadProgressListener?
    ) : AsyncTask<String, Int, String>() {

        private var currentFilePath: String? = null

        override fun doInBackground(vararg params: String): String {
            currentFilePath = params[0]
            try {
                val file = File(currentFilePath)
                val boundary = "*****"
                val lineEnd = "\r\n"
                val twoHyphens = "--"

                val url = URL("https://api.imgbb.com/1/upload?expiration=0&key=faa85ffbac0217ff67b5f3c4baa7fb29")

                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Connection", "Keep-Alive")
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

                val dos = DataOutputStream(connection.outputStream)

                dos.writeBytes(twoHyphens + boundary + lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"${file.name}\"$lineEnd")
                dos.writeBytes(lineEnd)

                val fileInputStream = FileInputStream(file)
                val bufferSize = 1024
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                var totalBytesSent: Long = 0
                val totalBytes = file.length()

                while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                    dos.write(buffer, 0, bytesRead)
                    totalBytesSent += bytesRead
                    val progress = ((totalBytesSent * 100) / totalBytes).toInt()
                    publishProgress(progress)
                }

                dos.writeBytes(lineEnd)
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                fileInputStream.close()
                dos.flush()
                dos.close()

                val responseCode = connection.responseCode
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (`in`.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    `in`.close()
                    response.toString()
                } else {
                    "Error: $responseCode"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return "Error: ${e.message}"
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            val progress = values[0] ?: 0
            currentFilePath?.let { filePath ->
                progressListener?.onProgress(filePath, progress)
            }
        }

        override fun onPostExecute(result: String) {
            if (result.startsWith("Error")) {
                callback.onUploadError(result)
            } else {
                handleUploadResponse(result)
            }
        }

        private fun handleUploadResponse(response: String) {
            try {
                val jsonResponse = JSONObject(response)
                val data = jsonResponse.getJSONObject("data")
                val imageUrl = data.getString("url")
                callback.onUploadComplete(imageUrl)
            } catch (e: JSONException) {
                e.printStackTrace()
                callback.onUploadError("Error parsing JSON response")
            }
        }
    }
}