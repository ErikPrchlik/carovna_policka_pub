package maurit.carovna_polika

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject


class BookActivity : AppCompatActivity() {

    val context = this

    private lateinit var formatTxt: TextView
    private lateinit var contentTxt:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        setSupportActionBar(findViewById(R.id.toolbar))

        BookActivity.setContext(this)

        formatTxt = findViewById(R.id.scan_format)
        contentTxt = findViewById(R.id.scan_content)

        authorText = findViewById(R.id.book_author)
        titleText = findViewById(R.id.book_title)
        descriptionText = findViewById(R.id.book_description)
        dateText = findViewById(R.id.book_date)
        starLayout = findViewById(R.id.star_layout)
        ratingCountText = findViewById(R.id.book_rating_count)
        thumbView = findViewById(R.id.thumb)

        starViews = listOf()
        for (i in 1..5) {
            starViews.plus(ImageView(this))
        }

        formatTxt.text = this.intent.getStringExtra("scanFormat")
        contentTxt.text = this.intent.getStringExtra("scanContent")

        val isbn = contentTxt.text

        val apiKey = R.string.api_key
        val bookSearchString = "https://www.googleapis.com/books/v1/volumes?" + "q=ISBN:" + isbn + "&key=" + apiKey

        GetBookInfo().execute(bookSearchString)
    }

    companion object{

        private lateinit var authorText: TextView
        private lateinit var titleText:TextView
        private lateinit var descriptionText:TextView
        private lateinit var dateText:TextView
        private lateinit var ratingCountText:TextView
        private lateinit var thumbView: ImageView
        private lateinit var starLayout: LinearLayout
        private lateinit var starViews: List<ImageView>

        private lateinit var context: Context

        fun setContext(con: Context) {
            context=con
        }

        fun noData(){
            titleText.setText("NOT FOUND")
            authorText.setText("")
            descriptionText.setText("")
            dateText.setText("")
            starLayout.removeAllViews()
            ratingCountText.setText("")
            thumbView.setImageBitmap(null)
        }

        fun notComplete(volumeObject: JSONObject?) {
            try {
                titleText.text = "TITLE: " + volumeObject!!.getString("title")
            } catch (jse: JSONException) {
                titleText.text = ""
                jse.printStackTrace()
            }
            val authorBuild = StringBuilder("")
            try {
                val authorArray = volumeObject!!.getJSONArray("authors")
                for (a in 0 until authorArray.length()) {
                    if (a > 0) authorBuild.append(", ")
                    authorBuild.append(authorArray.getString(a))
                }
                authorText.text = "AUTHOR(S): $authorBuild"
            } catch (jse: JSONException) {
                authorText.text = ""
                jse.printStackTrace()
            }
            try {
                dateText.text = "PUBLISHED: " + volumeObject!!.getString("publishedDate")
            } catch (jse: JSONException) {
                dateText.text = ""
                jse.printStackTrace()
            }
            try {
                descriptionText.text = "DESCRIPTION: " + volumeObject!!.getString("description")
            } catch (jse: JSONException) {
                descriptionText.text = ""
                jse.printStackTrace()
            }
            try {
                //set stars
                val decNumStars = volumeObject!!.getString("averageRating").toDouble()
                val numStars = decNumStars.toInt()
                starLayout.setTag(numStars)
                starLayout.removeAllViews()
                starViews = listOf()
                for (i in 1..5) {
                    starViews += listOf(ImageView(context))
                }
                println(starViews)
//                for (s in 1 until android.R.attr.numStars) {
                for (s in 1..numStars) {
                    println(s)
                    starViews.get(s - 1).setImageResource(R.drawable.ic_star)
                    starLayout.addView(starViews[s - 1])
                }
                try {
                    ratingCountText.text = " - " + volumeObject.getString("ratingsCount").toString() + " ratings"
                } catch (jse: JSONException) {
                    ratingCountText.text = ""
                    jse.printStackTrace()
                }
            } catch (jse: JSONException) {
                starLayout.removeAllViews()
                jse.printStackTrace()
            }
        }
    }
}
