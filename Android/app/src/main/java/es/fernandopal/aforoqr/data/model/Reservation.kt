/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data.model

import java.time.LocalDateTime

class Reservation {
    val id: Long? = null
    val user: AppUser? = null
    val room: Room? = null
    val event: Event? = null
    val startDate: LocalDateTime? = null
    val endDate: LocalDateTime? = null
    val cancelled = false
    val confirmed = false
    val confirmationDate: LocalDateTime? = null
    val cancellationDate: LocalDateTime? = null
    val requestDate: LocalDateTime? = null
}