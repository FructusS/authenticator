package com.example.itplaneta.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType

@Entity(tableName = "accounts")
data class Account (

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "issuer")
    val issuer: String?,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "tokenType")
    val tokenType: OtpType,
    @ColumnInfo(name = "algorithm")
    val algorithm: OtpDigest,
    @ColumnInfo(name = "secret")
    val secret: String,
    @ColumnInfo(name = "digits", defaultValue = "6")
    val digits: Int,
    @ColumnInfo(name = "counter", defaultValue = "0")
    val counter: Int,
    @ColumnInfo(name = "period", defaultValue = "30")
    val period: Int

)