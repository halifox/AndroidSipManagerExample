package com.github.sip

import android.annotation.SuppressLint
import android.net.sip.SipAudioCall
import android.net.sip.SipManager
import android.net.sip.SipProfile
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.sip.databinding.ActivityCallBinding

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallBinding
    private val context = this
    private val sipManager by lazy { SipManager.newInstance(context) }

    private val localProfileUri by lazy { intent.getStringExtra("localProfileUri")!! }
    private val peerProfileUri by lazy { intent.getStringExtra("peerProfileUri")!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        call()
    }

    private fun call() {
        val call = sipManager?.makeAudioCall(localProfileUri, peerProfileUri, object : SipAudioCall.Listener() {
            private val TAG = "SipAudioCall.Listener"
            override fun onCallEstablished(call: SipAudioCall) {
                Log.d(TAG, "onCallEstablished: ${call}")
                call.apply {
                    startAudio()
                    setSpeakerMode(true)
                    toggleMute()
                }
            }

            override fun onCallEnded(call: SipAudioCall) {
                Log.d(TAG, "onCallEnded: ${call}")
            }

            override fun onReadyToCall(call: SipAudioCall?) {
                Log.d(TAG, "onReadyToCall: ${call}")
            }

            override fun onCalling(call: SipAudioCall?) {
                Log.d(TAG, "onCalling: ${call}")
            }

            override fun onRinging(call: SipAudioCall?, caller: SipProfile?) {
                Log.d(TAG, "onRinging: ${call}")
            }

            override fun onRingingBack(call: SipAudioCall?) {
                Log.d(TAG, "onRingingBack: ${call}")
            }

            override fun onCallBusy(call: SipAudioCall?) {
                Log.d(TAG, "onCallBusy: ${call}")
            }

            override fun onCallHeld(call: SipAudioCall?) {
                Log.d(TAG, "onCallHeld: ${call}")
            }

            override fun onError(call: SipAudioCall?, errorCode: Int, errorMessage: String?) {
                Log.d(TAG, "onError: ${call} ${errorCode} ${errorMessage}")
            }

            override fun onChanged(call: SipAudioCall?) {
                Log.d(TAG, "onChanged: ${call}")
            }
        }, 30)

        Log.d("TAG", "call: ${call}")
    }


}