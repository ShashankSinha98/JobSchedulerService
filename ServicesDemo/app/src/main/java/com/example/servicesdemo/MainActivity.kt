package com.example.servicesdemo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.servicesdemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "xlr8_MA"

    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent


    private lateinit var myService: MyService // instance of our service class
    private var isServiceBound = false // To check if service is bound or not
    private var serviceConnection: ServiceConnection?= null

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        serviceIntent = Intent(this,MyService::class.java)

        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler


        Log.d(TAG," current thread- ${Thread.currentThread().name}")

        binding.startService.setOnClickListener {
            val componentName = ComponentName(this,MyService::class.java)
            val jobInfo = JobInfo.Builder(101,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(15*60*1000)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build()

            if(jobScheduler.schedule(jobInfo)==JobScheduler.RESULT_SUCCESS){
                Log.d(TAG,"Thread- ${Thread.currentThread().name} job scheduled successfully")
            } else {
                Log.d(TAG,"Thread- ${Thread.currentThread().name} job could not be scheduled")
            }
        }

        binding.stopService.setOnClickListener {
            jobScheduler.cancel(101)
        }

        binding.bindService.setOnClickListener {

            if(serviceConnection==null){
                serviceConnection = object : ServiceConnection{
                    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                        // Initialize our service class instance - can be got from iBinder
                        val myServiceBinder = iBinder as MyService.MyServiceBinder
                        myService = myServiceBinder.getService()
                        isServiceBound = true
                    }

                    override fun onServiceDisconnected(name: ComponentName?) { isServiceBound = false }
                }
            }
            bindService(serviceIntent,serviceConnection!!,Context.BIND_AUTO_CREATE)
        }


        binding.unbindService.setOnClickListener {
            if(isServiceBound){
                unbindService(serviceConnection!!)
                isServiceBound = false
            }
        }


        binding.getRandomNo.setOnClickListener {
            if(isServiceBound){
                binding.randomNoTV.text = "Random No: ${myService.getRandomNo().toString()}"
            } else {
                binding.randomNoTV.text = "Service not bound"
            }
        }



    }


}