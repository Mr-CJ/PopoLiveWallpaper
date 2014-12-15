package com.chenjian.popolivewallpaper;

import android.content.Context;

public class Particle {

	public int radius;
	private boolean horizontal;
	private boolean vertical;
	public int xPoint;
	public int yPoint;
	private int xStep;
	private int yStep;
	private int screenWidth;
	private int screenHeight;
	
	public Particle() {

	}
	
	public Particle(int radius, int xPoint, int yPoint, int xStep, int yStep, boolean horizontal, boolean vertical, Context context){
		this.radius = radius;
		this.xPoint = xPoint;
		this.yPoint = yPoint;
		this.xStep = xStep;
		this.yStep = yStep;
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.screenWidth  = context.getWallpaperDesiredMinimumWidth();
		this.screenHeight = context.getWallpaperDesiredMinimumHeight();
	}
	
	public void move(){
		if(horizontal) 
			xPoint += xStep;
		else xPoint -= xStep;
		if(vertical)
			yPoint += yStep;
		else yPoint -= yStep;
		if(xPoint >= 1100-radius)
			horizontal = false;
		if(xPoint <= radius)
			horizontal = true;
		if(yPoint >= 1900-radius)
			vertical = false;
		if(yPoint <= radius)
			vertical = true;
	}

}
