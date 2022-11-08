package com.ahm.plantcare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.wateria.DataStructures.PlantList
import java.lang.ref.WeakReference


class RecyclerViewAdapter(currentContext: Context, myPlantList: PlantList) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder?>() {
    private val plantList: PlantList
    var mContextWeakReference: WeakReference<Context>

    init {
        plantList = myPlantList
        mContextWeakReference = WeakReference(currentContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = mContextWeakReference.get()
        if (context != null) {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_view_layout, parent, false)
            return ViewHolder(itemView, context)
        }
        return null
    }

    class ViewHolder(v: View, context: Context) :
        RecyclerView.ViewHolder(v) {
        var textViewPlantName: TextView
        var textViewDaysRemaining: TextView
        var textViewStringDays: TextView
        var imageViewIcon: ImageView
        var buttonWatering: AppCompatImageButton

        init {
            textViewPlantName = v.findViewById(R.id.text_name)
            textViewDaysRemaining = v.findViewById(R.id.text_number)
            textViewStringDays = v.findViewById(R.id.text_days)
            imageViewIcon = v.findViewById(R.id.image)
            buttonWatering = v.findViewById(R.id.button)
            v.setOnClickListener { (context as MainActivity).onRowClicked(adapterPosition) }
            buttonWatering.setOnClickListener {
                (context as MainActivity).onWateringButtonClicked(
                    adapterPosition
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val context = mContextWeakReference.get() ?: return
        val currentPlant: Plant = plantList.get(position)
        val plantName: String = currentPlant.getPlantName()
        holder!!.textViewPlantName.text = plantName
        val daysRemaining: Int = currentPlant.getDaysRemaining()
        holder.textViewDaysRemaining.text = Integer.toString(daysRemaining)
        val days = context.resources.getQuantityString(R.plurals.days, daysRemaining)
        holder.textViewStringDays.text = days
        holder.imageViewIcon.setImageDrawable(currentPlant.getIcon())
    }

    override fun getItemCount(): Int {
        return plantList.getSize()
    }
}
