package de.ahahn94.coboli.api.clients

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.ahahn94.coboli.R
import de.ahahn94.coboli.utils.ContextProvider
import de.ahahn94.coboli.utils.Localization
import okhttp3.*
import okio.*

/**
 * Factory class that produces OkHttpClients that show a progress notification when receiving responses
 * and that have a TrustManager that is preloaded with the certificate of the known server.
 */
class DownloadClientFactory {

    companion object {

        /**
         * Get a custom OkHttpClient that shows a progress notification when receiving responses.
         * Uses TrustedCertificatesClientFactory.createPreconfiguredBuilder to be able to connect
         * to the trusted server.
         * Returns an OkHttpClient that shows a progress notification.
         */
        fun create(progressListener: NotifyingProgressListener): OkHttpClient {
            val builder = TrustedCertificatesClientFactory.createPreconfiguredBuilder()

            builder.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val response = chain.proceed(chain.request())
                    return response.newBuilder()
                        .body(
                            ProgressResponseBody(
                                response.body!!,
                                progressListener
                            )
                        ).build()
                }

            })

            return builder.build()
        }

    }

    /**
     * Listener that shows a notification with a progress bar.
     */
    class NotifyingProgressListener(private val notificationID: Int, private val issueName: String) {

        // Constants.
        private val MAX_PROGRESS = 100
        private var INITIAL_PROGRESS = 0

        // Progress at previous update.
        private var previousProgress = 0

        // Setup things for the notification and notificationManager.
        private val applicationContext = ContextProvider.getApplicationContext()
        private val channelID = applicationContext.getString(R.string.app_name)
        private val notificationManager = NotificationManagerCompat.from(applicationContext)
        private val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        private val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(issueName)
            .setContentText(Localization.getLocalizedString(R.string.download_progress))
            .setSmallIcon(R.drawable.ic_coboli_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)

        init {
            // Add channel for Android Oreo and newer.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        channelID,
                        channelID,
                        NotificationManager.IMPORTANCE_LOW
                    )
                notificationManager.createNotificationChannel(channel)
            }

            // Initial notification.
            notificationManager.apply {
                builder.setProgress(MAX_PROGRESS, INITIAL_PROGRESS, false)
                notify(notificationID, builder.build())
            }
        }

        fun updateProgress(percent: Int) {
            // Only update if new value.
            // Notifications have a rate limit.
            // Only issuing necessary updates and limiting to update every 5 percent
            // prevents drop of changes.
            if (percent > previousProgress && percent % 5 == 0) {
                previousProgress = percent
                NotificationManagerCompat.from(applicationContext).apply {
                    if (percent == 100) {
                        // Wait a second to make sure that the last update does not hit the rate limit.
                        Thread.sleep(1000)
                        builder.setContentText(Localization.getLocalizedString(R.string.download_complete))
                            .setProgress(0, 0, false)
                    } else {
                        builder.setProgress(MAX_PROGRESS, percent, false)
                    }
                    notify(notificationID, builder.build())
                }
            }

        }
    }

    /**
     * ResponseBody that uses a NotifyingProgressListener to
     * perform an action while receiving data.
     */
    class ProgressResponseBody(
        val responseBody: ResponseBody,
        val progressListener: NotifyingProgressListener
    ) : ResponseBody() {

        private lateinit var bufferedSource: BufferedSource

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun source(): BufferedSource {
            if (!this::bufferedSource.isInitialized) {
                // bufferedSource is not yet initialized.
                bufferedSource = source(responseBody.source()).buffer()
            }
            return bufferedSource
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var bytesTotal: Long = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    bytesTotal += when (bytesRead) {
                        -1L -> 0
                        else -> bytesRead
                    }
                    val percent: Float = when (bytesRead) {
                        -1L -> 100F
                        else -> ((bytesTotal.toFloat() / responseBody.contentLength().toFloat()) * 100F)
                    }
                    progressListener.updateProgress(percent.toInt())
                    return bytesRead
                }
            }
        }

    }

}