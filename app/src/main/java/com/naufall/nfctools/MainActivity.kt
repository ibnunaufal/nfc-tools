package com.naufall.nfctools

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.naufall.nfctools.Utils.disableNfcForegroundDispatch
import com.naufall.nfctools.Utils.enableNfcForegroundDispatch
import com.naufall.nfctools.Utils.getNfcFirstEightFromIntent
import com.naufall.nfctools.Utils.getNfcHexFromIntentFlipped
import com.naufall.nfctools.Utils.getNfcHexFromIntentNotFlipped
import com.naufall.nfctools.Utils.getNfcLastEightFromIntent
import com.naufall.nfctools.Utils.getTagTechList
import com.naufall.nfctools.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adapter: NfcAdapter? = null
    private var show = false

    private var nfc = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNfcAdapter()

        binding.btnHowItWorks.setOnClickListener {
            // showing alert dialog
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage(resources.getString(R.string.howitworks))
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Okay"
            ) { dialog, id -> dialog.cancel() }

            val alert11 = builder1.create()
            alert11.show()
        }
    }

    fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch(adapter)
    }
    override fun onPause() {
        disableNfcForegroundDispatch(adapter)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        var serialFlipped = getNfcHexFromIntentFlipped(intent)
        serialFlipped = serialFlipped.chunked(2).joinToString(":")

        var serialNotFlipped = getNfcHexFromIntentNotFlipped(intent)
        serialNotFlipped = serialNotFlipped.chunked(2).joinToString(":")

        val techList = getTagTechList(intent)
        var techListString = ""
        for (tech in techList) {
            techListString += "${tech.split(".").last()},"
        }

        val last8 = getNfcLastEightFromIntent(intent)
        val first8 = getNfcFirstEightFromIntent(intent)

        val fromTki = !techList.contains("android.nfc.tech.IsoDep")

        nfc = if (fromTki) last8 else first8

        if (nfc.isEmpty()) {
            binding.tvWelcome.visibility = android.view.View.VISIBLE
            Toast.makeText(this, "Unsupported tag tapped", Toast.LENGTH_SHORT).show()
            return
        }else{
            binding.tvWelcome.visibility = android.view.View.GONE
        }
        with(binding) {
            tvFromTki.text = if (fromTki) "Status: Kartu TKI ✅" else "Status: Bukan Kartu TKI ❎"
            tvTechList.text = techListString

            tvSerialFlipped.text = serialFlipped
            tvSerialOriginal.text = serialNotFlipped
            tvNfcFirst8.text = first8
            tvNfcLast8.text = last8

            tvFinalResult.text = if (fromTki)
                "Pada percobaan ini jika dari tki maka pakai Last 8, bukan dari tki pakai First 8.\nHasil Akhir NFC id: $last8"
            else
                "Pada percobaan ini jika dari tki maka pakai Last 8, bukan dari tki pakai First 8.\nHasil Akhir NFC id: $first8"
        }

        Log.d("nfc", "===============================================================")
    }
}