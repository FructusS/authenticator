package com.example.itplaneta.data.sources

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType

@Entity(tableName = "accounts")
data class Account (

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "issuer")
    val issuer: String?,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "tokenType")
    val tokenType: OtpType,
    @ColumnInfo(name = "algorithm")
    val algorithm: OtpAlgorithm,
    @ColumnInfo(name = "secret")
    val secret: String,
    @ColumnInfo(name = "digits", defaultValue = "6")
    val digits: Int,
    @ColumnInfo(name = "counter", defaultValue = "0")
    val counter: Long,
    @ColumnInfo(name = "period", defaultValue = "30")
    val period: Int

)