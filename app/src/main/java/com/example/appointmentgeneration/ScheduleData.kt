package com.example.appointmentgeneration

import android.os.Parcel
import android.os.Parcelable

data class ScheduleData(
    var myLocation: String = "",
    var friendLocations: MutableList<String> = mutableListOf(),
    var desiredDestinations: MutableList<String> = mutableListOf(),
    var scheduleDate: String? = null,
    var scheduleTime: String? = null,
    var budget: Int? = null,
    var moods: MutableList<String> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        myLocation = parcel.readString() ?: "",
        friendLocations = parcel.createStringArrayList() ?: mutableListOf(),
        desiredDestinations = parcel.createStringArrayList() ?: mutableListOf(),
        scheduleDate = parcel.readString(),
        scheduleTime = parcel.readString(),
        budget = parcel.readValue(Int::class.java.classLoader) as? Int,
        moods = parcel.createStringArrayList() ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(myLocation)
        parcel.writeStringList(friendLocations)
        parcel.writeStringList(desiredDestinations)
        parcel.writeString(scheduleDate)
        parcel.writeString(scheduleTime)
        parcel.writeValue(budget)
        parcel.writeStringList(moods)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ScheduleData> {
        override fun createFromParcel(parcel: Parcel) = ScheduleData(parcel)

        override fun newArray(size: Int): Array<ScheduleData?> = arrayOfNulls(size)
    }
}
