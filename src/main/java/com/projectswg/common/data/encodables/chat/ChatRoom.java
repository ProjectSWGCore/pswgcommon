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
package com.projectswg.common.data.encodables.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.projectswg.common.encoding.CachedEncode;
import com.projectswg.common.encoding.Encodable;
import com.projectswg.common.network.NetBuffer;
import com.projectswg.common.network.NetBufferStream;
import com.projectswg.common.persistable.Persistable;

public class ChatRoom implements Encodable, Persistable {
	
	private final CachedEncode cache;
	private final Set<ChatAvatar> moderators;
	private final Set<ChatAvatar> invited;
	private final Set<ChatAvatar> banned;
	private final Set<ChatAvatar> members;
	
	private int id;
	private int type;
	private String path;
	private ChatAvatar owner;
	private ChatAvatar creator;
	private String title;
	private boolean moderated; // No one but moderators can talk
	
	public ChatRoom() {
		this.cache = new CachedEncode(this::encodeImpl);
		this.moderators = ConcurrentHashMap.newKeySet();
		this.invited = ConcurrentHashMap.newKeySet();
		this.members = ConcurrentHashMap.newKeySet();
		this.banned = ConcurrentHashMap.newKeySet();
		
		this.id = 0;
		this.type = 0;
		this.path = null;
		this.owner = new ChatAvatar();
		this.creator = new ChatAvatar();
		this.title = null;
		this.moderated = false;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.cache.clearCached();
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.cache.clearCached();
		this.type = type;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.cache.clearCached();
		this.path = path;
	}
	
	public ChatAvatar getOwner() {
		return owner;
	}
	
	public void setOwner(ChatAvatar owner) {
		this.cache.clearCached();
		this.owner = owner;
	}
	
	public ChatAvatar getCreator() {
		return creator;
	}
	
	public void setCreator(ChatAvatar creator) {
		this.cache.clearCached();
		this.creator = creator;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.cache.clearCached();
		this.title = title;
	}
	
	public List<ChatAvatar> getModerators() {
		return new ArrayList<>(moderators);
	}
	
	public List<ChatAvatar> getInvited() {
		return new ArrayList<>(invited);
	}
	
	public boolean isModerated() {
		return moderated;
	}
	
	public void setModerated(boolean moderated) {
		this.cache.clearCached();
		this.moderated = moderated;
	}
	
	public List<ChatAvatar> getMembers() {
		return new ArrayList<>(members);
	}
	
	public boolean isPublic() {
		return type == 0;
	}
	
	public void setIsPublic(boolean isPublic) {
		this.cache.clearCached();
		this.type = (isPublic ? 0 : 1);
	}
	
	public List<ChatAvatar> getBanned() {
		return new ArrayList<>(banned);
	}
	
	public ChatResult canJoinRoom(ChatAvatar avatar, boolean ignoreInvitation) {
		if (banned.contains(avatar))
			return ChatResult.ROOM_AVATAR_BANNED;
		
		if (members.contains(avatar))
			return ChatResult.ROOM_ALREADY_JOINED;
		
		if (isPublic() || ignoreInvitation || invited.contains(avatar) || moderators.contains(avatar))
			return ChatResult.SUCCESS;
		
		return ChatResult.ROOM_AVATAR_NO_PERMISSION;
	}
	
	public ChatResult canSendMessage(ChatAvatar avatar) {
		if (banned.contains(avatar))
			return ChatResult.ROOM_AVATAR_BANNED;
		
		if (moderated && !moderators.contains(avatar))
			return ChatResult.CUSTOM_FAILURE;
		
		return ChatResult.SUCCESS;
	}
	
	public boolean isModerator(ChatAvatar avatar) {
		return avatar.equals(owner) || moderators.contains(avatar);
	}
	
	public boolean isMember(ChatAvatar avatar) {
		return members.contains(avatar);
	}
	
	public boolean isBanned(ChatAvatar avatar) {
		return banned.contains(avatar);
	}
	
	public boolean isInvited(ChatAvatar avatar) {
		return invited.contains(avatar);
	}
	
	public boolean addMember(ChatAvatar avatar) {
		this.cache.clearCached();
		return members.add(avatar);
	}
	
	public boolean removeMember(ChatAvatar avatar) {
		this.cache.clearCached();
		return members.remove(avatar);
	}
	
	public boolean addModerator(ChatAvatar avatar) {
		this.cache.clearCached();
		return moderators.add(avatar);
	}
	
	public boolean removeModerator(ChatAvatar avatar) {
		this.cache.clearCached();
		return moderators.remove(avatar);
	}
	
	public boolean addInvited(ChatAvatar avatar) {
		this.cache.clearCached();
		return invited.add(avatar);
	}
	
	public boolean removeInvited(ChatAvatar avatar) {
		this.cache.clearCached();
		return invited.remove(avatar);
	}
	
	public boolean addBanned(ChatAvatar avatar) {
		this.cache.clearCached();
		return banned.add(avatar);
	}
	
	public boolean removeBanned(ChatAvatar avatar) {
		this.cache.clearCached();
		return banned.remove(avatar);
	}
	
	@Override
	public void decode(NetBuffer data) {
		id = data.getInt();
		type = data.getInt();
		moderated = data.getBoolean();
		path = data.getAscii();
		owner = data.getEncodable(ChatAvatar.class);
		creator = data.getEncodable(ChatAvatar.class);
		title = data.getUnicode();
		moderators.clear();
		moderators.addAll(data.getList(ChatAvatar.class));
		invited.clear();
		invited.addAll(data.getList(ChatAvatar.class));
	}
	
	@Override
	public byte[] encode() {
		return cache.encode();
	}
	
	private byte[] encodeImpl() {
		NetBuffer data = NetBuffer.allocate(getLength());
		data.addInt(id);
		data.addInt(type);
		data.addBoolean(moderated);
		data.addAscii(path);
		data.addEncodable(owner);
		data.addEncodable(creator);
		data.addUnicode(title);
		data.addList(moderators);
		data.addList(invited);
		return data.array();
	}
	
	@Override
	public int getLength() {
		int avatarIdSize = 0; // The encode method for ChatAvatar saves the encode result if the class was modified/null data
		
		for (ChatAvatar moderator : moderators) {
			avatarIdSize += moderator.getLength();
		}
		for (ChatAvatar invitee : invited) {
			avatarIdSize += invitee.getLength();
		}
		
		avatarIdSize += owner.getLength() + creator.getLength();
		return 23 + path.length() + (title.length() * 2) + avatarIdSize;
	}
	
	@Override
	public void save(NetBufferStream stream) {
		stream.addByte(0);
		owner.save(stream);
		creator.save(stream);
		stream.addInt(id);
		stream.addInt(type);
		stream.addAscii(path);
		stream.addUnicode(title);
		stream.addBoolean(moderated);
		stream.addList(moderators, (a) -> a.save(stream));
		stream.addList(invited, (a) -> a.save(stream));
		stream.addList(banned, (a) -> a.save(stream));
	}
	
	@Override
	public void read(NetBufferStream stream) {
		stream.getByte();
		owner.read(stream);
		creator.read(stream);
		id = stream.getInt();
		type = stream.getInt();
		path = stream.getAscii();
		title = stream.getUnicode();
		moderated = stream.getBoolean();
		stream.getList((i) -> moderators.add(inflateAvatar(stream)));
		stream.getList((i) -> invited.add(inflateAvatar(stream)));
		stream.getList((i) -> banned.add(inflateAvatar(stream)));
	}
	
	private ChatAvatar inflateAvatar(NetBufferStream stream) {
		ChatAvatar avatar = new ChatAvatar();
		avatar.read(stream);
		return avatar;
	}
	
	@Override
	public String toString() {
		return "ChatRoom[id=" + id + ", type=" + type + ", path='" + path + "', title='" + title + '\'' + ", creator=" + creator + ", moderated=" + moderated + ", isPublic=" + isPublic() + "]";
	}
	
	public static ChatRoom create(NetBufferStream stream) {
		ChatRoom room = new ChatRoom();
		room.read(stream);
		return room;
	}
}
