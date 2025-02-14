package com.github.sip

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.sip.SipAudioCall
import android.net.sip.SipManager
import android.net.sip.SipProfile
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.sip.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    companion object {
        const val ACTION_INCOMING_CALL = "android.SipDemo.INCOMING_CALL"
    }

    private lateinit var binding: ActivityMainBinding
    private val context = this
    private val sipManager by lazy { SipManager.newInstance(context) }

    private val localProfileUri by lazy { intent.getStringExtra("localProfileUri")!! }

    fun onRinging(call: SipAudioCall, caller: SipProfile) {
        runOnUiThread {
            MaterialAlertDialogBuilder(context)
                .setTitle("Ringing")
                .setMessage(caller.userName)
                .setPositiveButton("answerCall") { _, _ ->
                    call.answerCall(30)
                    call.startAudio()
                    call.setSpeakerMode(true)

                }
                .setNeutralButton("endCall") { _, _ ->
                    call.endCall()
                }
                .create()
                .show()
        }
    }

    fun onReceive(context: Context, intent: Intent) {
        val incomingCall = sipManager?.takeAudioCall(intent, null)
        incomingCall?.setListener(object : SipAudioCall.Listener() {
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

            override fun onReadyToCall(call: SipAudioCall) {
                Log.d(TAG, "onReadyToCall: ${call}")
            }

            override fun onCalling(call: SipAudioCall) {
                Log.d(TAG, "onCalling: ${call}")
            }

            override fun onRinging(call: SipAudioCall, caller: SipProfile) {
                Log.d(TAG, "onRinging: ${call}")
                this@MainActivity.onRinging(call,caller)
            }

            override fun onRingingBack(call: SipAudioCall) {
                Log.d(TAG, "onRingingBack: ${call}")
            }

            override fun onCallBusy(call: SipAudioCall) {
                Log.d(TAG, "onCallBusy: ${call}")
            }

            override fun onCallHeld(call: SipAudioCall) {
                Log.d(TAG, "onCallHeld: ${call}")
            }

            override fun onError(call: SipAudioCall, errorCode: Int, errorMessage: String?) {
                Log.d(TAG, "onError: ${call} ${errorCode} ${errorMessage}")
            }

            override fun onChanged(call: SipAudioCall) {
                Log.d(TAG, "onChanged: ${call}")
            }
        }, true)

    }

    private val callReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("TAG", "onReceive: ${intent.action} ${intent.getStringExtra(SipManager.EXTRA_CALL_ID)}")
            this@MainActivity.onReceive(context, intent)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(callReceiver, IntentFilter(ACTION_INCOMING_CALL))
        binding.call.setOnClickListener {
            val peerProfileUri = binding.sip.text.toString()
            val intent = Intent(context, CallActivity::class.java).apply {
                putExtra("localProfileUri", localProfileUri)
                putExtra("peerProfileUri", peerProfileUri)
            }
            startActivity(intent)
        }
        binding.unregister.setOnClickListener {
            unregister()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callReceiver)
    }


    private fun unregister() {
        try {
            sipManager?.close(localProfileUri)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}