package com.example.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.interceptor.ThreatDefenseManager
import com.example.service.CyberSentinelService
import java.io.File

class ThreatInterceptorReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                CyberSentinelService.startService(context)
            }
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (downloadId != -1L) {
                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager?.query(query)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val localUriIdx = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                            if (localUriIdx != -1) {
                                val uriString = it.getString(localUriIdx) ?: ""
                                if (uriString.endsWith(".apk", ignoreCase = true) ||
                                    uriString.endsWith(".apkm", ignoreCase = true) ||
                                    uriString.endsWith(".xapk", ignoreCase = true)
                                ) {
                                    val path = uriString.removePrefix("file://")
                                    val file = File(path)
                                    ThreatDefenseManager.inspectAndNeutralizeApk(context, file)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
