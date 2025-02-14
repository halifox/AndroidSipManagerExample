package com.github.sip

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.sip.SipManager
import android.net.sip.SipProfile
import android.net.sip.SipRegistrationListener
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.sip.MainActivity.Companion.ACTION_INCOMING_CALL
import com.github.sip.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n", "UnspecifiedImmutableFlag")
class LoginActivity : AppCompatActivity() {
    private val context = this
    private lateinit var binding: ActivityLoginBinding
    private val sipManager by lazy { SipManager.newInstance(context) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.supported.text = """
                    isApiSupported:${SipManager.isApiSupported(context)}
                    isVoipSupported:${SipManager.isVoipSupported(context)}
                    isSipWifiOnly:${SipManager.isSipWifiOnly(context)}
                """.trimIndent()
        binding.register.setOnClickListener {
            register()
        }

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("TAG", "registerForActivityResult:${it} ")
        }.launch(
            arrayOf(
                Manifest.permission.USE_SIP,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
            )
        )
    }

    private fun register() {
        val domain = binding.domain.text.toString()
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        val sipProfile = SipProfile.Builder(username, domain)
//            .setAuthUserName()
//            .setProfileName()
            .setPassword(password)
//            .setPort(5060)
//            .setProtocol("UDP")
//            .setOutboundProxy("")
//            .setDisplayName()
//            .setSendKeepAlive(false)
//            .setAutoRegistration(true)
            .build()
        val intent = Intent(ACTION_INCOMING_CALL)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA)
        sipManager?.open(sipProfile, pendingIntent, object : SipRegistrationListener {
            private val TAG = "SipRegistrationListener"
            override fun onRegistering(localProfileUri: String) {
                Log.d(TAG, "onRegistering:localProfileUri:${localProfileUri} ")
            }

            override fun onRegistrationDone(localProfileUri: String, expiryTime: Long) {
                Log.d(TAG, "onRegistrationDone:localProfileUri:${localProfileUri} expiryTime:${expiryTime}")
                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("localProfileUri", localProfileUri)
                }
                startActivity(intent)
            }

            override fun onRegistrationFailed(localProfileUri: String, errorCode: Int, errorMessage: String) {
                Log.d(TAG, "onRegistrationFailed:localProfileUri: ${localProfileUri} errorCode:${errorCode} errorMessage:${errorMessage}")
                runOnUiThread {
                    MaterialAlertDialogBuilder(context)
                        .setTitle("RegistrationFailed")
                        .setMessage("errorCode:${errorCode}\nerrorMessage:${errorMessage}")
                        .setPositiveButton("ok") { _, _ ->

                        }
                        .show()
                }
            }

        })
    }

}