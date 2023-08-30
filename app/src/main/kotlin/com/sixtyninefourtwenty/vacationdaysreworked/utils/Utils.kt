package com.sixtyninefourtwenty.vacationdaysreworked.utils

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import java.util.Locale

fun AppCompatEditText.isBlank() = text == null || text.toString().isBlank()

fun AppCompatEditText.getInput() = if (isBlank()) "" else text.toString().trim()

fun Fragment.showToast(@StringRes stringResId: Int) {
    Toast.makeText(requireContext(), stringResId, Toast.LENGTH_SHORT).show()
}

fun Fragment.navigate(navDirections: NavDirections) = findNavController().navigate(navDirections)

fun obtainDeviceLocale(): Locale = AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault()
