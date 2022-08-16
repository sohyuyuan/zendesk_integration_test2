package com.sohyuyuan.zendesk.zendesk_integration

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName
import com.zendesk.logger.Logger
import com.zendesk.service.ErrorResponse
import com.zendesk.service.ZendeskCallback
import io.flutter.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject
import zendesk.answerbot.AnswerBot
import zendesk.answerbot.AnswerBotEngine
import zendesk.chat.Chat
import zendesk.chat.ChatConfiguration
import zendesk.chat.ChatEngine
import zendesk.chat.JwtAuthenticator
import zendesk.configurations.Configuration
import zendesk.core.Identity
import zendesk.core.JwtIdentity
import zendesk.core.Zendesk
import zendesk.messaging.Engine
import zendesk.support.CustomField
import zendesk.support.Guide
import zendesk.support.Support
import zendesk.support.SupportEngine
import zendesk.support.guide.HelpCenterActivity
import zendesk.support.request.RequestConfiguration
import java.util.*


//import zendesk.android.Zendesk
//import zendesk.chat.*
//import zendesk.messaging.MessagingActivity
//import zendesk.messaging.android.DefaultMessagingFactory


data class CustomData(
    @SerializedName("country") val country: String,
    @SerializedName("corporate_name") val corporateName: String,
)

class MainActivity : FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            // This method is invoked on the main thread.
                call, result ->
            when (call.method) {
                "getBatteryLevel" -> {
                    val batteryLevel = getBatteryLevel()

                    if (batteryLevel != -1) {
                        result.success(batteryLevel)
                    } else {
                        result.error("UNAVAILABLE", "Battery level not available.", null)
                    }
                }
                "initZendesk" -> initZendesk()
                "showMessagingSdk" -> showMessagingSdk()
                "showChatSdk" -> showChatSdk()
                "resetChatSdkVisitorInfo" -> resetChatSdkVisitorInfo()
                "initZendeskUnifiedSdk" -> initZendeskUnifiedSdk()
                "startMessagingActivity" -> startMessagingActivity()
                else -> result.notImplemented()
            }
        }
    }

    private fun initZendeskUnifiedSdk() {
        val zendeskUrl = "https://sohyuyuan.zendesk.com"
        val appId = "9e304e7d0bdcdecd136f60c88d4dbbd07c7f1a1820f84c4c"
        val clientId = "mobile_sdk_client_893fdae81ed5b44dc0c2"

        Logger.setLoggable(true)

        Zendesk.INSTANCE.init(context, zendeskUrl, appId, clientId)
        Support.INSTANCE.init(Zendesk.INSTANCE)

        val identity: Identity = JwtIdentity("coqug1972t")
        Zendesk.INSTANCE.setIdentity(identity)

        Guide.INSTANCE.init(Zendesk.INSTANCE)
        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Guide.INSTANCE)

        Chat.INSTANCE.init(context, "GKIGL9IFvQMokOw5ns7b2sNjiTSd91Sh", appId)

        Chat.INSTANCE.resetIdentity()

        val userProvider = Zendesk.INSTANCE.provider()!!.userProvider()
        val userFields: MutableMap<String, String> = HashMap()
        userFields["country"] = "Indiaa"
        userFields["corporate"] = "Soh Corp"

        userProvider.setUserFields(
            userFields,
            object : ZendeskCallback<Map<String?, String?>?>() { // handle callbacks
                override fun onSuccess(result: Map<String?, String?>?) {
                    Log.i("UserFields", "Successfully set user fields.")
                }

                override fun onError(error: ErrorResponse?) {
                    Log.i("UserFields", "Failed to set user fields.")
                }
            })

        val uniqueID = UUID.randomUUID().toString()

        val customFieldsJson = JSONObject()
        customFieldsJson.put("country", "Vietnam")
        customFieldsJson.put("corporate_name", "SoH CorP")

        val jwtAuthenticator =
            JwtAuthenticator { jwtCompletion ->
                //Fetch or generate the JWT token at this point
//                val jwt: String =
//                    Jwts.builder()
//                        .claim("name", "Rocket1245")
//                        .claim("email", "jrocket@example22.com")
//                        .claim("jti", uniqueID)
//                        .claim("external_id", "Rocket1245")
//                        .claim("user_fields", customFieldsJson)
//                        .setHeaderParam("typ", "JWT")
//                        .setHeaderParam("alg", "HS256")
//                        .setIssuedAt(Date())
//                        .signWith(
//                            SignatureAlgorithm.HS256,
//                            "06BF552F4D51AAEA379BC6AE1695AD4B64C36A213B60A7923550804C30049353"
//                        )
//                        .compact();
//                Log.v("Chat SDK JWT : - ", jwt);

                //OnSuccess
                jwtCompletion.onTokenLoaded(
                    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIiLCJpYXQiOjE2NjA2MjMxMjksImV4cCI6MTY5MjE1ODY2OCwiYXVkIjoiIiwic3ViIjoiIiwibmFtZSI6ImhvbGx5IiwiZW1haWwiOiJob2xseUBhYmMuY29tIiwianRpIjoiNTE5NzI0MzI2NTkxIiwiZXh0ZXJuYWxfaWQiOiJob2xseTEyMyJ9.gdCUBhJalZlhpvNUoQUCOxPw3-OFHQf1yRKWVIfoxd0"
                )

                //OnError
                jwtCompletion.onError()
            }

        Chat.INSTANCE.setIdentity(jwtAuthenticator);


//        val identity = AnonymousIdentity.Builder()
//            .withNameIdentifier("Customer Test Soh")
//            .withEmailIdentifier("aema6147@gmail.com")
//            .build()
//        Zendesk.INSTANCE.setIdentity(identity)

//        val identity: Identity = JwtIdentity("unique_id")
//        Zendesk.INSTANCE.setIdentity(identity)

//        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Guide.INSTANCE)


//        val visitorInfo = VisitorInfo.builder()
//            .withName("Customer Test Soh")
//            .withEmail("aema6147@gmail.com")
//            .build()
//
//        val chatProvidersConfiguration = ChatProvidersConfiguration.builder()
//            .withVisitorInfo(visitorInfo)
//            .withDepartment("Test Department Name")
//            .build()

//        Chat.INSTANCE.setChatProvidersConfiguration(chatProvidersConfiguration)
    }

    private fun startMessagingActivity() {
        val answerBotEngine: Engine? = AnswerBotEngine.engine()
        val supportEngine: Engine = SupportEngine.engine()
        val chatEngine: Engine? = ChatEngine.engine()

        val chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(false)
            .build()

//
//        val requestActivityConfig: Configuration = RequestActivity.builder()
//            .withRequestSubject("Android ticket")
//            .withTags("android", "mobile")
//            .withCustomFields(
//                listOf(
//                    CustomField(9402354102297, "Vietnam"),
//                    CustomField(9402434341529, "Soh Corp")
//                )
//            )
//            .config()

//        HelpCenterActivity().showRequestList()


        val requestConf: Configuration = RequestConfiguration.Builder()
            .withTicketForm(9532733313177,listOf(CustomField(9543769869465, "Vietname")))
            .config();

        HelpCenterActivity.builder()
            .withEngines(chatEngine, supportEngine, answerBotEngine)
            .show(context, chatConfiguration, requestConf)

//        MessagingActivity.builder()
//            .withEngines(answerBotEngine, supportEngine, chatEngine)
//            .show(context)
    }

    private fun initZendesk() {
//        Zendesk.initialize(
//            context = this,
//            channelKey = "eyJzZXR0aW5nc191cmwiOiJodHRwczovL3NvaHl1eXVhbi56ZW5kZXNrLmNvbS9tb2JpbGVfc2RrX2FwaS9zZXR0aW5ncy8wMUdBMTEwUlhEVFRHTThNOVZTNjlFR0hRQS5qc29uIn0=",
//            successCallback = { zendesk ->
//                Log.i("IntegrationApplication", "Initialization successful")
//            },
//            failureCallback = { error ->
//                // Tracking the cause of exceptions in your crash reporting dashboard will help to triage any unexpected failures in production
//                Log.e("IntegrationApplication", "Initialization failed", error)
//            },
//            messagingFactory = DefaultMessagingFactory()
//        )
    }

    private fun showMessagingSdk() {
//        Zendesk.instance.messaging.showMessaging(context)
    }

    private fun showChatSdk() {
//        Chat.INSTANCE.init(
//            applicationContext,
//            "eyJzZXR0aW5nc191cmwiOiJodHRwczovL3NvaHl1eXVhbi56ZW5kZXNrLmNvbS9tb2JpbGVfc2RrX2FwaS9zZXR0aW5ncy8wMUdBMTEwUlhEVFRHTThNOVZTNjlFR0hRQS5qc29uIn0=",
//            "7be9233eb56dfc28972dbea41f75705b97b117f56ab4c836"
//        );
//
//        val chatConfiguration = ChatConfiguration.builder()
//            .withAgentAvailabilityEnabled(false)
//            .build()
//
//        val visitorInfo = VisitorInfo.builder()
//            .withName("Bob")
//            .withEmail("bob@example.com")
//            .withPhoneNumber("123456") // numeric string
//            .build()
//
//        val chatProvidersConfiguration = ChatProvidersConfiguration.builder()
//            .withVisitorInfo(visitorInfo)
//            .withDepartment("Department Name")
//            .build()
//
//        Chat.INSTANCE.chatProvidersConfiguration = chatProvidersConfiguration
//
////        MessagingActivity.builder()
////            .withEngines(ChatEngine.engine())
////            .show(applicationContext, chatConfiguration);
    }

    private fun resetChatSdkVisitorInfo() {
//        Chat.INSTANCE.resetIdentity();
    }

    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }

        return batteryLevel
    }
}
