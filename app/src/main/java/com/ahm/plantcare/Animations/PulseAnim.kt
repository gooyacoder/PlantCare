package com.ahm.plantcare.Animations

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View
import com.airbnb.lottie.LottieAnimationView


class PulseAnim : Animator.AnimatorListener,
    AnimatorUpdateListener {
    private var lottie: LottieAnimationView? = null
    fun show(lottie: LottieAnimationView) {
        this.lottie = lottie
        lottie.addAnimatorListener(this)
        lottie.addAnimatorUpdateListener(this)
        lottie.setVisibility(View.VISIBLE)
        lottie.playAnimation()
    }

    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {
        lottie.setVisibility(View.GONE)
    }

    override fun onAnimationCancel(animator: Animator) {}
    override fun onAnimationRepeat(animator: Animator) {}
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        if (valueAnimator.animatedValue as Float * 100 >= 60) {
            lottie.cancelAnimation()
        }
    }
}
