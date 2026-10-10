package com.example.data.model

enum class StolenStatus(val label: String, val colorHex: Long) {
    CLEAN("CLEAN / VERIFIED", 0xFF00FF9D),
    FLAGGED_STOLEN("FLAGGED STOLEN", 0xFFFF2A55),
    CEIR_BLOCKED("CEIR BLOCKED", 0xFFFF9900),
    POLICE_TRACED("TRACED BY CYBER CELL", 0xFF00F0FF),
    RECOVERED("RECOVERED & SECURED", 0xFF00FF9D)
}

data class CellTower(
    val id: String,
    val name: String,
    val lac: String,
    val cid: String,
    val mcc: String = "404",
    val mnc: String = "45",
    val signalDbm: Int,
    val distanceMeters: Int,
    val azimuthDegrees: Float,
    val operator: String,
    val isPrimaryServing: Boolean = false
)

data class TriangulationReport(
    val estimatedLatitude: Double,
    val estimatedLongitude: Double,
    val uncertaintyRadiusMeters: Int,
    val confidenceScorePercent: Int,
    val activeTowersCount: Int,
    val lastPingTimestamp: Long
)

data class RiskFactor(
    val id: String,
    val title: String,
    val impactPoints: Int,
    val description: String,
    val isMitigated: Boolean
)

data class DeviceRiskScore(
    val totalScore: Int, // 0 - 100
    val riskLevel: String, // "CRITICAL", "HIGH", "ELEVATED", "MINIMAL"
    val goldenHourRemainingMinutes: Int,
    val factors: List<RiskFactor>
)

data class BankProtocol(
    val bankName: String,
    val tollFreePhone: String,
    val smsCommand: String,
    val smsNumber: String,
    val ussdQuickCode: String,
    val netBankingLockUrl: String,
    val iconEmoji: String
)

data class UpiProvider(
    val name: String,
    val emergencyPhone: String,
    val deactivationSteps: String,
    val portalUrl: String
)

data class CarrierHotline(
    val operator: String,
    val tollFree: String,
    val alternativeSupport: String,
    val simBlockInstruction: String
)

data class FirDraft(
    val complaintId: String,
    val victimName: String,
    val contactPhone: String,
    val alternateContact: String,
    val email: String,
    val incidentDate: String,
    val incidentTime: String,
    val incidentAddress: String,
    val policeStation: String,
    val deviceBrandModel: String,
    val imei1: String,
    val imei2: String,
    val simOperator: String,
    val purchaseValue: String,
    val narrative: String,
    val suspectDetails: String,
    val digitalSeal: String,
    val timestamp: Long
)

data class PoliceStation(
    val name: String,
    val division: String,
    val address: String,
    val phone: String,
    val distanceKm: Double,
    val cyberCellInCharge: String
)

data class InsuranceClaimData(
    val policyNumber: String,
    val insuranceCompany: String,
    val estimatedValue: String,
    val firAckNumber: String,
    val ceirAckNumber: String,
    val affidavitText: String
)

data class LegalActSummary(
    val section: String,
    val statute: String,
    val title: String,
    val explanation: String,
    val victimRights: String
)

data class RemoteAccountTask(
    val id: String,
    val title: String,
    val platform: String,
    val urgentOrder: Int,
    val isCompleted: Boolean,
    val actionUrl: String,
    val stepByStep: String
)

data class B2bVerificationQuery(
    val queryToken: String,
    val clientName: String,
    val targetImeiOrHash: String,
    val deviceDescription: String,
    val status: StolenStatus,
    val riskRating: Int,
    val blacklistedDate: String?,
    val isSafeToProcure: Boolean
)

data class OwnershipQrData(
    val imeiSha256: String,
    val deviceModel: String,
    val ownerCertificateHash: String,
    val verificationUrl: String,
    val issuedDate: String
)
