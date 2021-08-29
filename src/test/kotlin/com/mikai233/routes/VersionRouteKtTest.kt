package com.mikai233.routes

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test

class VersionRouteKtTest {

    @Test
    fun testGetV2ServiceConfig() {
        withTestApplication({ versionRoute() }) {
            handleRequest(HttpMethod.Get, "/v2/service/config").apply {

            }
        }
    }
}