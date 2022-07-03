/**
 * Copyright (c) 2022 Fernando Palomo <mail@fernandopal.es>.
 * All rights reserved.
 */
package es.fernandopal.aforoqr.extensions

import android.widget.EditText


fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.error = if (validator(this.text.toString())) null else message
}