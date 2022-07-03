/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data.model

data class AppUser(
    var uid: String,
    var name: String,
    var email: String,
    var isEmailVerified: Boolean,
    var admin: Boolean,
    var issuer: String,
    var picture: String,
    var active: Boolean,
    var reputation: Int
)