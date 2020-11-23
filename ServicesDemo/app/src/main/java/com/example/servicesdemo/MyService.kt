package com.example.servicesdemo

import android.R
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Binder
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
import java.util.logging.Handler


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyService : JobService() {

    companion object {
        private const val TAG = "xlr8_MS"
        private const val MIN = 0
        private const val MAX = 100
    }

    private  var mRandomNumber: Int = 9650 // Store random no
    private var mIsRandomGeneratorOn: Boolean = false // Tells if we are generating no or not


    // Classes can be nested in other classes
    // A nested class marked as inner can access the members of its outer class. Inner classes carry a reference to an object of an outer class:
    inner class MyServiceBinder : Binder() {
        fun getService(): MyService{
         return this@MyService
        }
    }



    override fun onDestroy() {
        Log.d(TAG, " onDestroy Called, current thread- ${Thread.currentThread().name}")
        stopRandomNumberGenerator()
        super.onDestroy()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob called")
        doBackgroundWork()
        return true
    }

    private fun doBackgroundWork() {
        Thread(Runnable {
            mIsRandomGeneratorOn = true
            startRandomNumberGenerator()
        }).start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob Called")
        return true
    }


    private fun startRandomNumberGenerator(){

        while(mIsRandomGeneratorOn){
            try{
                Thread.sleep(1000)
                if(mIsRandomGeneratorOn) {
                    mRandomNumber = Random().nextInt(MAX) + MIN
                    Log.d(TAG, "Thread- ${Thread.currentThread().name}, No- $mRandomNumber")
                    val handler = android.os.Handler(Looper.getMainLooper())
                    handler.post {
                        Toast.makeText(this, "No: $mRandomNumber", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                Log.d(TAG, "Ex- ${e.message}")
            }
        }

    }

    private fun stopRandomNumberGenerator(){
        mIsRandomGeneratorOn = false
    }

    fun getRandomNo() : Int {
        Log.d(TAG, "Returning Random No - $mRandomNumber")
        return mRandomNumber
    }


}

