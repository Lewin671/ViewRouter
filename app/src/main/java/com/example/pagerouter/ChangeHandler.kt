package com.example.pagerouter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

class ChangeHandler {

    fun getAnimator(
        container: ViewGroup,
        from: View?,
        to: View?,
        isPush: Boolean
    ): Animator {
        val animatorSet = AnimatorSet()
        if (isPush) {
            if (from != null) {
                animatorSet.play(
                    ObjectAnimator.ofFloat(
                        from,
                        View.TRANSLATION_X,
                        -from.width.toFloat()
                    )
                )
            }
            if (to != null) {
                animatorSet.play(
                    ObjectAnimator.ofFloat(
                        to,
                        View.TRANSLATION_X,
                        to.width.toFloat(),
                        0f
                    )
                )
            }
        } else {
            if (from != null) {
                animatorSet.play(
                    ObjectAnimator.ofFloat(
                        from,
                        View.TRANSLATION_X,
                        from.width.toFloat()
                    )
                )
            }
            if (to != null) {
                // Allow this to have a nice transition when coming off an aborted push animation
                val fromLeft = from?.translationX ?: 0F
                animatorSet.play(
                    ObjectAnimator.ofFloat(
                        to,
                        View.TRANSLATION_X,
                        fromLeft - to.width,
                        0f
                    )
                )
            }
        }
        return animatorSet.apply {
            this.duration = 2000
        }
    }

    fun performChange(
        container: ViewGroup,
        from: View?,
        to: View?,
        isPush: Boolean,
        animationListener: AnimationListener? = null
    ) {
        var readyToAnimate = true
        if (to!!.width <= 0 && to.height <= 0) {
            readyToAnimate = false

            to.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                var hasRun = false
                override fun onPreDraw(): Boolean {
                    if (!hasRun) {
                        hasRun = true
                        val observer = to.viewTreeObserver
                        if (observer.isAlive) {
                            observer.removeOnPreDrawListener(this)
                        }
                        performAnimation(container, from, to, isPush, animationListener)
                    }
                    return true
                }
            })
        }

        if (readyToAnimate) {
            performAnimation(container, from, to, isPush, animationListener)
        }
    }

    fun performAnimation(
        container: ViewGroup,
        from: View?,
        to: View?,
        isPush: Boolean,
        animationListener: AnimationListener?
    ) {
        val animator = getAnimator(container, from, to, isPush)

        animator.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    from?.let { resetFromView(it) }
                    if (to != null && to.parent === container) {
                        container.removeView(to)
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (from != null && (!isPush)) {
                        container.removeView(from)
                    }
                    if (isPush && from != null) {
                        resetFromView(from)
                    }
                    animationListener?.onAnimationEnd()
                }
            },
        )
        animator.start()
    }

    fun resetFromView(from: View?) {
        from?.translationX = 0f
    }
}