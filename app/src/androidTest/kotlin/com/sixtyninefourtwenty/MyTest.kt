package com.sixtyninefourtwenty

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.fragments.MainFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTest {

    @Test
    fun t() {
        launchFragmentInContainer<MainFragment>(themeResId = R.style.Theme_VacationDaysReworked).use { scenario ->

        }
    }

}