package com.chriniko.mob.info.service.domain

data class MobileInfo(val id: String, val model: String, val modelId: String,
                      val technology: List<MobileTechnology>, val bandsByTech: Map<MobileTechnology, List<MobileBand>>)
