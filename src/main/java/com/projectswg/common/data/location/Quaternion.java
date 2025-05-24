/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
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

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import me.joshlarson.jlcommon.utilities.Arguments;
import org.jetbrains.annotations.NotNull;

public class Quaternion implements Encodable, MongoPersistable {

	private final double [] rotationMatrix;
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
		this.rotationMatrix = new double[9];
		updateRotationMatrix();
	}

	public Quaternion(double [][] rotationMatrix) {
		Arguments.validate(rotationMatrix.length >= 3 && rotationMatrix[0].length >= 3, "Matrix must be at least 3x3!");
		this.rotationMatrix = new double[9];
		for (int i = 0; i < 3; i++) {
			System.arraycopy(rotationMatrix[i], 0, this.rotationMatrix, i * 3, 3);
		}

		// Optimization and thread safe
		double [] matrix = this.rotationMatrix;

		// Source: http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
		double tr = matrix[0] + matrix[4] + matrix[8];
		if (tr > 0) {
			double S = Math.sqrt(tr+1.0) * 2; // S=4*w
			w = S / 4;
			x = (matrix[7] - matrix[5]) / S;
			y = (matrix[2] - matrix[6]) / S;
			z = (matrix[3] - matrix[1]) / S;
		} else if ((matrix[0] > matrix[4])&(matrix[0] > matrix[8])) {
			double S = Math.sqrt(1.0 + matrix[0] - matrix[4] - matrix[8]) * 2; // S=4*x
			w = (matrix[7] - matrix[5]) / S;
			x = S / 4;
			y = (matrix[1] + matrix[3]) / S;
			z = (matrix[2] + matrix[6]) / S;
		} else if (matrix[4] > matrix[8]) {
			double S = Math.sqrt(1.0 + matrix[4] - matrix[0] - matrix[8]) * 2; // S=4*qy
			w = (matrix[2] - matrix[6]) / S;
			x = (matrix[1] + matrix[3]) / S;
			y = S / 4;
			z = (matrix[5] + matrix[7]) / S;
		} else {
			double S = Math.sqrt(1.0 + matrix[8] - matrix[0] - matrix[4]) * 2; // S=4*qz
			w = (matrix[3] - matrix[1]) / S;
			x = (matrix[2] + matrix[6]) / S;
			y = (matrix[5] + matrix[7]) / S;
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

	public double getHeading() {
		double heading = Math.toDegrees(Math.atan2(2*y*w - 2*x*z , 1 - 2*y*y - 2*z*z));
		return (heading >= 0) ? heading : heading + 360;
	}

	public void getRotationMatrix(double [][] rotationMatrix) {
		Arguments.validate(rotationMatrix.length >= 3 && rotationMatrix[0].length >= 3, "Matrix must be at least 3x3!");
		for (int i = 0; i < 3; i++) {
			System.arraycopy(this.rotationMatrix, i * 3, rotationMatrix[i], 0, 3);
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
		double w = Math.cos(rad);
		double x = sin * axisX;
		double y = sin * axisY;
		double z = sin * axisZ;
		double nW = w * this.w - x * this.x - y * this.y - z * this.z;
		double nX = w * this.x + x * this.w + y * this.z - z * this.y;
		double nY = w * this.y + y * this.w + z * this.x - x * this.z;
		double nZ = w * this.z + z * this.w + x * this.y - y * this.x;
		this.w = nW;
		this.x = nX;
		this.y = nY;
		this.z = nZ;
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
		double nX = rotationMatrix[0]*p.getX() + rotationMatrix[1]*p.getY() + rotationMatrix[2]*p.getZ();
		double nY = rotationMatrix[3]*p.getX() + rotationMatrix[4]*p.getY() + rotationMatrix[5]*p.getZ();
		double nZ = rotationMatrix[6]*p.getX() + rotationMatrix[7]*p.getY() + rotationMatrix[8]*p.getZ();
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
	public byte @NotNull [] encode() {
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
	public void readMongo(MongoData data) {
		x = data.getDouble("x", 0);
		y = data.getDouble("y", 0);
		z = data.getDouble("z", 0);
		w = data.getDouble("w", 1);
	}

	@Override
	public void saveMongo(MongoData data) {
		data.putDouble("x", x);
		data.putDouble("y", y);
		data.putDouble("z", z);
		data.putDouble("w", w);
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
		rotationMatrix[0] = x2 + w2 - y2 - z2;
		rotationMatrix[1] = 2*y*x - 2*z*w;
		rotationMatrix[2] = 2*y*w + 2*z*x;
	}

	private void updateRotationMatrixY(double x2, double y2, double z2, double w2) {
		rotationMatrix[3] = 2*x*y + 2*w*z;
		rotationMatrix[4] = y2 - z2 + w2 - x2;
		rotationMatrix[5] = 2*z*y - 2*x*w;
	}

	private void updateRotationMatrixZ(double x2, double y2, double z2, double w2) {
		rotationMatrix[6] = 2*x*z - 2*w*y;
		rotationMatrix[7] = 2*y*z + 2*w*x;
		rotationMatrix[8] = z2 + w2 - x2 - y2;
	}
}
