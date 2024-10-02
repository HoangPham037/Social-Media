package com.example.socialmedia.ui.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import com.example.socialmedia.model.MessageModel
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateProvider {
    var dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
    var datetimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
    var timeFormat: DateFormat = SimpleDateFormat("HH:mm")

    //Convert yyyy-MM-dd to dd/MM/yyyy
    fun convertDateSqliteToPerson(date: String): String {
        val units = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return units[2] + "/" + units[1] + "/" + units[0]
    }

    //Convert dd/MM/yyyy to yyyy-MM-dd
    fun convertDatePersonToSqlite(date: String): String {
        val units = date.split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return units[2] + "-" + units[1] + "-" + units[0]
    }

    fun standardization(value: Int, length: Int): String {
        var str = value.toString()
        val lenStr = str.length
        for (i in 0 until length - lenStr) {
            str = "0$str"
        }
        return str
    }

    //Convert yyyy-MM-dd HH:mm to HH:mm dd/MM/yyyy
    fun convertDateTimeSqliteToPerson(fullTime: String): String {
        val times = fullTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return times[1] + " " + convertDateSqliteToPerson(times[0])
    }

    //Convert HH:mm dd/MM/yyyy to yyyy-MM-dd HH:mm
    fun convertDateTimePersonToSqlite(date: String): String {
        val times = date.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        return convertDatePersonToSqlite(times[1]) + " " + times[0]
    }

    fun selectDate(txtDate: EditText) {
        val units = txtDate.text.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val mYear = units[2].toInt()
        val mMonth = units[1].toInt()
        val mDay = units[0].toInt()
        val datePickerDialog = DatePickerDialog(txtDate.context,
            { view, year, monthOfYear, dayOfMonth ->
                txtDate.setText(
                    standardization(
                        dayOfMonth,
                        2
                    ) + "/" + standardization(monthOfYear + 1, 2) + "/" + year
                )
            }, mYear, mMonth - 1, mDay
        )
        datePickerDialog.show()
    }

    fun selectTime(txtTime: EditText) {
        val times = txtTime.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val mHour = times[0].toInt()
        val mMinute = times[1].toInt()
        val timePickerDialog = TimePickerDialog(txtTime.context,
            { view, hourOfDay, minute ->
                txtTime.setText(
                    standardization(
                        hourOfDay,
                        2
                    ) + ":" + standardization(minute, 2)
                )
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    fun selectDateTime(txtDatetime: EditText) {
        val units = txtDatetime.text.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val mYear = units[2].toInt()
        val mMonth = units[1].toInt()
        val mDay = units[0].toInt()
        val datePickerDialog = DatePickerDialog(txtDatetime.context,
            { view, year, monthOfYear, dayOfMonth ->
                val date = standardization(dayOfMonth, 2) + "/" + standardization(
                    monthOfYear + 1,
                    2
                ) + "/" + year
                selectTime(txtDatetime, date)
            }, mYear, mMonth - 1, mDay
        )
        datePickerDialog.show()
    }

    private fun selectTime(txtDatetime: EditText, date: String) {
        val times = txtDatetime.text.toString().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val mHour = times[0].toInt()
        val mMinute = times[1].toInt()
        val timePickerDialog = TimePickerDialog(txtDatetime.context,
            { view, hourOfDay, minute ->
                val time = standardization(hourOfDay, 2) + ":" + standardization(minute, 2)
                txtDatetime.setText("$time $date")
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    fun formatTimestampsString(timestamp: Timestamp): String {
        return try {
            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
            val date = Date(milliseconds)
            val outputFormatter = SimpleDateFormat("HH:mm a", Locale.getDefault())
            outputFormatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    fun formatDate(date: Date) : String{
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            simpleDateFormat.format(date)
        }catch (e: Exception) {
            ""
        }
    }
    fun convertTimestampsToLong(timestamp: Timestamp): Long {
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val date = Date(milliseconds)
        val outputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormatter.format(date)
        return date.time
    }

    fun getChatTime(time: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val timeFormat = SimpleDateFormat("hh:mm a")
        val calendarOne = Calendar.getInstance()
        val calendarTwo = Calendar.getInstance()

        calendarOne.timeInMillis = System.currentTimeMillis()
        calendarTwo.timeInMillis = time.toLong()

        if (calendarOne.get(Calendar.YEAR) != calendarTwo.get(Calendar.YEAR))
            return dateFormat.format(calendarTwo.time)

        if (calendarOne.get(Calendar.MONTH) == calendarTwo.get(Calendar.MONTH))
        {
            val diff =
                calendarOne.get(Calendar.DAY_OF_MONTH) - calendarTwo.get(Calendar.DAY_OF_MONTH)
            if (diff < 7) {
                if (diff == 0) return clearLeadingZeros(timeFormat.format(calendarTwo.time))
                if (diff == 1) return "Yesterday"
                return when (calendarTwo.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SATURDAY -> "Saturday"
                    Calendar.SUNDAY -> "Sunday"
                    Calendar.MONDAY -> "Monday"
                    Calendar.TUESDAY -> "Tuesday"
                    Calendar.WEDNESDAY -> "Wednesday"
                    Calendar.THURSDAY -> "Thursday"
                    else -> "Friday"
                }
            }
        }
        return dateFormat.format(calendarTwo.time)
    }

    private fun clearLeadingZeros(time:String):String{
        return if(time[0]=='0') time.drop(1) else time
    }
    fun checkMessageDate(messages: List<MessageModel>, position: Int): Boolean {
        if (position > 0) {
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar1.timeInMillis = convertTimestampsToLong(messages[position].timestamp!!)
            calendar2.timeInMillis = convertTimestampsToLong(messages[position-1].timestamp!!)
            val diff = calendar1.timeInMillis - calendar2.timeInMillis
            val days = diff / (1000 * 60 * 60 * 24)
            return if (days == 0L) {
                calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH]
            } else false
        }
        return false
    }
}

