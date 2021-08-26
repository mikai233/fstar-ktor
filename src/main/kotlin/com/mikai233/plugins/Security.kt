package com.mikai233.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mikai233.service.userService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtVerifier =
        JWT
            .require(Algorithm.HMAC256(jwtSecret))
            .withAudience(jwtAudience)
            .withIssuer(environment.config.property("jwt.issuer").getString())
            .build()
    authentication {
        jwt("ROLE_ADMIN") {
            realm = jwtRealm
            verifier(
                jwtVerifier
            )
            validate { credential ->
                val claims = credential.payload.claims
                val audience = credential.payload.audience
                claims["username"]?.asString()?.let { username ->
                    userService.getUserByName(username).firstOrNull()?.let { user ->
                        user.takeIf {
                            it.roles.contains("ROLE_ADMIN") && it.username == username && audience.contains(jwtAudience)
                        }?.let {
                            return@validate JWTPrincipal(credential.payload)
                        }
                    }
                }
                null
            }
        }
        jwt("ROLE_USER") {
            realm = jwtRealm
            verifier(jwtVerifier)
            validate { credential ->
                val claims = credential.payload.claims
                val audience = credential.payload.audience
                claims["username"]?.asString()?.let { username ->
                    userService.getUserByName(username).firstOrNull()?.let { user ->
                        user.takeIf {
                            it.roles.contains("ROLE_USER") && it.username == username && audience.contains(jwtAudience)
                        }?.let {
                            return@validate JWTPrincipal(credential.payload)
                        }
                    }
                }
                null
            }
        }
    }
}
