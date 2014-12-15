package com.chenjian.popolivewallpaper;

public class Well {

	public float radius;
	public float pointX;
	public float pointY;
	public final float maxRidius = 200;
	public float minRidius = 40;
	private static Well instance = null;
	
	public Well() {
		// TODO Auto-generated constructor stub
	}
	
	public static Well getInstance(){
		if(instance!=null)
			return instance;
		else return instance=new Well();
	}
	
	public static void cleanInstance(){
		if(instance!=null)
			instance = null;
	}
	
	public static boolean isInstanceExist(){
		if(instance == null)
			return false;
		else return true;
	}
	
	public void setRadius(float radius){
		this.radius = radius;
	}
	
	public void setX(float pointX){
		this.pointX = pointX;
	}
	
	public void setY(float pointY){
		this.pointY = pointY;
	}

}
