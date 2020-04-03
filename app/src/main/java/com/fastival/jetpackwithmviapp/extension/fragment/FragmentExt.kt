package com.fastival.jetpackwithmviapp.extension.fragment

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(@IdRes nav_actionId: Int)
    = findNavController().navigate(nav_actionId)

