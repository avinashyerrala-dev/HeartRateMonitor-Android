package com.health.heartratemonitor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.health.heartratemonitor.MainActivity
import com.health.heartratemonitor.R
import com.health.heartratemonitor.domain.usecases.device.DisconnectDeviceUseCase
import com.health.heartratemonitor.domain.usecases.activity.FinishActivitySessionUseCase
import com.health.heartratemonitor.domain.usecases.device.ObserveHeartRateUseCase
import com.health.heartratemonitor.domain.usecases.activity.StartActivitySessionUseCase
import com.health.heartratemonitor.domain.usecases.activity.UpdateActivityTrackingUseCase
import com.health.heartratemonitor.domain.usecases.profile.GetUserProfileUseCase
import com.health.heartratemonitor.domain.utils.HeartRateZone
import com.health.heartratemonitor.domain.utils.HeartRateZoneCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActivityTrackingService : Service() {

    @Inject lateinit var observeHeartRateUseCase: ObserveHeartRateUseCase
    @Inject lateinit var disconnectDeviceUseCase: DisconnectDeviceUseCase
    @Inject lateinit var startActivitySessionUseCase: StartActivitySessionUseCase
    @Inject lateinit var finishActivitySessionUseCase: FinishActivitySessionUseCase
    @Inject lateinit var updateActivityTrackingUseCase: UpdateActivityTrackingUseCase
    @Inject lateinit var getProfileUseCase: GetUserProfileUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var heartRateJob: Job? = null
    private var connectedAddress: String? = null // Should be passed in from startIntent!

    companion object {
        const val ACTION_FINISH_ACTIVITY = "com.health.heartratemonitor.FINISH_ACTIVITY"
    }
    private var age: Int = 30

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_FINISH_ACTIVITY) {
            serviceScope.launch {
                finishActivitySessionUseCase.trackingState().firstOrNull()?.let {
                    if (it) finishActivitySessionUseCase()
                }
                stopSelf()
            }
            return START_NOT_STICKY
        }

        connectedAddress = intent?.getStringExtra("address")
        startForeground(
            1,
            createNotification("Starting Activity...", NotificationManager.IMPORTANCE_HIGH, isInitial = true)
        )
        startTracking()
        return START_STICKY
    }

    private fun startTracking() {
        val address = connectedAddress
        if (address == null) {
            stopSelf()
            return
        }
        serviceScope.launch {
            getProfileUseCase().collectLatest { profile ->
                profile?.dob?.let { dob ->
                    age = HeartRateZoneCalculator.calculateAge(dob)
                }
            }
        }

        startActivitySessionUseCase()
        heartRateJob = serviceScope.launch {
            observeHeartRateUseCase(address).collectLatest { hr ->
               val zone = HeartRateZoneCalculator.getHeartRateZone(age, hr)
                updateActivityTrackingUseCase(hr, zone)
                updateNotification(hr)
            }
        }
    }

    private fun updateNotification(heartRate: Int) {
        val notification = createNotification("Heart Rate: $heartRate bpm")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun createNotification(
        contentText: String,
        importance: Int = NotificationManager.IMPORTANCE_LOW,
        isInitial: Boolean = false
    ): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val finishIntent = Intent(this, ActivityTrackingService::class.java).apply {
            action = ACTION_FINISH_ACTIVITY
        }
        val finishPendingIntent = PendingIntent.getService(
            this,
            1,
            finishIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "activity")
            .setContentTitle("Tracking Activity")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(importance)
            .setOnlyAlertOnce(!isInitial)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Finish", finishPendingIntent)
            .setVibrate(if (isInitial) longArrayOf(0, 500, 250, 500) else longArrayOf(0))
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val channel = NotificationChannel(
                "activity",
                "Activity Tracking",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Track heart rate activity"
                enableVibration(true)
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        heartRateJob?.cancel()
        serviceScope.cancel()
//        connectedAddress?.let { address ->
//            disconnectDeviceUseCase()
//        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf() // Stop service if app is swiped from recent apps
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
