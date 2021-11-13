package com.findme.app.utils

import android.app.Activity
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.findme.app.R

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    activity?.toast(message, duration)

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    tag: String? = null,
    @IdRes fragmentContainer: Int = R.id.fragment_container
) {
    val manager = supportFragmentManager
    manager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)

    manager.beginTransaction()
        .replace(fragmentContainer, fragment, tag)
        .commit()
}
