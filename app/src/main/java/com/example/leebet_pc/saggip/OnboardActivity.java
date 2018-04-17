package com.example.leebet_pc.saggip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;


public class OnboardActivity extends AppCompatActivity {
    int height;
    int width;
    FloatingActionButton mFab;
    ConstraintLayout mConstraintLayout;
    FrameLayout activityTutorial;

    CoordinatorLayout mCoordLayout;
    int duration = 300;
    Transition sharedElementEnterTransition;
    Transition.TransitionListener mTransitionListener;

    @Override
    public void onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition.removeListener(mTransitionListener);
            setAnim(mConstraintLayout, false);
            setFab(mFab, true);
        } else {

            super.onBackPressed();

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.bg);
        mCoordLayout = (CoordinatorLayout) findViewById(R.id.mybg);
        mConstraintLayout.setVisibility(View.INVISIBLE);
        boolean h = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mConstraintLayout.setVisibility(View.VISIBLE);
                boolean h = new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent passIntent = new Intent(getApplicationContext(), OnboardDetailsActivity.class);
                        startActivityForResult(passIntent, 1);
                        overridePendingTransition(R.anim.fade_from,R.anim.fade_to);
                    }
                }, 250);
            }
        }, 500);

        getWindow().setEnterTransition(null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        mFab = (FloatingActionButton) findViewById(R.id.next_fab);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();


        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {


            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setAnim(mConstraintLayout, true);
                setFab(mFab, false);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        };

        sharedElementEnterTransition.addListener(mTransitionListener);


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setAnim(final ConstraintLayout myView, boolean isShow) {
        // previously invisible view

// get the center for the clipping circle
        int cx = mFab.getWidth() / 2;
        int cy = mFab.getHeight() / 2;

// get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(width, height);

        int[] startingLocation = new int[2];
        mFab.getLocationInWindow(startingLocation);

// create the animator for this view (the start radius is zero)
        Animator anim;
        if (isShow) {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, (int) (mFab.getX() + cx), (int) (mFab.getY() + cy), 0, finalRadius);
            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
        } else {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, (int) (mFab.getX() + cx), (int) (mFab.getY() + cy), finalRadius, 0);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    myView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

        anim.setDuration(duration);
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void setFab(final FloatingActionButton myView, boolean isShow) {

// get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

// get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);
        Animator anim;
        if (isShow) {
// create the animation (the final radius is zero)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, initialRadius);
// make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.VISIBLE);
                    finishAfterTransition();
                }
            });
            anim.setDuration(duration);
        } else {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
// make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    myView.setVisibility(View.INVISIBLE);
                }
            });
        }
// start the animation
        anim.start();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                boolean h = new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 500);
                //finish();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
}
