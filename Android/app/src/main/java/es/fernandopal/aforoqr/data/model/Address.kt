/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.data.model

class Address {
    val id: Long? = null
    val buildingNumber: String? = null
    val street: String? = null
    val city: String? = null
    val country: String? = null

    override fun toString(): String {
        return "$street, $buildingNumber ($city)"
    }
}