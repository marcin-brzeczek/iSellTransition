package com.raywenderlich.isell.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.transition.Transition
import android.transition.TransitionValues
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar

class ProgressBarTransition : Transition() {

    val PROGRESSBAR_PROPERTY = "progress"
    val TRANSITION_PROPERTY = "ProgressBarTransition:progress"

    override fun createAnimator(sceneRoot: ViewGroup?, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if (startValues != null && endValues != null && endValues.view is ProgressBar) {
            val progressBar = endValues.view as ProgressBar
            val startValue = startValues.values[TRANSITION_PROPERTY] as Int
            val endValue = endValues.values[TRANSITION_PROPERTY] as Int
            if (startValue != endValue) {
                val objectAnimator = ObjectAnimator.ofInt(progressBar, PROGRESSBAR_PROPERTY, startValue, endValue)
                objectAnimator.interpolator = DecelerateInterpolator()
                return objectAnimator
            }
        }
        return null
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view is ProgressBar) {
            val progressBar = transitionValues.view as ProgressBar
            transitionValues.values[TRANSITION_PROPERTY] = progressBar.progress
        }
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }
}