package winkhanh.com.insta.models

import android.util.Log
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@ParseClassName("Post")
class Post: ParseObject() {
    companion object{
        const val KEY_DESCRIPTION = "description"
        const val KEY_IMAGE = "image"
        const val KEY_USER = "author"
        const val CREATE_AT = "createdAt"
    }

    fun getDescription(): String? {
        return getString(KEY_DESCRIPTION)
    }

    fun setDescription(description: String?) {
        put(KEY_DESCRIPTION, description!!)
    }

    fun getImage(): ParseFile? {
        return getParseFile(KEY_IMAGE)
    }

    fun setImage(parseFile: ParseFile?) {
        put(KEY_IMAGE, parseFile!!)
    }

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser?) {
        put(KEY_USER, user!!)
    }

    fun getCreateAt(): String?{
        return getString(CREATE_AT)
    }
    public fun getTime():String{
        var xtime = ""
        val twitterFormat = "EEE MMM dd HH:mm:ss ZZZ yyyy"
        val format = SimpleDateFormat(twitterFormat, Locale.US)
        format.isLenient=true
        try{
            val diff = (System.currentTimeMillis() - format.parse(createdAt.toString()).time) / 1000
            if (diff < 5)
                xtime = "Just now"
            else if (diff < 60)
                xtime = String.format(Locale.ENGLISH, "%ds",diff)
            else if (diff < 60 * 60)
                xtime = String.format(Locale.ENGLISH, "%dm", diff / 60)
            else if (diff < 60 * 60 * 24)
                xtime = String.format(Locale.ENGLISH, "%dh", diff / (60 * 60))
            else if (diff < 60 * 60 * 24 * 30)
                xtime = String.format(Locale.ENGLISH, "%dd", diff / (60 * 60 * 24))
            else {
                val now = Calendar.getInstance();
                val then = Calendar.getInstance();

                then.time = format.parse(createdAt.toString())
                if (now.get(Calendar.YEAR) == then.get(Calendar.YEAR)) {
                    xtime = (then.get(Calendar.DAY_OF_MONTH)).toString() + " " + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                } else {
                    xtime = (then.get(Calendar.DAY_OF_MONTH)).toString() + " " + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + (then.get(Calendar.YEAR) - 2000).toString();
                }
            }
        }catch(e: ParseException){
            e.printStackTrace()
        }
        return xtime
    }
}