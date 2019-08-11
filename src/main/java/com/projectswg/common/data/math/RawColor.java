package com.projectswg.common.data.math;

public class RawColor {
	
	private final double a;
	private final double r;
	private final double g;
	private final double b;
	
	public RawColor(double a, double r, double g, double b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public double getA() {
		return a;
	}
	
	public double getR() {
		return r;
	}
	
	public double getG() {
		return g;
	}
	
	public double getB() {
		return b;
	}
	
	public static RawColor from(double r, double g, double b) {
		return new RawColor(1, r, g, b);
	}
	
	public static RawColor from(double a, double r, double g, double b) {
		return new RawColor(a, r, g, b);
	}
	
}
