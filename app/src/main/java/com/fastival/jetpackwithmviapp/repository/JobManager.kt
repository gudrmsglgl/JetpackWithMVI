package com.fastival.jetpackwithmviapp.repository

import android.util.Log
import kotlinx.coroutines.Job

open class JobManager(
    private val className: String
) {

    private val TAG: String = "AppDebug"

    private val jobs: HashMap<String, Job> = HashMap()

    protected fun addJob(methodName: String, job: Job){
        cancelJob(methodName)  // when duplicated job added then cancel job
        jobs[methodName] = job
    }

    protected fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    private fun getJob(methodName: String): Job? {
        if (jobs.containsKey(methodName)) {
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs(){
        for ((key, job) in jobs) {
            if (job.isActive) {
                Log.e(TAG, "$className : cancelling job in method: $key")
                job.cancel()
            }
        }
    }

}