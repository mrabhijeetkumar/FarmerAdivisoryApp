package com.example.farmeradvisoryapp.data.repositories

import com.example.farmeradvisoryapp.data.models.MandiPrice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MandiRepository @Inject constructor() {
    fun getLatestPrices(): List<MandiPrice> {
        // In a real app, this would fetch from a Govt API like data.gov.in
        return listOf(
            MandiPrice("Wheat", "Delhi Mandi", "2100", "2400", "2250", "2026-05-02"),
            MandiPrice("Rice", "Delhi Mandi", "3500", "4200", "3800", "2026-05-02"),
            MandiPrice("Tomato", "Azadpur", "1500", "2500", "2000", "2026-05-02"),
            MandiPrice("Onion", "Nashik", "1200", "1800", "1500", "2026-05-02")
        )
    }
}
