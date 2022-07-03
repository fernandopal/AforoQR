/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.util

import java.time.LocalDateTime

class InterfaceUtils {
    fun dateToString(date: LocalDateTime, includeDate: Boolean, includeTime: Boolean): String {
        val day = "${if (date.dayOfMonth > 9) date.dayOfMonth else "0${date.dayOfMonth}"}"
        val month = "${if (date.monthValue > 9) date.monthValue else "0${date.monthValue}"}"
        val year = "${date.year}"
        val hour = "${if (date.hour > 9) date.hour else "0${date.hour}"}"
        val minute = "${if (date.minute > 9) date.minute else "0${date.minute}"}"

        return "${
            if(includeDate) "$day/$month/$year" else ""
        }${
            if(includeDate && includeTime) " - " else ""
        }${
            if(includeTime) "$hour:$minute" else ""
        }"
    }
}