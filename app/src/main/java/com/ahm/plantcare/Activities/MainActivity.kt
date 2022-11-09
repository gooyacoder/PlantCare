package com.ahm.plantcare.Activities

import android.R
import android.app.job.JobScheduler
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahm.plantcare.DataStructures.PlantList
import com.ahm.plantcare.Dialogs.MiddleBottomSheetDialog
import com.ahm.plantcare.Dialogs.OnBoarding.checkOnboardingDialog
import com.ahm.plantcare.JobSchedulers.NotificationJobService
import com.ahm.plantcare.RecyclerViewAdapter
import com.ahm.plantcare.Utils.CommunicationKeys
import com.jakewharton.threetenabp.AndroidThreeTen


class MainActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var middleDalog: MiddleBottomSheetDialog? = null
    private var plantList: PlantList? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)
        plantList = PlantList.getInstance(this)
        mRecyclerView = findViewById<View>(R.id.my_recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mLayoutManager
        mAdapter = RecyclerViewAdapter(this, plantList)
        mRecyclerView!!.adapter = mAdapter
        val viewGroup = (findViewById<View>(R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        checkOnboardingDialog(this, viewGroup)
        checkNoPlantsMessage()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Check if there's a Job for notification scheduled:
        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        if (jobScheduler.allPendingJobs.isEmpty()
            || jobScheduler.allPendingJobs[0].id != NotificationJobService.NOTIF_JOB_ID
        ) {
            // There isn't -> schedule it
            NotificationJobService.scheduleNextJob(this)
        }
    }

    fun onRowClicked(position: Int) {
        val intent = Intent(this, EditPlantActivity::class.java)
        intent.putExtra(CommunicationKeys.Main_EditPlant_ExtraPlantPosition, position)
        startActivityForResult(intent, CommunicationKeys.Main_EditPlant_RequestCode)
    }

    fun onWateringButtonClicked(position: Int) {
        val newPos = plantList!!.waterPlant(position)
        if (newPos != position) {
            mRecyclerView!!.smoothScrollToPosition(newPos)
            mAdapter!!.notifyItemMoved(
                position,
                newPos
            ) // Indicate possible change of position in list (after sorting)
        }
        mAdapter!!.notifyItemChanged(newPos) // Indicate change in DaysRemaining field
        checkNoPlantsMessage()
    }

    fun onNewPlantButtonClicked(view: View?) {
        val intent = Intent(this, NewPlantActivity::class.java)
        startActivityForResult(intent, CommunicationKeys.Main_NewPlant_RequestCode)
    }

    fun onSettingsButtonClicked(view: View?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, CommunicationKeys.Main_Settings_RequestCode)
    }

    fun onMiddleButtonClicked(view: View?) {
        if (middleDalog == null) {
            middleDalog = MiddleBottomSheetDialog(this)
        }
        middleDalog!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check which request we're responding to
        if (requestCode == CommunicationKeys.Main_NewPlant_RequestCode) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                val pos = data!!.getIntExtra(CommunicationKeys.NewPlant_Main_PlantPos, 0)
                mRecyclerView!!.smoothScrollToPosition(pos)
                mAdapter!!.notifyItemInserted(pos)
            }
        } else if (requestCode == CommunicationKeys.Main_EditPlant_RequestCode) {
            if (resultCode == RESULT_OK) {
                val prevPos =
                    data!!.getIntExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, 0)
                val newPos = data.getIntExtra(CommunicationKeys.EditPlant_Main_PlantNewPosition, 0)
                //mRecyclerView.smoothScrollToPosition(newPos);
                if (newPos != prevPos) {
                    mAdapter!!.notifyItemMoved(prevPos, newPos)
                }
                mAdapter!!.notifyItemChanged(newPos)
            } else if (resultCode == CommunicationKeys.EditPlant_Main_ResultDelete) {
                val prevPos =
                    data!!.getIntExtra(CommunicationKeys.EditPlant_Main_PlantPrevPosition, 0)
                mRecyclerView!!.smoothScrollToPosition(prevPos)
                mAdapter!!.notifyItemRemoved(prevPos)
            }
        } else if (requestCode == CommunicationKeys.Main_Settings_RequestCode) {
            if (resultCode == CommunicationKeys.Settings_Main_ResultDeleteAll) {
                mAdapter!!.notifyDataSetChanged()
            }
        }
        checkNoPlantsMessage()
    }

    private fun checkNoPlantsMessage() {
        // Sometimes the ViewStub is active, sometimes the View is active
        val view =
            if (findViewById<View?>(R.id.no_plants_viewstub) != null) findViewById(R.id.no_plants_viewstub) else findViewById<View>(
                R.id.no_plants_inflated
            )
        if (plantList!!.size <= 0) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}
