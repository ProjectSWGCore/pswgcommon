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
package com.projectswg.common.data.encodables.player;

import com.projectswg.common.data.encodables.mongo.MongoData;
import com.projectswg.common.data.encodables.mongo.MongoPersistable;
import com.projectswg.common.data.encodables.oob.OutOfBandPackage;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;

import java.time.Instant;
import java.util.function.Supplier;

public class Mail implements Encodable, Persistable, MongoPersistable {
	
	private int id;
	private byte status;
	private String sender;
	private String subject;
	private String message;
	private long receiverId;
	private OutOfBandPackage outOfBandPackage;
	private Instant timestamp;
	
	public static final byte NEW = 0x4E;
	public static final byte READ = 0x52;
	public static final byte UNREAD = 0x55;
	
	public Mail(MongoData data) {
		this.id = 0;
		this.status = NEW;
		this.sender = "";
		this.subject = "";
		this.message = "";
		this.receiverId = 0;
		this.outOfBandPackage = null;
		this.timestamp = Instant.now();
		readMongo(data);
	}
	
	public Mail(String sender, String subject, String message, long receiverId) {
		this.id = 0;
		this.status = NEW;
		this.sender = sender;
		this.subject = subject;
		this.message = message;
		this.receiverId = receiverId;
		this.outOfBandPackage = null;
		this.timestamp = Instant.now();
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

	public long getReceiverId() {
		return receiverId;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
	
	public OutOfBandPackage getOutOfBandPackage() {
		return outOfBandPackage;
	}

	public void setOutOfBandPackage(OutOfBandPackage outOfBandPackage) {
		this.outOfBandPackage = outOfBandPackage;
	}

	@Override
	public byte[] encode() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addUnicode(message);
		data.addUnicode(subject);
		data.addEncodable(outOfBandPackage);
		return data.array();
	}

	@Override
	public void decode(NetBuffer data) {
		message 			= data.getUnicode();
		subject				= data.getUnicode();
		outOfBandPackage	= data.getEncodable(OutOfBandPackage.class);
	}
	
	@Override
	public int getLength() {
		return  8 + message.length()*2 + subject.length()*2 + outOfBandPackage.getLength();
	}
	
	@Override
	public void read(NetBufferStream stream) {
		stream.getByte();
		status = stream.getByte();
		id = stream.getInt();
		timestamp = Instant.ofEpochSecond(stream.getInt());
		receiverId = stream.getLong();
		sender = stream.getUnicode();
		subject = stream.getUnicode();
		message = stream.getUnicode();
		if (stream.getBoolean()) {
			outOfBandPackage = new OutOfBandPackage();
			outOfBandPackage.read(stream);
		}
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addByte(0);
		stream.addByte(status);
		stream.addInt(id);
		stream.addInt((int) timestamp.getEpochSecond());
		stream.addLong(receiverId);
		stream.addUnicode(sender);
		stream.addUnicode(subject);
		stream.addUnicode(message);
		stream.addBoolean(outOfBandPackage != null);
		if (outOfBandPackage != null)
			outOfBandPackage.save(stream);
	}
	
	@Override
	public final void readMongo(MongoData data) {
		id = data.getInteger("id", id);
		timestamp = data.getDate("timestamp", timestamp);
		receiverId = data.getLong("receiverId", receiverId);
		status = (byte) data.getInteger("status", status);
		sender = data.getString("sender", sender);
		subject = data.getString("subject", subject);
		message = data.getString("message", message);
		outOfBandPackage = data.getDocument("oobPackage", (Supplier<OutOfBandPackage>) OutOfBandPackage::new);
	}
	
	@Override
	public void saveMongo(MongoData data) {
		data.putInteger("id", id);
		data.putDate("timestamp", timestamp);
		data.putLong("receiverId", receiverId);
		data.putInteger("status", status);
		data.putString("sender", sender);
		data.putString("subject", subject);
		data.putString("message", message);
		
		OutOfBandPackage outOfBandPackage = this.outOfBandPackage;
		if (outOfBandPackage != null)
			data.putDocument("oobPackage", outOfBandPackage);
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Mail))
			return false;
		return ((Mail) o).id == id;
	}

	public byte[] encodeHeader() {
		NetBuffer data = NetBuffer.allocate(12 + subject.length() * 2);
		data.addInt(0);
		data.addUnicode(subject);
		data.addInt(0);
		return data.array();
	}
	
	public void decodeHeader(NetBuffer data) {
		data.getInt();
		subject = data.getUnicode();
		data.getInt();
	}
	
	public static Mail create(NetBufferStream stream) {
		Mail m = new Mail("", "", "", 0);
		m.read(stream);
		return m;
	}
	
	public static void saveMail(Mail m, NetBufferStream stream) {
		m.save(stream);
	}
}
