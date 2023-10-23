package maurit.carovna_polika

import android.os.AsyncTask
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class GetBookInfo: AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg bookURLs: String?): String {
        val url = bookURLs.first()
        val bookBuilder = StringBuilder()
//        for (bookSearchURL in bookURLs) {
            //search urls
            val bookClient: HttpClient = DefaultHttpClient()
            try {
                //get the data
                val bookGet = HttpGet(url)
                val bookResponse = bookClient.execute(bookGet)
                val bookSearchStatus: StatusLine = bookResponse.statusLine
                if (bookSearchStatus.getStatusCode() === 200) {
                    //we have a result
                    val bookEntity = bookResponse.entity
                    val bookContent: InputStream = bookEntity.content
                    val bookInput = InputStreamReader(bookContent)
                    val bookReader = BufferedReader(bookInput)
                    var lineIn: String?
                    while (bookReader.readLine().also { lineIn = it } != null) {
                        bookBuilder.append(lineIn)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//        }
        return bookBuilder.toString()
    }

    override fun onPostExecute(result: String?) {
        //parse search results
        try {
            //parse results
            val resultObject = JSONObject(result)
            val bookArray = resultObject.getJSONArray("items")
            val bookObject = bookArray.getJSONObject(0)
            val volumeObject = bookObject.getJSONObject("volumeInfo")
            BookActivity.notComplete(volumeObject)
        } catch (e: java.lang.Exception) {
            //no result
            e.printStackTrace()
            BookActivity.noData()
        }
    }
}