/***********************************************************************************
 * Copyright (c) 2024 /// Project SWG /// www.projectswg.com                       *
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
package com.projectswg.common.data.encodables.chat

import com.projectswg.common.encoding.CachedEncode
import com.projectswg.common.encoding.Encodable
import com.projectswg.common.network.NetBuffer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ChatRoom : Encodable {
	private val cache = CachedEncode { this.encodeImpl() }
	private val _moderators = ConcurrentHashMap.newKeySet<ChatAvatar>()
	private val _invited = ConcurrentHashMap.newKeySet<ChatAvatar>()
	private val _banned = ConcurrentHashMap.newKeySet<ChatAvatar>()
	private val _members = ConcurrentHashMap.newKeySet<ChatAvatar>()

	var id: Int = 0
		set(value) {
			cache.clearCached()
			field = value
		}
	var type: Int = 0
		set(value) {
			cache.clearCached()
			field = value
		}
	var path: String = ""
		get() {
			assert(field.isNotEmpty())
			return field
		}
		set(value) {
			cache.clearCached()
			field = value
		}
	var owner: ChatAvatar = ChatAvatar("")
		set(value) {
			cache.clearCached()
			field = value
		}
	var creator: ChatAvatar = ChatAvatar("")
		set(value) {
			cache.clearCached()
			field = value
		}
	var title: String = ""
		set(value) {
			cache.clearCached()
			field = value
		}
	var isModerated: Boolean = false // No one but moderators can talk
		set(value) {
			cache.clearCached()
			field = value
		}
	var isPublic: Boolean
		get() = type == 0
		set(isPublic) {
			cache.clearCached()
			this.type = (if (isPublic) 0 else 1)
		}

	val moderators: List<ChatAvatar>
		get() { return ArrayList(_moderators) }

	val invited: List<ChatAvatar>
		get() { return ArrayList(_invited) }

	val members: List<ChatAvatar>
		get() { return ArrayList(_members) }

	val banned: List<ChatAvatar>
		get() { return ArrayList(_banned) }

	fun canJoinRoom(avatar: ChatAvatar, ignoreInvitation: Boolean): ChatResult {
		if (_banned.contains(avatar)) return ChatResult.ROOM_AVATAR_BANNED

		if (_members.contains(avatar)) return ChatResult.ROOM_ALREADY_JOINED

		if (isPublic || ignoreInvitation || _invited.contains(avatar) || _moderators.contains(avatar)) return ChatResult.SUCCESS

		return ChatResult.ROOM_AVATAR_NO_PERMISSION
	}

	fun canSendMessage(avatar: ChatAvatar): ChatResult {
		if (_banned.contains(avatar)) return ChatResult.ROOM_AVATAR_BANNED

		if (isModerated && !_moderators.contains(avatar)) return ChatResult.CUSTOM_FAILURE

		return ChatResult.SUCCESS
	}

	fun isModerator(avatar: ChatAvatar): Boolean {
		return avatar == owner || _moderators.contains(avatar)
	}

	fun isMember(avatar: ChatAvatar): Boolean {
		return _members.contains(avatar)
	}

	fun isBanned(avatar: ChatAvatar): Boolean {
		return _banned.contains(avatar)
	}

	fun isInvited(avatar: ChatAvatar): Boolean {
		return _invited.contains(avatar)
	}

	fun addMember(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _members.add(avatar)
	}

	fun removeMember(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _members.remove(avatar)
	}

	fun addModerator(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _moderators.add(avatar)
	}

	fun removeModerator(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _moderators.remove(avatar)
	}

	fun addInvited(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _invited.add(avatar)
	}

	fun removeInvited(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _invited.remove(avatar)
	}

	fun addBanned(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _banned.add(avatar)
	}

	fun removeBanned(avatar: ChatAvatar): Boolean {
		cache.clearCached()
		return _banned.remove(avatar)
	}

	override fun decode(data: NetBuffer) {
		id = data.int
		type = data.int
		isModerated = data.boolean
		path = data.ascii
		owner = data.getEncodable(ChatAvatar::class.java)
		creator = data.getEncodable(ChatAvatar::class.java)
		title = data.unicode
		_moderators.clear()
		_moderators.addAll(data.getList(ChatAvatar::class.java))
		_invited.clear()
		_invited.addAll(data.getList(ChatAvatar::class.java))
	}

	override fun encode(): ByteArray {
		return cache.encode()
	}

	private fun encodeImpl(): ByteArray {
		val data = NetBuffer.allocate(length)
		data.addInt(id)
		data.addInt(type)
		data.addBoolean(isModerated)
		data.addAscii(path)
		data.addEncodable(owner)
		data.addEncodable(creator)
		data.addUnicode(title)
		data.addList(_moderators)
		data.addList(_invited)
		return data.array()
	}

	override val length: Int
		get() {
			var avatarIdSize = 0 // The encode method for ChatAvatar saves the encode result if the class was modified/null data

			for (moderator in _moderators) {
				avatarIdSize += moderator.length
			}
			for (invitee in _invited) {
				avatarIdSize += invitee.length
			}

			avatarIdSize += owner.length + creator.length
			return 23 + path.length + (title.length * 2) + avatarIdSize
		}

	override fun toString(): String {
		return "ChatRoom[id=$id, type=$type, path='$path', title='$title', creator=$creator, moderated=$isModerated, isPublic=$isPublic]"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val chatRoom = other as ChatRoom
		return id == chatRoom.id && type == chatRoom.type && isModerated == chatRoom.isModerated && _moderators == chatRoom._moderators && _invited == chatRoom._invited && _banned == chatRoom._banned && path == chatRoom.path && owner == chatRoom.owner && creator == chatRoom.creator && title == chatRoom.title
	}

	override fun hashCode(): Int {
		return Objects.hash(_moderators, _invited, _banned, id, type, path, owner, creator, title, isModerated)
	}

}
