package com.chenjian.popolivewallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
/**
 * 
 * @author chenjian
 *
 */
public class LiveWallpaper extends WallpaperService
{
	// 实现WallpaperService必须实现的抽象方法
	public Engine onCreateEngine()
	{
		// 返回自定义的Engine
		return new MyEngine();
	}

	class MyEngine extends Engine
	{
		// 记录程序界面是否可见
		private boolean mVisible;
		// 记录当前当前用户动作事件的发生位置
		private float mTouchX = -1;
		private float mTouchY = -1;
		private float oTouchX = -1;
		private float oTouchY = -1;
		// 圆圈列表
		List<Particle> particles = new ArrayList<Particle>();	
		// 画图模式
		private int modle = 1;
		private double distance = -1;
		// 定义画笔
		private Paint mPaintWhite = new Paint();
		private Paint mPaintBlue = new Paint();
		// 定义一个Handler
		Handler mHandler = new Handler();
		// 定义一个周期性执行的任务
		private final Runnable drawTarget = new Runnable()
		{
			public void run()
			{
				// 动态地绘制图形
				drawFrame();
			}
		};
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			
			// 初始化画笔
			mPaintWhite.setColor(0xffffffff);
			mPaintWhite.setAntiAlias(true);
			mPaintWhite.setStrokeWidth(2);
			mPaintWhite.setStrokeCap(Paint.Cap.ROUND);
			mPaintWhite.setStyle(Paint.Style.FILL);
//			mPaintBlue.setColor(0xff3300ff);
			mPaintBlue.setAntiAlias(true);
//			mPaintBlue.setStrokeWidth(2);
			mPaintBlue.setStrokeCap(Paint.Cap.ROUND);
			mPaintBlue.setStyle(Paint.Style.FILL);
			
			// 设置处理触摸事件
			setTouchEventsEnabled(true);			
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			// 删除回调
			mHandler.removeCallbacks(drawTarget);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			// 当界面可见时候，执行drawFrame()方法。
			if (visible)
			{
				// 动态地绘制图形
				drawFrame();
			}
			else
			{
				// 如果界面不可见，删除回调
				mHandler.removeCallbacks(drawTarget);
			}
		}

		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
			float yStep, int xPixels, int yPixels)
		{
			drawFrame();
		}


		public void onTouchEvent(MotionEvent event)
		{
			// 检测触摸点数
			int pointCount = event.getPointerCount();
			if(pointCount == 2)
				modle = 2;
			
			Log.v("pc",pointCount+"");
			// 如果检测到单点滑动操作
			if (modle == 1) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					boolean vertical, horizontal;
					int stepX, stepY;
					if (oTouchX - mTouchX <= 0) {
						horizontal = false;
						stepX = (int) ((mTouchX - oTouchX) / 12);
					} else {
						horizontal = true;
						stepX = (int) ((oTouchX - mTouchX) / 12);
					}
					if (oTouchY - mTouchY <= 0) {
						vertical = false;
						stepY = (int) ((mTouchY - oTouchY) / 12);
					} else {
						vertical = true;
						stepY = (int) ((oTouchY - mTouchY) / 12);
					}
					particles.add(new Particle(40, (int) mTouchX,
							(int) mTouchY, stepX, stepY, horizontal, vertical, LiveWallpaper.this));
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					oTouchX = event.getX();
					oTouchY = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					mTouchX = event.getX();
					mTouchY = event.getY();
				} else {
					mTouchX = -1;
					mTouchY = -1;
				}
			}
			//检测到双指触摸动作
			if(modle == 2){
				if(event.getPointerCount() == 2)
					distance = spacing(event);
				Well well = Well.getInstance();
				if(distance>=well.minRidius && distance<=well.maxRidius)
					well.setRadius((int)distance);
				if(distance<well.minRidius)
					well.radius = well.minRidius;
				if(distance>well.maxRidius)
					well.radius = well.maxRidius;
				if(event.getPointerCount() == 2){
					well.setX((event.getX(0)+event.getX(1))/2);
					well.setY((event.getY(0)+event.getY(1))/2);
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					modle = 1;
					mTouchX = -1;
					mTouchY = -1;
					Well.cleanInstance();
				}
			}
			super.onTouchEvent(event);
		}

		// 定义绘制图形的工具方法
		private void drawFrame()
		{
			// 获取该壁纸的SurfaceHolder
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try
			{
				// 对画布加锁
				c = holder.lockCanvas();
				if (c != null)
				{
					c.save();
					// 绘制背景色
					c.drawColor(0xff333333);
					
					
					// 绘制圆圈
					for(int i=0;i<particles.size();i++){
						if(Well.isInstanceExist()){
							Well w = Well.getInstance();
							double result = Math.sqrt(Math.pow(w.pointX-particles.get(i).xPoint,2)+Math.pow(w.pointY-particles.get(i).yPoint, 2));
							if(result<w.radius){
								particles.remove(i);
							}
							else c.drawCircle(particles.get(i).xPoint, particles.get(i).yPoint, particles.get(i).radius, mPaintWhite);
						}
						else c.drawCircle(particles.get(i).xPoint, particles.get(i).yPoint, particles.get(i).radius, mPaintWhite);
					}
					// 在触碰点绘制圆圈
					drawTouchPoint(c);
					c.restore();
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}
			mHandler.removeCallbacks(drawTarget);
			// 调度下一次重绘
			if (mVisible)
			{

				for(int i=0;i<particles.size();i++){
					particles.get(i).move();
				}
				
				// 指定0.1秒后重新执行mDrawCube一次
				mHandler.postDelayed(drawTarget, 10);
			}
		}

		// 在屏幕触碰点绘制圆圈
		private void drawTouchPoint(Canvas c)
		{
			if (modle==1 && mTouchX>=0 && mTouchY>=0)
			{
				c.drawCircle(mTouchX, mTouchY, 40, mPaintWhite);
				c.drawLine(oTouchX, oTouchY, mTouchX, mTouchY, mPaintWhite);
			}if(modle==2 ){
				Well well = Well.getInstance();
				mPaintBlue.setShader(new RadialGradient(well.pointX,well.pointY,well.radius+1,0xff3300ff,0xff333333,Shader.TileMode.REPEAT));
				c.drawCircle(well.pointX, well.pointY, well.radius, mPaintBlue);
			}
		}
		
		public double spacing(MotionEvent event){
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2))/2;
		}
		
	}
	
}