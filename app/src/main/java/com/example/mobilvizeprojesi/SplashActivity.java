package com.example.mobilvizeprojesi;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView imgOne, imgTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        imgOne=(ImageView) findViewById(R.id.imageView);
        imgTwo=(ImageView) findViewById(R.id.imageView2);

        ObjectAnimator alphaOneA = ObjectAnimator.ofFloat(imgOne,"scaleX",-1);
        alphaOneA.setDuration(1500);
        ObjectAnimator alphaTwoA = ObjectAnimator.ofFloat(imgTwo,"scaleX",-1);
        alphaTwoA.setDuration(1500);
        ObjectAnimator alphaThreeA = ObjectAnimator.ofFloat(imgOne,"scaleX",1);
        alphaThreeA.setDuration(1500);
        ObjectAnimator alphaFourA = ObjectAnimator.ofFloat(imgTwo,"scaleX",1);
        alphaFourA.setDuration(1500);

        AnimatorSet anim = new AnimatorSet();

        anim.play(alphaOneA).with(alphaTwoA).before(alphaThreeA).before(alphaFourA);

        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startActivity(new Intent(SplashActivity.this, StartActivity.class));
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}