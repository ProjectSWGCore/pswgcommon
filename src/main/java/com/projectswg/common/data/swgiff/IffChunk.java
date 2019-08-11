package com.projectswg.common.data.swgiff;

import com.projectswg.common.data.location.Location;
import com.projectswg.common.data.location.Point3D;
import com.projectswg.common.data.location.Quaternion;
import com.projectswg.common.data.math.RawColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IffChunk extends IffNode {
	
	private final String tag;
	
	private ByteBuffer data;
	private StringBuilder str;
	private boolean read;
	
	public IffChunk(String tag) {
		this(tag, new byte[0]);
	}
	
	public IffChunk(String tag, byte [] data) {
		this.tag = tag;
		this.data = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		
		this.str = null;
		this.read = false;
	}
	
	@Override
	public void close() {
		str = null;
		read = true;
	}
	
	@Override
	public String getTag() {
		return tag;
	}
	
	@Override
	public boolean isRead() {
		return read;
	}
	
	public ByteBuffer getData() {
		return data;
	}
	
	public boolean hasRemaining() {
		return data.hasRemaining();
	}
	
	public int remaining() {
		return data.remaining();
	}
	
	public int remaining(int size) {
		return remaining() / size;
	}
	
	public boolean readBoolean() {
		return data.get() != 0;
	}
	
	public byte readByte() {
		return data.get();
	}
	
	public short readShort() {
		return data.getShort();
	}
	
	public int readInt() {
		return data.getInt();
	}
	
	public long readLong() {
		return data.getLong();
	}
	
	public float readFloat() {
		return data.getFloat();
	}
	
	public double readDouble() {
		return data.getDouble();
	}
	
	public String readUnicodeString() {
		int length = readInt();
		assert length >= 0;
		byte [] stringData = new byte[length*2];
		data.get(stringData);
		return new String(stringData, StandardCharsets.UTF_16LE);
	}
	
	public String readString() {
		if (str == null)
			str = new StringBuilder();
		else
			str.setLength(0);
		byte b;
		while ((b = data.get()) != 0) {
			str.append((char) b);
		}
		return str.toString();
	}
	
	public Point3D readVector() {
		return new Point3D(readFloat(), readFloat(), readFloat());
	}
	
	public Quaternion readQuaternion() {
		double w = readFloat();
		double x = readFloat();
		double y = readFloat();
		double z = readFloat();
		return new Quaternion(x, y, z, w);
	}
	
	public RawColor readVectorArgb() {
		double b = readFloat();
		double a = readFloat();
		double r = readFloat();
		double g = readFloat();
		return new RawColor(a, r, g, b);
	}
	
	public Location readTransform() {
		double [][] matrix = new double[3][4];
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 4; x++) {
				matrix[y][x] = readFloat();
			}
		}
		Quaternion q = new Quaternion(matrix);
		return Location.builder()
				.setPosition(matrix[0][3], matrix[1][3], matrix[2][3])
				.setOrientation(q.getX(), q.getY(), q.getZ(), q.getW())
				.build();
	}
	
	public void writeBoolean(boolean b) {
		writeByte((byte) (b ? 1 : 0));
	}
	
	public void writeByte(byte b) {
		ensureCapacity(1);
		data.put(b);
	}
	
	public void writeShort(short s) {
		ensureCapacity(2);
		data.putShort(s);
	}
	
	public void writeInt(int i) {
		ensureCapacity(4);
		data.putInt(i);
	}
	
	public void writeLong(long l) {
		ensureCapacity(8);
		data.putLong(l);
	}
	
	public void writeFloat(float f) {
		ensureCapacity(4);
		data.putFloat(f);
	}
	
	public void writeDouble(double d) {
		ensureCapacity(8);
		data.putDouble(d);
	}
	
	public void writeUnicodeString(String str) {
		byte [] stringData = str.getBytes(StandardCharsets.UTF_16LE);
		ensureCapacity(stringData.length + 4);
		data.putInt(str.length());
		data.put(stringData);
	}
	
	public void writeString(String str) {
		byte [] stringData = str.getBytes(StandardCharsets.UTF_8);
		ensureCapacity(stringData.length + 1);
		data.put(stringData);
		data.put((byte) 0);
	}
	
	public void writeVector(Point3D p) {
		ensureCapacity(12);
		data.putFloat((float) p.getX());
		data.putFloat((float) p.getY());
		data.putFloat((float) p.getZ());
	}
	
	public void writeQuaternion(Quaternion q) {
		ensureCapacity(16);
		data.putFloat((float) q.getW());
		data.putFloat((float) q.getX());
		data.putFloat((float) q.getY());
		data.putFloat((float) q.getZ());
	}
	
	public void writeVectorArgb(RawColor color) {
		ensureCapacity(16);
		data.putFloat((float) color.getA());
		data.putFloat((float) color.getR());
		data.putFloat((float) color.getG());
		data.putFloat((float) color.getB());
	}
	
	public void writeTransform(Location location) {
		double [][] matrix = new double[3][4];
		location.getOrientation().getRotationMatrix(matrix);
		matrix[0][3] = location.getX();
		matrix[1][3] = location.getY();
		matrix[2][3] = location.getZ();
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 4; x++) {
				data.putFloat((float) matrix[y][x]);
			}
		}
	}
	
	public void ensureCapacity(int additionalLength) {
		if (data.remaining() >= additionalLength)
			return;
		int limit = data.limit();
		additionalLength += limit;
		
		if (limit <= 0)
			limit = 32;
		while (limit < additionalLength)
			limit *= 2;
		
		this.data = ByteBuffer.wrap(Arrays.copyOf(data.array(), limit));
	}
	
}
