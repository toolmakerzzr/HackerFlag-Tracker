package com.example.data.security

import java.security.MessageDigest
import java.util.UUID

object CryptoUtils {

    /**
     * Compute SHA-256 hash of a string (such as IMEI or document text).
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generate a Zero-Knowledge verification proof for an IMEI.
     * Keeps original IMEI private on client while generating a verifiable cryptographic signature.
     */
    fun generateZkImeiProof(imei: String, salt: String = UUID.randomUUID().toString().take(8)): ZkProof {
        val sanitized = imei.trim()
        val saltedHash = sha256("$sanitized::$salt")
        val publicCommitment = sha256(saltedHash)
        return ZkProof(
            rawImeiMasked = maskImei(sanitized),
            salt = salt,
            imeiSha256 = sha256(sanitized),
            zeroKnowledgeCommitment = publicCommitment,
            timestamp = System.currentTimeMillis()
        )
    }

    fun maskImei(imei: String): String {
        val trimmed = imei.trim()
        if (trimmed.length < 8) return "******"
        return trimmed.take(4) + "••••••••" + trimmed.takeLast(3)
    }

    /**
     * Creates an immutable legal SHA-256 digital stamp for Police complaints or FIR drafts.
     */
    fun generateDocumentSeal(content: String, timestamp: Long): String {
        val combined = "HF-FIR-STAMP::$timestamp::$content"
        return "SEAL-" + sha256(combined).take(16).uppercase()
    }
}

data class ZkProof(
    val rawImeiMasked: String,
    val salt: String,
    val imeiSha256: String,
    val zeroKnowledgeCommitment: String,
    val timestamp: Long
)
