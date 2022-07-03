/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data.model

import java.time.LocalTime

data class WorkingHours(
    val from: LocalTime,
    val to: LocalTime
)