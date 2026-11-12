package com.example.texascashfivequickhits.model

import org.junit.Test
import java.io.File

class SmartPickEngineTest {
    @Test
    fun verifyNumber7PivotCount() {
        val file = File("src/main/assets/cashfive.csv")
        var rawCount7 = 0
        var totalRows = 0

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size >= 9) {
                    val n1 = parts[4].trim().toIntOrNull() ?: 0
                    val n2 = parts[5].trim().toIntOrNull() ?: 0
                    val n3 = parts[6].trim().toIntOrNull() ?: 0
                    val n4 = parts[7].trim().toIntOrNull() ?: 0
                    val n5 = parts[8].trim().toIntOrNull() ?: 0
                    totalRows++
                    val nums = listOf(n1, n2, n3, n4, n5)
                    if (nums.contains(7)) {
                        rawCount7++
                    }
                }
            }
        }
        System.out.println("=== RAW CSV PIVOT CHECK: Total Rows=$totalRows, Number 7 Raw Count=$rawCount7 ===")
    }
}
