/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of PSWGCommon.                                                *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http://www.gnu.org/licenses/>.             *
 ***********************************************************************************/
package com.projectswg.common.data.location;

import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;
import me.joshlarson.jlcommon.utilities.Arguments;

public class Quaternion implements Encodable, Persistable {
	
	private final double [][] rotationMatrix;
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Quaternion(Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}
	
	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		this.rotationMatrix = new double[3][3];
		updateRotationMatrix();
	}
	
	public Quaternion(double [][] matrix) {
		Arguments.validate(matrix.length >= 3 && matrix[0].length >= 3, "Matrix must be at least 3x3!");
		this.rotationMatrix = new double[3][3];
		for (int i = 0; i < 3; i++) {
			System.arraycopy(matrix, 0, this.rotationMatrix, 0, 3);
		}
		
		// Optimization and thread safe
		matrix = rotationMatrix;
		
		// Source: http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
		double tr = matrix[0][0] + matrix[1][1] + matrix[2][2];
		if (tr > 0) {
			double S = Math.sqrt(tr+1.0) * 2; // S=4*w
			w = S / 4;
			x = (matrix[2][1] - matrix[1][2]) / S;
			y = (matrix[0][2] - matrix[2][0]) / S;
			z = (matrix[1][0] - matrix[0][1]) / S;
		} else if ((matrix[0][0] > matrix[1][1])&(matrix[0][0] > matrix[2][2])) {
			double S = Math.sqrt(1.0 + matrix[0][0] - matrix[1][1] - matrix[2][2]) * 2; // S=4*x 
			w = (matrix[2][1] - matrix[1][2]) / S;
			x = S / 4;
			y = (matrix[0][1] + matrix[1][0]) / S;
			z = (matrix[0][2] + matrix[2][0]) / S;
		} else if (matrix[1][1] > matrix[2][2]) {
			double S = Math.sqrt(1.0 + matrix[1][1] - matrix[0][0] - matrix[2][2]) * 2; // S=4*qy
			w = (matrix[0][2] - matrix[2][0]) / S;
			x = (matrix[0][1] + matrix[1][0]) / S;
			y = S / 4;
			z = (matrix[1][2] + matrix[2][1]) / S;
		} else {
			double S = Math.sqrt(1.0 + matrix[2][2] - matrix[0][0] - matrix[1][1]) * 2; // S=4*qz
			w = (matrix[1][0] - matrix[0][1]) / S;
			x = (matrix[0][2] + matrix[2][0]) / S;
			y = (matrix[1][2] + matrix[2][1]) / S;
			z = S / 4;
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getW() {
		return w;
	}
	
	public double getYaw() {
		return Math.toDegrees(2 * Math.acos(w));
	}
	
	public void getRotationMatrix(double [][] rotationMatrix) {
		Arguments.validate(rotationMatrix.length >= 3 && rotationMatrix[0].length >= 3, "Matrix must be at least 3x3!");
		for (int i = 0; i < 3; i++) {
			System.arraycopy(this.rotationMatrix, 0, rotationMatrix, 0, 3);
		}
	}
	
	public void setX(double x) {
		this.x = x;
		updateRotationMatrix();
	}

	public void setY(double y) {
		this.y = y;
		updateRotationMatrix();
	}

	public void setZ(double z) {
		this.z = z;
		updateRotationMatrix();
	}

	public void setW(double w) {
		this.w = w;
		updateRotationMatrix();
	}
	
	public void set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		updateRotationMatrix();
	}
	
	public void set(Quaternion q) {
		set(q.x, q.y, q.z, q.w);
	}
	
	public void setHeading(double degrees) {
		set(0, 0, 0, 1);
		rotateHeading(degrees);
	}
	
	public void rotateHeading(double degrees) {
		rotateDegrees(degrees, 0, 1, 0);
	}
	
	public void rotateDegrees(double degrees, double axisX, double axisY, double axisZ) {
		double rad = Math.toRadians(degrees) / 2;
		double sin = Math.sin(rad);
		w = Math.cos(rad);
		x = sin * axisX;
		y = sin * axisY;
		z = sin * axisZ;
		normalize();
	}
	
	public void rotateByQuaternion(Quaternion q) {
		double nW = w * q.w - x * q.x - y * q.y - z * q.z;
		double nX = w * q.x + x * q.w + y * q.z - z * q.y;
		double nY = w * q.y + y * q.w + z * q.x - x * q.z;
		double nZ = w * q.z + z * q.w + x * q.y - y * q.x;
		set(nX, nY, nZ, nW);
		normalize();
	}
	
	public void rotatePoint(Point3D p) {
		double nX = rotationMatrix[0][0]*p.getX() + rotationMatrix[0][1]*p.getY() + rotationMatrix[0][2]*p.getZ();
		double nY = rotationMatrix[1][0]*p.getX() + rotationMatrix[1][1]*p.getY() + rotationMatrix[1][2]*p.getZ();
		double nZ = rotationMatrix[2][0]*p.getX() + rotationMatrix[2][1]*p.getY() + rotationMatrix[2][2]*p.getZ();
		p.set(nX, nY, nZ);
	}
	
	public void normalize() {
		double mag = Math.sqrt(x * x + y * y + z * z + w * w);
		x /= mag;
		y /= mag;
		z /= mag;
		w /= mag;
		updateRotationMatrix();
	}

	@Override
	public byte[] encode() {
		NetBuffer buf = NetBuffer.allocate(16);
		buf.addFloat((float) x);
		buf.addFloat((float) y);
		buf.addFloat((float) z);
		buf.addFloat((float) w);
		return buf.array();
	}

	@Override
	public void decode(NetBuffer data) {
		x = data.getFloat();
		y = data.getFloat();
		z = data.getFloat();
		w = data.getFloat();
		updateRotationMatrix();
	}
	
	@Override
	public int getLength() {
		return 16;
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addFloat((float) x);
		stream.addFloat((float) y);
		stream.addFloat((float) z);
		stream.addFloat((float) w);
	}
	
	@Override
	public void read(NetBufferStream stream) {
		x = stream.getFloat();
		y = stream.getFloat();
		z = stream.getFloat();
		w = stream.getFloat();
		updateRotationMatrix();
	}

	@Override
	public String toString() {
		return String.format("Quaternion[%.3f, %.3f, %.3f, %.3f]", x, y, z, w);
	}
	
	private void updateRotationMatrix() {
		double x2 = x * x;
		double y2 = y * y;
		double z2 = z * z;
		double w2 = w * w;
		updateRotationMatrixX(x2, y2, z2, w2);
		updateRotationMatrixY(x2, y2, z2, w2);
		updateRotationMatrixZ(x2, y2, z2, w2);
	}
	
	private void updateRotationMatrixX(double x2, double y2, double z2, double w2) {
		rotationMatrix[0][0] = x2 + w2 - y2 - z2;
		rotationMatrix[0][1] = 2*y*x - 2*z*w;
		rotationMatrix[0][2] = 2*y*w + 2*z*x;
	}
	
	private void updateRotationMatrixY(double x2, double y2, double z2, double w2) {
		rotationMatrix[1][0] = 2*x*y + 2*w*z;
		rotationMatrix[1][1] = y2 - z2 + w2 - x2;
		rotationMatrix[1][2] = 2*z*y - 2*x*w;
	}
	
	private void updateRotationMatrixZ(double x2, double y2, double z2, double w2) {
		rotationMatrix[2][0] = 2*x*z - 2*w*y;
		rotationMatrix[2][1] = 2*y*z + 2*w*x;
		rotationMatrix[2][2] = z2 + w2 - x2 - y2;
	}
}
