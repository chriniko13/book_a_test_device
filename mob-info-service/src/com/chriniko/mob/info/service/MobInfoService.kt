package com.chriniko.mob.info.service

import com.chriniko.mob.info.service.error.ErrorMessage
import com.chriniko.mob.info.service.error.NoRecordExistsException
import com.chriniko.mob.info.service.service.MobileInfoRegistry
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MobInfoService {

    private val logger: Logger = LoggerFactory.getLogger(MobInfoService::class.java)

    fun run(port: Int) {
        val mobileInfoRegistry = MobileInfoRegistry()

        embeddedServer(Netty, port, configure = {
            connectionGroupSize = Runtime.getRuntime().availableProcessors()
            workerGroupSize = Runtime.getRuntime().availableProcessors()
            callGroupSize = Runtime.getRuntime().availableProcessors() * 2

        }) {

            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

            install(StatusPages) {
                exception<NoRecordExistsException> { cause ->
                    logger.error("error occurred: ${cause.message}", cause)

                    val errorMsg = ErrorMessage(cause.message ?: "")
                    call.respond(HttpStatusCode.BadRequest, errorMsg)
                }

                exception<IllegalArgumentException> { cause ->
                    logger.error("error occurred: ${cause.message}", cause)

                    val errorMsg = ErrorMessage(cause.message ?: "")
                    call.respond(HttpStatusCode.BadRequest, errorMsg)
                }

                exception<Throwable> { cause ->
                    logger.error("error occurred: ${cause.message}", cause)

                    val errorMsg = ErrorMessage(cause.message ?: "")
                    call.respond(HttpStatusCode.InternalServerError, errorMsg)
                }
            }

            install(ShutDownUrl.ApplicationCallFeature) {
                // The URL that will be intercepted
                shutDownUrl = "/ktor/application/shutdown"
                // A function that will be executed to get the exit code of the process
                exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
            }

            install(Routing) {

                route("phones") {
                    get("/{id}") {
                        val id = (call.parameters["id"]
                                ?: throw IllegalArgumentException("id should be provided")).toString()

                        val result = mobileInfoRegistry.fetchById(id)

                        call.respond(result
                                ?: throw NoRecordExistsException("no mobile phone record exists with the provided id"))
                    }

                    get {

                        val modelId = call.request.queryParameters["modelId"]

                        if (modelId != null) {

                            val result = mobileInfoRegistry.fetchByModelId(modelId)
                            call.respond(result
                                    ?: throw NoRecordExistsException("no mobile phone record exists with the provided modelId"))

                        } else {
                            call.respond(mobileInfoRegistry.fetchAll())
                        }

                    }
                }
            }

        }.start(wait = true)
    }
}