package com.projectswg.common.data.swgiff.parsers.terrain.fractals;


public class PerlinNoise {
	
	private static final int PB = 0x100;
	private static final int PBM = 0xff;
	private static final int PN = 0x1000;
	
	private final int[] p;
	private final float[][] g;
	
	private final SWGRandom rand;
	
	public PerlinNoise(SWGRandom r) {
		p = new int[PB + PB + 2];
		g = new float[PB + PB + 2][2];
		
		rand = r;
		init();
	}
	
	public float noise2(float vec0, float vec1) {
		int bx0, bx1, by0, by1, b00, b10, b01, b11;
		float rx0, rx1, ry0, ry1;
		float t, a, b, u, v;
		int ft, it;
		int i, j;
		
		t = vec0 + PN;
		it = (int) t;
		ft = (it - ((t < 0 && t != it) ? 1 : 0));
		bx0 = ft & PBM;
		bx1 = (bx0 + 1) & PBM;
		rx0 = t - ft;
		rx1 = rx0 - 1f;
		
		t = vec1 + PN;
		it = (int) t;
		ft = (it - ((t < 0 && t != it) ? 1 : 0));
		by0 = ft & PBM;
		by1 = (by0 + 1) & PBM;
		ry0 = t - ft;
		ry1 = ry0 - 1f;
		
		i = p[bx0];
		j = p[bx1];
		
		b00 = p[i + by0];
		b10 = p[j + by0];
		b01 = p[i + by1];
		b11 = p[j + by1];
		
		float sx = s_curve(rx0);
		u = rx0 * g[b00][0] + ry0 * g[b00][1];
		v = rx1 * g[b10][0] + ry0 * g[b10][1];
		a = lerp(sx, u, v);
		
		u = rx0 * g[b01][0] + ry1 * g[b01][1];
		v = rx1 * g[b11][0] + ry1 * g[b11][1];
		b = lerp(sx, u, v);
		
		float sy = s_curve(ry0);
		return lerp(sy, a, b);
	}
	
	private void init() {
		for (int i = 0; i < PB; i++) {
			p[i] = i;
			
			rand.next(); // Was previously used to initialize the 1D version
			
			for (int j = 0; j < 2; j++) {
				g[i][j] = ((float) ((rand.next() % (PB + PB)) - PB)) / PB;
			}
			
			normalize2(g[i]);
		}
		
		for (int i = PB - 1; i > 0; i--) {
			int k = p[i];
			int j = rand.next() % PB;
			p[i] = p[j];
			p[j] = k;
		}
		
		for (int i = 0; i < PB + 2; ++i) {
			p[PB + i] = p[i];
			
			System.arraycopy(g[i], 0, g[PB + i], 0, 2);
		}
	}
	
	private static float s_curve(float t) {
		return t * t * (3f - 2f * t);
	}
	
	private static float lerp(float t, float a, float b) {
		return a + t * (b - a);
	}
	
	private static void normalize2(float[] v) {
		float s = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		v[0] /= s;
		v[1] /= s;
	}
	
}
