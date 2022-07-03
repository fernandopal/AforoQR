/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data.model

class ReservationTimeWrapper(var hour: String, var usedCap: Int, var maxCap: Int) {
    override fun toString(): String {
        return "{ hour:$hour, used:$usedCap, max:$maxCap }"
    }
}