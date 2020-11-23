package com.example.servicesdemo

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class StartUpBootReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d("xlr8", "StartUpBootReceiver BOOT_COMPLETED");

            val jobScheduler = context.getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler

            val componentName = ComponentName(context,MyService::class.java)
            val jobInfo = JobInfo.Builder(101,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(15*60*1000)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build()

            if(jobScheduler.schedule(jobInfo)== JobScheduler.RESULT_SUCCESS){
                Log.d("xlr8","Thread- ${Thread.currentThread().name} job scheduled successfully")
            } else {
                Log.d("xlr8","Thread- ${Thread.currentThread().name} job could not be scheduled")
            }
        }
    }
}