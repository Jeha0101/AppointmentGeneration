package com.example.appointmentgeneration

import android.os.Parcel
import android.os.Parcelable

data class ScheduleData(
    var myLocation: String = "", // 내 위치
    var friendLocations: MutableList<String> = mutableListOf(), // 친구 위치 (최대 3명)
    var desiredDestinations: MutableList<String> = mutableListOf(), // 희망 목적지 (최대 10개)
    var scheduleDate: String? = null, // 약속 날짜
    var scheduleTime: String? = null, // 약속 시간
    var budget: Int? = null, // 예산
    var moods: MutableList<String> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: mutableListOf(),
        parcel.createStringArrayList() ?: mutableListOf(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createStringArrayList() ?: mutableListOf()
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

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleData> {
        override fun createFromParcel(parcel: Parcel): ScheduleData {
            return ScheduleData(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleData?> {
            return arrayOfNulls(size)
        }
    }
}
