package com.sixtyninefourtwenty.vacationdaysreworked.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import java.util.Locale

fun Fragment.navigate(navDirections: NavDirections) = findNavController().navigate(navDirections)

fun obtainDeviceLocale(): Locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
