package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FeatureCatalog
import com.example.data.model.FeatureCategory
import com.example.data.security.CryptoUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Hackerflag Tracker", appName)
    }

    @Test
    fun `verify crypto sha256 and zero-knowledge proof`() {
        val imei = "869204058193847"
        val hash = CryptoUtils.sha256(imei)
        assertNotNull(hash)
        assertEquals(64, hash.length)

        val proof = CryptoUtils.generateZkImeiProof(imei, "test-salt")
        assertTrue(proof.rawImeiMasked.contains("••••••••"))
        assertEquals(hash, proof.imeiSha256)
        assertNotNull(proof.zeroKnowledgeCommitment)
    }

    @Test
    fun `verify feature catalog search and categorization`() {
        val all = FeatureCatalog.search("")
        assertTrue(all.isNotEmpty())
        assertTrue(all.size >= 25)

        val bankResults = FeatureCatalog.search("bank")
        assertTrue(bankResults.any { it.id == "ussd_freeze" })
        assertTrue(bankResults.any { it.id == "multi_bank_freeze" })

        val helplineResults = FeatureCatalog.search("1930")
        assertTrue(helplineResults.any { it.id == "cyber_helpline_1930" })

        val ceirResults = FeatureCatalog.search("ceir")
        assertTrue(ceirResults.any { it.id == "ceir_portal" })

        val legalResults = FeatureCatalog.search("", FeatureCategory.LEGAL)
        assertTrue(legalResults.any { it.id == "police_fir_draft" })

        val emptyResults = FeatureCatalog.search("xyznonexistent123")
        assertTrue(emptyResults.isEmpty())
    }

    @Test
    fun `verify biometric auth manager availability and emergency passcode`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val availability = com.example.data.security.BiometricAuthManager.checkAvailability(context)
        assertNotNull(availability)
        assertNotNull(availability.description)
        assertEquals("9999", com.example.data.security.BiometricAuthManager.DEFAULT_EMERGENCY_PASSCODE)
    }
}
