package com.example.socialmedia.ui.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.widget.TextView
import com.example.socialmedia.R
import com.example.socialmedia.di.BaseApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.util.Calendar
import java.util.Date


class Utils {
    companion object {
        @SuppressLint("StaticFieldLeak")
        //val context = BaseApplication.instance.applicationContext

       // @SuppressLint("StaticFieldLeak")
        val firestore = FirebaseFirestore.getInstance()


        private val auth = FirebaseAuth.getInstance()
        private var userid: String = ""
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }
}

fun calculateDay(year: Int, monthOfYear: Int, dayOfMonth: Int): Long {
    val dateSelect = Calendar.getInstance()
    dateSelect.set(year, monthOfYear, dayOfMonth)
    val millis = Calendar.getInstance().timeInMillis - dateSelect.timeInMillis
    return millis / (24 * 60 * 60 * 1000)
}

private fun setUpAddress(tvGeolocation: TextView) {
    val geocoder = Geocoder(tvGeolocation.context)
    val latitude = 21.028668268482367
    val longitude = 105.83721414784287
    val results = geocoder.getFromLocation(latitude, longitude, 1)
    if (results != null) {
        if (results.isNotEmpty()) {
            val address = results[0].getAddressLine(0).split(",")
            tvGeolocation.text = address[0]
        } else {
            tvGeolocation.text = String.format("Unable to find location")
        }
    }
}
fun setTextView(num: Int?) : String {
    return if (num == null) {
        "0"
    } else if (num > 1000000) {
        String.format("%.1f", num.toDouble() / 1000000) + "tr"
    } else if (num > 1000) {
        String.format("%.1f", num.toDouble() / 1000) + "k"
    } else {
        num.toString()
    }
}
fun shareApp(context: Context) {
    val appPackageName = context.packageName
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(
        Intent.EXTRA_TEXT,
        "${context.getText(R.string.app_name)} -> https://play.google.com/store/apps/details?id=$appPackageName"
    )
    sendIntent.type = "text/plain"
    context.startActivity(sendIntent)
}

fun stringToBitMap(encodedString: String): Bitmap? {
    return try {
        val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: Exception) {
        e.message
        null
    }
}

fun String.asDrawable(context: Context): Drawable {
    val resources = context.resources
    val resourceId: Int = resources.getIdentifier(
        this, "drawable", context.packageName
    )
    return resources.getDrawable(resourceId)
}

fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)

    var filePath: String? = null

    val cursor = context.contentResolver.query(contentURI, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
        }
    }
    return filePath
}

fun bitMapToString(context: Context, contentURI: Uri): String {
    val imageStream = context.contentResolver.openInputStream(contentURI)
    val bitmap = BitmapFactory.decodeStream(imageStream)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}
fun getTimeDate(): Long = System.currentTimeMillis()

fun Context.showDialogErr(content: String, Unit: (()-> Unit)? = null){
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    builder.setTitle("Error")
    builder.setMessage(content)
    builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
       Unit?.invoke()
    })
    val dialog: AlertDialog = builder.create()
    dialog.show()
}
 fun Context.isInternetConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (connectivityManager != null) {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    return false
}
fun getDistance(date: String): Long {
    val cal = Calendar.getInstance()
    return try {
        cal.time.time - DateProvider.dateFormat.parse(date).time
    } catch (e: ParseException) {
        e.printStackTrace()
        -1
    }
}
 fun getYearOld(date : String): Int {
    val years: IntArray =
        getDistanceYear((getDistance(date).div(100) / 86400), date)
    return years[0]
}
private fun getDistanceYear(days: Long,date: String): IntArray {
    var days = days
    var count = 0
    var sumDays = 0
    val cal = Calendar.getInstance()
    for (i in DateProvider.dateFormat.parse(date).year until cal.time.year) {
        days -= getDaysOfYear(i).toLong()
        if (days >= 0) {
            sumDays += getDaysOfYear(i)
            count++
        } else break
    }
    return intArrayOf(count, sumDays)
}
private fun getDaysOfYear(year: Int): Int {
    return if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) 366 else 365
}

fun getTimeAgo(time: Long): String {

    val now = Date().time
    if (time > now || time <= 0) {
        return "in the future"
    }
    val diff = now - time
    val SECOND_MILLIS = 1000L
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS
    val WEEK_MILLIS = 7 * DAY_MILLIS
    val MONTH_MILLIS = 30 * DAY_MILLIS
    val YEAR_MILLIS = 365 * DAY_MILLIS

    return when {
        diff < SECOND_MILLIS -> "Khoảnh khắc trước"
        diff < 2 * MINUTE_MILLIS -> "1 phút trước"
        diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} phút trước"
        diff < 90 * MINUTE_MILLIS -> "1 giờ trước"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} giờ trước"
        diff < 48 * HOUR_MILLIS -> "1 Ngày trước"
        diff < WEEK_MILLIS -> "${diff / DAY_MILLIS} ngày trước"
        diff < 2 * WEEK_MILLIS -> "1 tuần trước"
        diff < MONTH_MILLIS -> "${diff / WEEK_MILLIS} tuần trước"
        diff < 2 * MONTH_MILLIS -> "1 tháng trước"
        diff < YEAR_MILLIS -> "${diff / MONTH_MILLIS} tháng trước"
        diff < 2 * YEAR_MILLIS -> "1 năm trước"
        else -> "${diff / YEAR_MILLIS} năm trước"
    }
}

fun getLastTimeAgo(time: Long): String {

    val now = Date().time
    if (time > now || time <= 0) {
        return "in the future"
    }
    val diff = now - time
    val SECOND_MILLIS = 1000L
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS
    val WEEK_MILLIS = 7 * DAY_MILLIS
    val MONTH_MILLIS = 30 * DAY_MILLIS
    val YEAR_MILLIS = 365 * DAY_MILLIS

    return when {
        diff < SECOND_MILLIS -> "Just now"
        diff < 2 * MINUTE_MILLIS -> "1 min ago"
        diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} min ago"
        diff < 90 * MINUTE_MILLIS -> "1 hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS}  hour ago"
        diff < 48 * HOUR_MILLIS -> "1 day ago"
        diff < WEEK_MILLIS -> "${diff / DAY_MILLIS} day ago"
        diff < 2 * WEEK_MILLIS -> "1 week ago"
        diff < MONTH_MILLIS -> "${diff / WEEK_MILLIS} week ago"
        diff < 2 * MONTH_MILLIS -> "1 month ago"
        diff < YEAR_MILLIS -> "${diff / MONTH_MILLIS} month ago"
        diff < 2 * YEAR_MILLIS -> "1 year ago"
        else -> "${diff / YEAR_MILLIS} year ago"
    }
}


fun String?.contains(substring: String?): Boolean {
    return if (this != null && substring != null) {
        val charSequence: CharSequence = this
        charSequence.contains(substring)
    } else {
        false
    }
}

fun String.formatReceiverMes(): String {
    return "You: $this"
}
fun String.formatNameSearch() :String {
    return "$this(me)"
}

fun String.formatStringBlock() :String = "Blocked $this"

fun formatLongToStringTimer(minutes: Long, seconds:Long) : String {
    return "%02d:%02d".format(minutes, seconds)
}


