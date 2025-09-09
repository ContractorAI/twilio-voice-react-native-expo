package com.twiliovoicereactnative

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import com.twilio.voice.ConnectOptions
import java.util.UUID
import com.twiliovoicereactnative.ReactNativeArgumentsSerializer.serializeCall

class ExpoModule : Module() {
    private val log = SDKLog(this.javaClass)

    override fun definition() = ModuleDefinition {
        Name("TwilioVoiceExpo")

        Function("voice_connect") { accessToken: String, params: Map<String, Any>, customParameters: Map<String, Any> ->
            val context = appContext.reactContext ?: return@Function

            val connectOptions = ConnectOptions.Builder(accessToken)
                .params(params)
                .build()
            val uuid = UUID.randomUUID()
            val callListenerProxy = CallListenerProxy(uuid, context)

            val call = VoiceApplicationProxy.getVoiceServiceApi().connect(
                connectOptions,
                callListenerProxy
            )

            val callRecord = CallRecordDatabase.CallRecord(
                uuid,
                call,
                "Callee", // provide a mechanism for determining the name of the callee
                customParameters, // use the custom parameters passed from the caller
                CallRecordDatabase.CallRecord.Direction.OUTGOING,
                "Display Name" // provide a mechanism for determining the notification display name of the callee
            )

            VoiceApplicationProxy.getCallRecordDatabase().add(callRecord)
            
            serializeCall(callRecord)
        }
    }
}