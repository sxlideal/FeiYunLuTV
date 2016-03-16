package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;


public class ControllerAnimation {





	/*
	 * 显示下面的控制栏动画	
	 */
		public static void showControllerDownAnimation(View view){
			
			TranslateAnimation ta=new TranslateAnimation
					(android.view.animation.Animation.RELATIVE_TO_SELF
							, 0, android.view.animation.Animation.RELATIVE_TO_SELF, 0, 
							android.view.animation.Animation.RELATIVE_TO_SELF, 2,
							android.view.animation.Animation.RELATIVE_TO_SELF, 0);

//			AlphaAnimation aa=new AlphaAnimation(0, 1);
			ta.setDuration(400);
			ta.setFillAfter(true);
			view.startAnimation(ta);
			
			
		}
		
		/*
		 * 隐藏下面的控制栏动画	
		 */
		public static void hideControllerDownAnimation(View view){
			
			TranslateAnimation ta=new TranslateAnimation
					(android.view.animation.Animation.RELATIVE_TO_PARENT
							, 0, android.view.animation.Animation.RELATIVE_TO_PARENT, 0, 
							android.view.animation.Animation.RELATIVE_TO_PARENT, 0,
							android.view.animation.Animation.RELATIVE_TO_PARENT, 2);
			
//			AlphaAnimation aa=new AlphaAnimation(1, 0);
			ta.setDuration(400);
			ta.setFillAfter(true);
			view.startAnimation(ta);
			
		}
		/*
		 * 显示上面的控制栏动画	
		 */
		public static void showControllerUpAnimation(View view,Animation.AnimationListener animationListener){
			
			TranslateAnimation ta=new TranslateAnimation
					(android.view.animation.Animation.RELATIVE_TO_PARENT, 0,
							android.view.animation.Animation.RELATIVE_TO_PARENT, 0,
							android.view.animation.Animation.RELATIVE_TO_PARENT, -1,
							android.view.animation.Animation.RELATIVE_TO_PARENT, 0);
			
			
//			AlphaAnimation aa=new AlphaAnimation(0, 1);
			ta.setDuration(400);
			ta.setFillAfter(true);
			ta.setAnimationListener(animationListener);
			view.startAnimation(ta);
			

		}
	/*
	 * 隐藏上面的控制栏动画	
	 */
		public static void hideControllerUpAnimation(View view,Animation.AnimationListener animationListener){
			
			TranslateAnimation ta=new TranslateAnimation
					(android.view.animation.Animation.RELATIVE_TO_PARENT
							, 0, android.view.animation.Animation.RELATIVE_TO_PARENT, 0, 
							android.view.animation.Animation.RELATIVE_TO_PARENT, 0,
							android.view.animation.Animation.RELATIVE_TO_PARENT, -1);
			
//			AlphaAnimation aa=new AlphaAnimation(1, 0);
			ta.setDuration(400);
			ta.setFillAfter(true);
			ta.setAnimationListener(animationListener);
			view.startAnimation(ta);
			
		}
		
}
