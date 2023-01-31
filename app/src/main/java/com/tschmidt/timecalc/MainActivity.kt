@file:Suppress("unused")

package com.tschmidt.timecalc

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.tschmidt.timecalc.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isIndustryTime = true
    private var isRealTime = false
    private var isSetMark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
    }

    fun numberAction(view2: View) {
        if(view2 is Button)
        {
            binding.workingsTV.append(view2.text)
        }
    }

    fun switchTime(view2: View) {
        val tmpStr = binding.workingsTV.text.toString()

        if(view2 is Button)
        {
            when (view2.text) {
                ":" -> {
                    isIndustryTime = true
                    isRealTime = false
                    if (isSetMark == false) {
                        binding.workingsTV.text = "$tmpStr:"
                        isSetMark = true
                    }
                    markButtonDisable(binding.btnDoppelPunkt)
                    markButtonEnable(binding.btnKomma)
                }
                "," -> {
                    isIndustryTime = false
                    isRealTime = true
                    if (isSetMark == false) {
                        binding.workingsTV.text = "$tmpStr,"
                        isSetMark = true
                    }

                    markButtonEnable(binding.btnDoppelPunkt)
                    markButtonDisable(binding.btnKomma)
                }
            }
        }
    }

    private fun markButtonEnable(button: Button) {
        button.isEnabled = true
        button.isClickable = true
    }

    private fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.isClickable = false
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingsTV.length()
        if (length > 0)
            binding.workingsTV.text = binding.workingsTV.text.subSequence(0, length - 1)
    }

    fun allClearAction(view: View) {
        binding.workingsTV.text = ""
        binding.resultsTV.text = ""
        markButtonEnable(binding.btnKomma)
        markButtonEnable(binding.btnDoppelPunkt)
        isSetMark = false
    }

    fun equalsAction(view: View) {
        if(binding.workingsTV.text.isNotEmpty()) {
            binding.resultsTV.text = calculateResults()
            binding.workingsTV.text = ""
            markButtonEnable(binding.btnKomma)
            markButtonEnable(binding.btnDoppelPunkt)
            isSetMark = false
        }
    }

    private fun calculateResults(): String {
        var timeToConvert : CharSequence = binding.workingsTV.text
        var result = ""

        // Anzahl von "XXX"-Zeichen aus String ermitteln:
        // result = charFrequenz(timeToConvert as String, ':').toString()

        // Check if the last Char is a Number, else add 0
        val lastChar : Char = timeToConvert.last()

        if(lastChar==':' || lastChar==',') {
            timeToConvert = timeToConvert.toString() + "00"
        }

        if(isIndustryTime) {
            try {
                result = getRoundedIndustryMinutes(timeToConvert.toString()).toString()
            } catch (exception: NumberFormatException) {
                result = "ERROR:WRONGNUMBER"
            }
        } else {
            try {
                getRealTime(timeToConvert.toString())
            } catch (e: java.lang.NumberFormatException) {
                result = "ERROR:WRONGTIME"
            }
        }
        return result
    }

    fun charFrequenz(str: String, searchChar: Char): Int {
        var frequency = 0

        for (i in 0..str.length - 1) {
            if (searchChar == str[i]) {
                ++frequency
            }
        }
        return frequency
    }

    private fun getRoundedIndustryMinutes(timeString: String): Double {
        return (getIndustryMinutes(timeString)*100.0).roundToInt() / 100.0
    }

    private fun getRealTime(timeString: String): String {
        var timeCode = ""
        val tmpMin: Int

        // Uhrzeit checken, ob Komma enthalten. Wenn nicht, ist die Zeit scheinbar falsch angegeben...
        // oder wir rechnen nur die Minuten aus? Also aus 75 werden 45 Minuten
        // erst wenn Komma vorhanden, werden Stunden und Minuten ausgegeben

        if(timeString.contains(",")) {
            // timeString enthält ein Komma, also gilt als Ausgabe: ??:??
            val hours = timeString.split(",")[0]
            val minutes = timeString.split(",")[1]

            tmpMin = ((minutes.toInt()*60)/100)
            if (tmpMin < 10)
                timeCode = "$hours:0$tmpMin"
        } else
        {
            // es ist kein Komma enthalten, also werden nur die Minuten konvertiert
            // Bsp: 75 --> 45
            timeCode = "0:" + ((timeString.toInt() * 60)/100).toString()
        }
        return timeCode
    }

    // Umwandlung normaler Zeit in Industrieminuten
    private fun getIndustryMinutes(timeString: String): Float {
        val timeCode: Float
        var hours = 0.0f
        var minutes = 0.0f

        // Wenn die Zeit mit Doppelpunkt angegeben ist.
        if(timeString.contains(":")) {
            hours = timeString.split(":")[0].toFloat() * 60
            minutes = timeString.split(":")[1].toFloat()
        }

        // Angaben ohne Doppelpunkt: Nur Minuten werden in Zeitminuten umgerechnet
        if(!timeString.contains(":") && !timeString.contains(","))
        {
            hours = 0.0f
            minutes = timeString.toFloat()
        }
        timeCode = (hours + minutes) / 60
        return timeCode
    }

    fun infoButton(view: View) {
        //
    }
}