package com.example.data.ai

import android.content.Context
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CyberDefenseAiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Dual-Model Load Balancing
    private val primaryModel = "gemini-3.5-flash"
    private val secondaryModel = "gemini-2.5-flash"

    suspend fun queryAiDefense(userQuery: String, isActiveTheft: Boolean = false): AiDefenseResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // System Instruction suited for high-stress cybercrime & device theft mitigation
        val systemPrompt = """
            You are the Hackerflag Cyber Defense & Intelligence Engine.
            OPERATIONAL RULES:
            1. If query reports ACTIVE THEFT or panic, SKIP all greetings and list the 3 MOST URGENT actions instantly:
               - Urgent Action 1: Dial *99*4*1# to freeze USSD UPI/Banking or call 1930.
               - Urgent Action 2: Block SIM with carrier hotline to prevent OTP hijacking.
               - Urgent Action 3: Flag IMEI on CEIR (Sanchar Saathi) to blacklist device across national telecom towers.
            2. Language: Authoritative, reassuring, and clear Hinglish / English.
            3. Provide step-by-step instructions with bold keywords.
            4. Keep responses concise, actionable, and formatted with bullet points.
        """.trimIndent()

        // If API key is dummy or empty, provide instant Smart Emergency Offline response immediately
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiDefenseResult(
                response = generateSmartOfflineAdvice(userQuery, isActiveTheft),
                modelUsed = "Smart Emergency Offline Defense Engine",
                isOfflineFallback = true
            )
        }

        // 1. Try Primary Model
        try {
            val response = executeGeminiRequest(primaryModel, apiKey, systemPrompt, userQuery)
            if (response != null && response.isNotBlank()) {
                return@withContext AiDefenseResult(
                    response = response,
                    modelUsed = "Primary AI ($primaryModel)",
                    isOfflineFallback = false
                )
            }
        } catch (e: Exception) {
            // Primary model encountered issue, fall back to secondary
        }

        // 2. Try Secondary Model (Load Balancer Fallback)
        try {
            val response = executeGeminiRequest(secondaryModel, apiKey, systemPrompt, userQuery)
            if (response != null && response.isNotBlank()) {
                return@withContext AiDefenseResult(
                    response = response,
                    modelUsed = "Secondary AI ($secondaryModel Fallback)",
                    isOfflineFallback = false
                )
            }
        } catch (e: Exception) {
            // Secondary also failed or network offline
        }

        // 3. Smart Emergency Offline Mode Fallback
        AiDefenseResult(
            response = generateSmartOfflineAdvice(userQuery, isActiveTheft),
            modelUsed = "Smart Emergency Offline Mode (Auto-Failover)",
            isOfflineFallback = true
        )
    }

    private fun executeGeminiRequest(model: String, apiKey: String, systemPrompt: String, userQuery: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val jsonBody = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", userQuery) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.3)
                put("topP", 0.9)
                put("maxOutputTokens", 1024)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val responseString = response.body?.string() ?: return null
            val root = JSONObject(responseString)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        }
    }

    /**
     * Smart Emergency Offline Mode:
     * High-speed heuristic rule-engine providing immediate, battle-tested cyber mitigation
     * without requiring an active network connection or cloud latency.
     */
    fun generateSmartOfflineAdvice(query: String, isActiveTheft: Boolean): String {
        val q = query.lowercase()

        if (isActiveTheft || q.contains("stolen") || q.contains("theft") || q.contains("chori") || q.contains("lost phone")) {
            return """
                🚨 **EMERGENCY ACTIVE THEFT PROTOCOL (GOLDEN HOUR - 0 to 60 MINS)**

                Ghabraiye mat, step-by-step ye 3 urgent actions abhi execute karein:

                **1. FINANCIAL FREEZE (Next 5 Mins)**
                • Kisi bhi dusre phone se dial karein: **`*99*4*1#`** (Universal USSD UPI / Bank Disable).
                • Call national cyber helpline **1930** (Indian Cyber Crime Helpline) aur fraud freeze alert raise karein.
                • Bank helpline ko call karke netbanking aur UPI blocks initiate karein (SBI: 1800111109, HDFC: 1800164606, ICICI: 18001080).

                **2. SIM & OTP HIJACK SHIELD (Next 15 Mins)**
                • Apne SIM operator ko call karein (Jio: 198, Airtel: 121, Vi: 199) aur **SIM card deactivate/block** karayein taaki chor ko OTP na mile.

                **3. CEIR NATIONAL IMEI BLOCKLIST (Next 30 Mins)**
                • Government portal **ceir.sanchar-saathi.gov.in** par jakar stolen IMEI block karein.
                • Block hone par thief koi doosra SIM card dalkar phone use nahi kar payega.

                **4. POLICE COMPLAINT / ZERO FIR**
                • Nearest police station ya cybercrime.gov.in par formal complaint file karein. App ke **'Legal FIR'** tab se auto-draft copy kar lijiye.
            """.trimIndent()
        }

        if (q.contains("fir") || q.contains("police") || q.contains("complaint") || q.contains("legal")) {
            return """
                📋 **LEGAL POLICE FIR & CYBER COMPLAINT GUIDE**

                • **Zero FIR Rule:** Aap kisi bhi nazdeeki Police Station me Zero FIR darj karwa sakte hain, chahe crime kisi bhi area me hua ho (CrPC Section 154).
                • **Key Documents Needed:**
                  1. Device Purchase Invoice / Bill with IMEI.
                  2. Identification Proof (Aadhaar / Voter ID).
                  3. CEIR acknowledgement number (if generated).
                • **IT Act 2000 Protection:**
                  - Section 66C: Identity theft punishment.
                  - Section 66D: Cheating by personation using computer resource.
                • **Action:** Open 'Legal & FIR' module in Hackerflag Tracker to generate an instant state-compliant FIR affidavit with digital cryptographic seal.
            """.trimIndent()
        }

        if (q.contains("bank") || q.contains("upi") || q.contains("paytm") || q.contains("gpay") || q.contains("phonepe")) {
            return """
                💳 **EMERGENCY FINANCIAL & PAYMENT DEFENSE**

                • **Instant USSD Lock:** Phone dialer par **`*99*4*1#`** dial karke BHIM/UPI linkage freeze karein.
                • **PhonePe:** Call 080-68727374 to immediately block PhonePe account.
                • **Google Pay:** Go to support.google.com/pay to deregister devices and remove bank cards.
                • **Paytm:** Call 0120-3888388 and select "Report Loss of Phone" to freeze payments.
                • **RBI Guidelines:** Agla 3 din ke andar report karne par user liability ZERO hoti hai for unauthorized electronic transactions.
            """.trimIndent()
        }

        if (q.contains("whatsapp") || q.contains("google") || q.contains("apple") || q.contains("account") || q.contains("cloud")) {
            return """
                🔐 **REMOTE ACCOUNT & IDENTITY DEFENSE**

                • **WhatsApp Remote Deactivation:** Send email to **support@whatsapp.com** with subject: `"Lost/Stolen: Please deactivate my account"` and body containing your international number `+91XXXXXXXXXX`.
                • **Google Find My Device:** Visit **myaccount.google.com/device-activity** or **google.com/android/find** -> Tap 'Secure Device' or 'Erase Device'.
                • **Apple iCloud:** Visit **icloud.com/find** -> Mark as Lost with Activation Lock enabled.
                • **Social Media:** Immediately force-logout all active sessions from Instagram, X, Facebook Security Settings.
            """.trimIndent()
        }

        return """
            🛡️ **HACKERFLAG CYBER DEFENSE INTELLIGENCE**

            • **Priority 1:** Financial freeze via *99*4*1# and 1930 Cyber Helpline.
            • **Priority 2:** SIM card blocking via telecom operator.
            • **Priority 3:** Device IMEI blacklisting via CEIR Portal.
            • **Priority 4:** Remote session logout for Google/Apple/WhatsApp.
            • **Priority 5:** File police complaint with device purchase invoice.

            Use the bottom navigation tabs to access automated tools for every stage of mitigation.
        """.trimIndent()
    }
}

data class AiDefenseResult(
    val response: String,
    val modelUsed: String,
    val isOfflineFallback: Boolean
)
