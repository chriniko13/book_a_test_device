package com.chriniko.mob.info.service.service

import com.chriniko.mob.info.service.domain.MobileBand
import com.chriniko.mob.info.service.domain.MobileInfo
import com.chriniko.mob.info.service.domain.MobileTechnology
import java.util.*

class MobileInfoRegistry() {


    private val mobilesInfosById: Map<String, MobileInfo>
    private val mobilesInfosByModel: Map<String, MobileInfo>
    private val mobiles: List<MobileInfo>


    init {
        mobiles = getMobiles()
        mobilesInfosById = mobiles.asSequence().map { it.id to it }.toMap()
        mobilesInfosByModel = mobiles.asSequence().map { it.modelId to it }.toMap()
    }


    fun fetchById(id: String): MobileInfo? {
        return mobilesInfosById[id]
    }

    fun fetchByModelId(modelId: String): MobileInfo? {
        return mobilesInfosByModel[modelId]
    }

    fun fetchAll(): List<MobileInfo> {
        return mobiles
    }

    // --- infra ---
    private fun getMobiles() = listOf(
            MobileInfo(
                    "7fe1c6e5-7bda-4297-8a70-b9b5d4a87f09", "Samsung Galaxy S9", "samsung_galaxy_s9",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),

            MobileInfo(
                    "76aa4f02-0956-4bc8-b092-723fe2f4944a", "Samsung Galaxy S8", "samsung_galaxy_s8",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),

            MobileInfo(
                    "7e8fe38d-1b85-4907-8409-13ddab799757", "Samsung Galaxy S7", "samsung_galaxy_s7",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),

            MobileInfo(
                    "0712d2b8-864d-41ba-ab7f-15e049aac03c", "Motorola Nexus 6", "motorola_nexus_6",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),

            MobileInfo(
                    "91b287f4-4d9e-4e6a-8327-31c80c4973fb", "LG Nexus 5X", "lg_nexus_5x",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),


            MobileInfo(
                    "68ed4db2-e030-455f-9073-d84578b8059c", "Huawei Honor 7X", "huawei_honor_7x",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),


            MobileInfo(
                    "a383db47-b1fa-48f5-826d-b755f644d6a6", "Apple iPhone X", "apple_iphone_x",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),


            MobileInfo(
                    "aeca925b-8103-4f14-8aea-ae7c9478b3b3", "Apple iPhone 8", "apple_iphone_8",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),


            MobileInfo(
                    "cbc0e576-6836-4c0f-8215-188b77502e35", "Apple iPhone 4s", "apple_iphone_4s",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            ),

            MobileInfo(
                    "8170aaa5-b29c-4c72-af68-40e51514ac7a", "Nokia 3310", "nokia_3310",
                    listOf(MobileTechnology.`5G`, MobileTechnology.`4G`, MobileTechnology.`3G`, MobileTechnology.`2G`),
                    mapOf(
                            MobileTechnology.`5G` to listOf(MobileBand.n1, MobileBand.n3, MobileBand.n5, MobileBand.n7, MobileBand.n8, MobileBand.n20, MobileBand.n28, MobileBand.n38, MobileBand.n41, MobileBand.n77, MobileBand.n78),
                            MobileTechnology.`4G` to listOf(MobileBand.fddLte1),
                            MobileTechnology.`3G` to listOf(MobileBand.wcdma1),
                            MobileTechnology.`2G` to listOf(MobileBand.gsm850),
                    )
            )


    )


}