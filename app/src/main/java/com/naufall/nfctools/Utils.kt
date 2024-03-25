package com.naufall.nfctools

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import android.widget.Toast
import com.naufall.nfctools.nfc.WritableTag

object Utils {
    fun Activity.enableNfcForegroundDispatch(adapter: NfcAdapter?){
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("enableNfcForegroundDispatch", "Error enabling NFC foreground dispatch", ex)
        }
    }

    fun Activity.disableNfcForegroundDispatch(adapter: NfcAdapter?) {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e("disableNfcForegroundDispatch", "Error disabling NFC foreground dispatch", ex)
        }
    }

    fun Activity.getNfcFirstEightFromIntent(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc","Activity.getNfcFirstEightFromIntent $tagId")
        return takeFirstEightNumber(tagId.toString())
    }

    fun Activity.getNfcLastEightFromIntent(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "Activity.getNfcLastEightFromIntent" + tagId.toString())
        return takeLastEightNumber(tagId.toString())
    }

    fun Activity.getFullLengthNfcFromIntent(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "Activity.getFullLengthNfcFromIntent" + tagId.toString())
        return takeFull(tagId.toString())
    }

    fun Activity.getTagTechList(intent: Intent): List<String>{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            Toast.makeText(this, "Unsupported tag tapped", Toast.LENGTH_SHORT).show()
            return emptyList()
        }
        return tag!!.tagTechList
    }

    fun Activity.getSerialNumberFromIntent(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId

        return tagId.toString()
    }

    fun Activity.getNfcHexFromIntentFlipped(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "getNfcHexFromIntentFlipped $tagId")

        val str = tagId.toString()

        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()

        return buffer
    }

    fun Activity.getNfcHexFromIntentNotFlipped(intent: Intent): String{
        var tag: WritableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WritableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "getNfcHexFromIntentNotFlipped $tagId")

        return tagId.toString()
    }


    private fun takeFirstEightNumber(str: String) : String{
        var nfcid = ""
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }

        buffer = buffer.trim()
        Log.d("nfc","takeFirstEightNumber from $str to $buffer")
        buffer = buffer.take(8)
        Log.d("nfc","takeFirstEightNumber takeFirst $buffer")

        nfcid = buffer.toLong(16).toString()

        if (nfcid.length < 10){
            val length = 10 - nfcid.length
            var newId = ""
            for(i in 1..length){
                newId += "0"
            }
            nfcid = newId + nfcid
        }
        Log.d("nfc", "takeFirstEightNumber "+nfcid)
        return  nfcid
    }

    private fun takeLastEightNumber(str: String) : String{
        var nfcid = ""
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()
        Log.d("nfc","takeLastEightNumber from $str to $buffer")
        buffer = buffer.takeLast(8)
        Log.d("nfc","takeLastEightNumber takeLast $buffer")

        nfcid = buffer.toLong(16).toString()

        if (nfcid.length < 10){
            val length = 10 - nfcid.length
            var newId = ""
            for(i in 1..length){
                newId += "0"
            }
            nfcid = newId + nfcid
        }
        Log.d("nfc", "takeLastEightNumber "+nfcid)
        return  nfcid
    }

    private fun takeFull(str: String) : String{
        var nfcid = ""
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()

        nfcid = buffer.toLong(16).toString()

        if (nfcid.length < 10){
            val length = 10 - nfcid.length
            var newId = ""
            for(i in 1..length){
                newId += "0"
            }
            nfcid = newId + nfcid
        }
        Log.d("nfc", "takeFull "+nfcid)
        return  nfcid
    }
}