package com.simoesctt.line16.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "returns")
data class ReturnEntity(
    @PrimaryKey val year: Int,
    val status: String,
    val taxpayerJson: String,
    val dependentsJson: String,
    val w2Json: String,
    val scheduleCJson: String,
    val businessMiles: Int,
    val homeOfficeSqFt: Int,
)
