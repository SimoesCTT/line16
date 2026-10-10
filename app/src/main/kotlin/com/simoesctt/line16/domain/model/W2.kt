package com.simoesctt.line16.domain.model

data class W2(
    val employer: String = "",
    val wages: Long = 0L,
    val federalWithholding: Long = 0L,
)
