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
package com.projectswg.common.network.packets;

import com.projectswg.common.data.EnumLookup;
import com.projectswg.common.network.packets.swg.holo.login.HoloLoginRequestPacket;
import com.projectswg.common.network.packets.swg.holo.login.HoloLoginResponsePacket;
import com.projectswg.common.network.packets.swg.zone.*;
import me.joshlarson.jlcommon.log.Log;
import com.projectswg.common.network.packets.swg.ErrorMessage;
import com.projectswg.common.network.packets.swg.ServerUnixEpochTime;
import com.projectswg.common.network.packets.swg.admin.AdminShutdownServer;
import com.projectswg.common.network.packets.swg.holo.HoloConnectionStarted;
import com.projectswg.common.network.packets.swg.holo.HoloConnectionStopped;
import com.projectswg.common.network.packets.swg.holo.HoloSetProtocolVersion;
import com.projectswg.common.network.packets.swg.login.AccountFeatureBits;
import com.projectswg.common.network.packets.swg.login.CharacterCreationDisabled;
import com.projectswg.common.network.packets.swg.login.ClientIdMsg;
import com.projectswg.common.network.packets.swg.login.ClientPermissionsMessage;
import com.projectswg.common.network.packets.swg.login.ConnectionServerLagResponse;
import com.projectswg.common.network.packets.swg.login.EnumerateCharacterId;
import com.projectswg.common.network.packets.swg.login.LoginClientId;
import com.projectswg.common.network.packets.swg.login.LoginClientToken;
import com.projectswg.common.network.packets.swg.login.LoginClusterStatus;
import com.projectswg.common.network.packets.swg.login.LoginEnumCluster;
import com.projectswg.common.network.packets.swg.login.LoginIncorrectClientId;
import com.projectswg.common.network.packets.swg.login.OfflineServersMessage;
import com.projectswg.common.network.packets.swg.login.RequestExtendedClusters;
import com.projectswg.common.network.packets.swg.login.ServerId;
import com.projectswg.common.network.packets.swg.login.ServerString;
import com.projectswg.common.network.packets.swg.login.StationIdHasJediSlot;
import com.projectswg.common.network.packets.swg.login.creation.ClientCreateCharacter;
import com.projectswg.common.network.packets.swg.login.creation.ClientVerifyAndLockNameRequest;
import com.projectswg.common.network.packets.swg.login.creation.ClientVerifyAndLockNameResponse;
import com.projectswg.common.network.packets.swg.login.creation.CreateCharacterFailure;
import com.projectswg.common.network.packets.swg.login.creation.CreateCharacterSuccess;
import com.projectswg.common.network.packets.swg.login.creation.DeleteCharacterRequest;
import com.projectswg.common.network.packets.swg.login.creation.DeleteCharacterResponse;
import com.projectswg.common.network.packets.swg.login.creation.RandomNameRequest;
import com.projectswg.common.network.packets.swg.login.creation.RandomNameResponse;
import com.projectswg.common.network.packets.swg.zone.auction.AuctionQueryHeadersMessage;
import com.projectswg.common.network.packets.swg.zone.auction.AuctionQueryHeadersResponseMessage;
import com.projectswg.common.network.packets.swg.zone.auction.CancelLiveAuctionMessage;
import com.projectswg.common.network.packets.swg.zone.auction.CancelLiveAuctionResponseMessage;
import com.projectswg.common.network.packets.swg.zone.auction.CommoditiesItemTypeListRequest;
import com.projectswg.common.network.packets.swg.zone.auction.CommoditiesItemTypeListResponse;
import com.projectswg.common.network.packets.swg.zone.auction.GetAuctionDetails;
import com.projectswg.common.network.packets.swg.zone.auction.GetAuctionDetailsResponse;
import com.projectswg.common.network.packets.swg.zone.auction.IsVendorOwnerMessage;
import com.projectswg.common.network.packets.swg.zone.auction.IsVendorOwnerResponseMessage;
import com.projectswg.common.network.packets.swg.zone.auction.RetrieveAuctionItemMessage;
import com.projectswg.common.network.packets.swg.zone.auction.RetrieveAuctionItemResponseMessage;
import com.projectswg.common.network.packets.swg.zone.baselines.Baseline;
import com.projectswg.common.network.packets.swg.zone.building.UpdateCellPermissionMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatAddModeratorToRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatBanAvatarFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatCreateRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatDeletePersistentMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatDestroyRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatEnterRoomById;
import com.projectswg.common.network.packets.swg.zone.chat.ChatFriendsListUpdate;
import com.projectswg.common.network.packets.swg.zone.chat.ChatIgnoreList;
import com.projectswg.common.network.packets.swg.zone.chat.ChatInstantMessageToCharacter;
import com.projectswg.common.network.packets.swg.zone.chat.ChatInstantMessageToClient;
import com.projectswg.common.network.packets.swg.zone.chat.ChatInviteAvatarToRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatKickAvatarFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnConnectAvatar;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnCreateRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnDestroyRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnEnteredRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnLeaveRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnReceiveRoomInvitation;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnSendInstantMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatOnSendRoomMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatPersistentMessageToClient;
import com.projectswg.common.network.packets.swg.zone.chat.ChatPersistentMessageToServer;
import com.projectswg.common.network.packets.swg.zone.chat.ChatQueryRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatRemoveAvatarFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatRemoveModeratorFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatRequestPersistentMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatRequestRoomList;
import com.projectswg.common.network.packets.swg.zone.chat.ChatRoomMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatSendToRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatSystemMessage;
import com.projectswg.common.network.packets.swg.zone.chat.ChatUnbanAvatarFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ChatUninviteFromRoom;
import com.projectswg.common.network.packets.swg.zone.chat.ConGenericMessage;
import com.projectswg.common.network.packets.swg.zone.chat.VoiceChatStatus;
import com.projectswg.common.network.packets.swg.zone.combat.GrantCommandMessage;
import com.projectswg.common.network.packets.swg.zone.deltas.DeltasMessage;
import com.projectswg.common.network.packets.swg.zone.insertion.ChatRoomList;
import com.projectswg.common.network.packets.swg.zone.insertion.ChatServerStatus;
import com.projectswg.common.network.packets.swg.zone.insertion.CmdStartScene;
import com.projectswg.common.network.packets.swg.zone.insertion.ConnectPlayerMessage;
import com.projectswg.common.network.packets.swg.zone.insertion.SelectCharacter;
import com.projectswg.common.network.packets.swg.zone.object_controller.ChangeRoleIconChoice;
import com.projectswg.common.network.packets.swg.zone.object_controller.ObjectController;
import com.projectswg.common.network.packets.swg.zone.object_controller.ShowLootBox;
import com.projectswg.common.network.packets.swg.zone.server_ui.SuiCreatePageMessage;
import com.projectswg.common.network.packets.swg.zone.server_ui.SuiEventNotification;
import com.projectswg.common.network.packets.swg.zone.spatial.AttributeListMessage;
import com.projectswg.common.network.packets.swg.zone.spatial.GetMapLocationsMessage;
import com.projectswg.common.network.packets.swg.zone.spatial.GetMapLocationsResponseMessage;
import com.projectswg.common.network.packets.swg.zone.spatial.NewTicketActivityResponseMessage;
import com.projectswg.common.network.packets.swg.zone.trade.AbortTradeMessage;
import com.projectswg.common.network.packets.swg.zone.trade.AcceptTransactionMessage;
import com.projectswg.common.network.packets.swg.zone.trade.AddItemFailedMessage;
import com.projectswg.common.network.packets.swg.zone.trade.AddItemMessage;
import com.projectswg.common.network.packets.swg.zone.trade.BeginTradeMessage;
import com.projectswg.common.network.packets.swg.zone.trade.BeginVerificationMessage;
import com.projectswg.common.network.packets.swg.zone.trade.DenyTradeMessage;
import com.projectswg.common.network.packets.swg.zone.trade.GiveMoneyMessage;
import com.projectswg.common.network.packets.swg.zone.trade.RemoveItemMessage;
import com.projectswg.common.network.packets.swg.zone.trade.TradeCompleteMessage;
import com.projectswg.common.network.packets.swg.zone.trade.UnAcceptTransactionMessage;
import com.projectswg.common.network.packets.swg.zone.trade.VerifyTradeMessage;

public enum PacketType {

	// Holocore
	HOLO_SET_PROTOCOL_VERSION					(HoloSetProtocolVersion.CRC, HoloSetProtocolVersion.class),
	HOLO_CONNECTION_STARTED						(HoloConnectionStarted.CRC,	HoloConnectionStarted.class),
	HOLO_CONNECTION_STOPPED						(HoloConnectionStopped.CRC,	HoloConnectionStopped.class),
	
	// Admin
	ADMIN_SHUTDOWN_SERVER						(AdminShutdownServer.CRC,	AdminShutdownServer.class),
	
	// Both
	SERVER_UNIX_EPOCH_TIME						(ServerUnixEpochTime.CRC, 	ServerUnixEpochTime.class),
	SERVER_ID									(ServerId.CRC, 				ServerId.class),
	SERVER_STRING								(ServerString.CRC, 			ServerString.class),
	LAG_REQUEST									(LagRequest.CRC,			LagRequest.class),

	// Login
	HOLO_LOGIN_REQUEST							(HoloLoginRequestPacket.CRC,	HoloLoginRequestPacket.class),
	HOLO_LOGIN_RESPONSE							(HoloLoginResponsePacket.CRC,	HoloLoginResponsePacket.class),
	CLIENT_ID_MSG								(ClientIdMsg.CRC, 				ClientIdMsg.class),
	ERROR_MESSAGE								(ErrorMessage.CRC, 				ErrorMessage.class),
	ACCOUNT_FEATURE_BITS						(AccountFeatureBits.CRC, 		AccountFeatureBits.class),
	CLIENT_PERMISSIONS_MESSAGE					(ClientPermissionsMessage.CRC, 	ClientPermissionsMessage.class),
	REQUEST_EXTENDED_CLUSTERS					(RequestExtendedClusters.CRC, 	RequestExtendedClusters.class),
	OFFLINE_SERVERS_MESSAGE     				(OfflineServersMessage.CRC, 	OfflineServersMessage.class),
	SERVER_NOW_EPOCH_TIME						(ServerNowEpochTime.CRC,		ServerNowEpochTime.class),
	GAME_SERVER_LAG_RESPONSE					(GameServerLagResponse.CRC,		GameServerLagResponse.class),

		// Post-Login
		LOGIN_CLIENT_ID							(LoginClientId.CRC, 			LoginClientId.class),
		LOGIN_INCORRECT_CLIENT_ID				(LoginIncorrectClientId.CRC, 	LoginIncorrectClientId.class),
		LOGIN_CLIENT_TOKEN						(LoginClientToken.CRC, 			LoginClientToken.class),
		LOGIN_ENUM_CLUSTER						(LoginEnumCluster.CRC, 			LoginEnumCluster.class),
		LOGIN_CLUSTER_STATUS					(LoginClusterStatus.CRC, 		LoginClusterStatus.class),
		ENUMERATE_CHARACTER_ID					(EnumerateCharacterId.CRC, 		EnumerateCharacterId.class),
		STATION_ID_HAS_JEDI_SLOT				(StationIdHasJediSlot.CRC, 		StationIdHasJediSlot.class),
		CHARACTER_CREATION_DISABLED				(CharacterCreationDisabled.CRC, CharacterCreationDisabled.class),

		// Character Creation
		CLIENT_CREATE_CHARACTER					(ClientCreateCharacter.CRC, 			ClientCreateCharacter.class),
		CREATE_CHARACTER_SUCCESS				(CreateCharacterSuccess.CRC, 			CreateCharacterSuccess.class),
		CREATE_CHARACTER_FAILURE				(CreateCharacterFailure.CRC, 			CreateCharacterFailure.class),
		APPROVE_NAME_REQUEST					(ClientVerifyAndLockNameRequest.CRC,	ClientVerifyAndLockNameRequest.class),
		APPROVE_NAME_RESPONSE					(ClientVerifyAndLockNameResponse.CRC, 	ClientVerifyAndLockNameResponse.class),
		RANDOM_NAME_REQUEST						(RandomNameRequest.CRC, 				RandomNameRequest.class),
		RANDOM_NAME_RESPONSE					(RandomNameResponse.CRC, 				RandomNameResponse.class),

		// Character Deletion
		DELETE_CHARACTER_RESPONSE				(DeleteCharacterResponse.CRC, 	DeleteCharacterResponse.class),
		DELETE_CHARACTER_REQUEST				(DeleteCharacterRequest.CRC, 	DeleteCharacterRequest.class),

	// Zone
	CONNECTION_SERVER_LAG_RESPONSE				(ConnectionServerLagResponse.CRC,	ConnectionServerLagResponse.class),
	SELECT_CHARACTER							(SelectCharacter.CRC, 				SelectCharacter.class),
	CMD_SCENE_READY								(CmdSceneReady.CRC, 				CmdSceneReady.class),
	CMD_START_SCENE								(CmdStartScene.CRC, 				CmdStartScene.class),
	HEART_BEAT_MESSAGE							(HeartBeat.CRC, 					HeartBeat.class),
	OBJECT_CONTROLLER							(ObjectController.CRC, 				ObjectController.class),
	BASELINE									(Baseline.CRC, 						Baseline.class),
	CONNECT_PLAYER_MESSAGE						(ConnectPlayerMessage.CRC, 			ConnectPlayerMessage.class),
	CONNECT_PLAYER_RESPONSE_MESSAGE				(ConnectPlayerResponseMessage.CRC, 	ConnectPlayerResponseMessage.class),
	GALAXY_LOOP_TIMES_REQUEST					(RequestGalaxyLoopTimes.CRC, 		RequestGalaxyLoopTimes.class),
	GALAXY_LOOP_TIMES_RESPONSE					(GalaxyLoopTimesResponse.CRC, 		GalaxyLoopTimesResponse.class),
	PARAMETERS_MESSAGE							(ParametersMessage.CRC, 			ParametersMessage.class),
	DELTA										(DeltasMessage.CRC, 				DeltasMessage.class),
	SERVER_TIME_MESSAGE							(ServerTimeMessage.CRC, 			ServerTimeMessage.class),
	SET_WAYPOINT_COLOR							(SetWaypointColor.CRC, 				SetWaypointColor.class),
	SHOW_BACKPACK								(ShowBackpack.CRC, 					ShowBackpack.class),
	SHOW_HELMET									(ShowHelmet.CRC, 					ShowHelmet.class),
	SERVER_WEATHER_MESSAGE						(ServerWeatherMessage.CRC, 			ServerWeatherMessage.class),
	PLAY_MUSIC_MESSAGE							(PlayMusicMessage.CRC,				PlayMusicMessage.class),
	PLAY_CLIENT_EFFECT_OBJECT_MESSAGE			(PlayClientEffectObjectMessage.CRC, PlayClientEffectObjectMessage.class),
	STOP_CLIENT_EFFECT_OBJECT_BY_LABEL			(StopClientEffectObjectByLabelMessage.CRC, 	StopClientEffectObjectByLabelMessage.class),
	EXPERTISE_REQUEST_MESSAGE					(ExpertiseRequestMessage.CRC,		ExpertiseRequestMessage.class),
	CHANGE_ROLE_ICON_CHOICE						(ChangeRoleIconChoice.CRC,			ChangeRoleIconChoice.class),
	SHOW_LOOT_BOX								(ShowLootBox.CRC,					ShowLootBox.class),
	CREATE_CLIENT_PATH_MESSAGE					(CreateClientPathMessage.CRC,		CreateClientPathMessage.class),
	DESTROY_CLIENT_PATH_MESSAGE					(DestroyClientPathMessage.CRC,		DestroyClientPathMessage.class),
	
		// Chat
		CHAT_CREATE_ROOM						(ChatCreateRoom.CRC,				ChatCreateRoom.class),
		CHAT_DESTROY_ROOM						(ChatDestroyRoom.CRC,				ChatDestroyRoom.class),
		CHAT_ON_CREATE_ROOM						(ChatOnCreateRoom.CRC,				ChatOnCreateRoom.class),
		CHAT_FRIENDS_LIST_UPDATE				(ChatFriendsListUpdate.CRC, 		ChatFriendsListUpdate.class),
		CHAT_IGNORE_LIST						(ChatIgnoreList.CRC, 				ChatIgnoreList.class),
		CHAT_INSTANT_MESSAGE_TO_CLIENT			(ChatInstantMessageToClient.CRC, 	ChatInstantMessageToClient.class),
		CHAT_INSTANT_MESSAGE_TO_CHARACTER		(ChatInstantMessageToCharacter.CRC, ChatInstantMessageToCharacter.class),
		CHAT_ON_CONNECT_AVATAR					(ChatOnConnectAvatar.CRC,			ChatOnConnectAvatar.class),
		CHAT_ON_DESTROY_ROOM					(ChatOnDestroyRoom.CRC, 			ChatOnDestroyRoom.class),
		CHAT_ON_ENTERED_ROOM					(ChatOnEnteredRoom.CRC, 			ChatOnEnteredRoom.class),
		CHAT_ON_LEAVE_ROOM						(ChatOnLeaveRoom.CRC, 				ChatOnLeaveRoom.class),
		CHAT_ON_RECEIVE_ROOM_INVITATION			(ChatOnReceiveRoomInvitation.CRC, 	ChatOnReceiveRoomInvitation.class),
		CHAT_ON_SEND_INSTANT_MESSAGE			(ChatOnSendInstantMessage.CRC, 		ChatOnSendInstantMessage.class),
		CHAT_ON_SEND_ROOM_MESSAGE				(ChatOnSendRoomMessage.CRC, 		ChatOnSendRoomMessage.class),
		CHAT_PERSISTENT_MESSAGE_TO_CLIENT		(ChatPersistentMessageToClient.CRC, ChatPersistentMessageToClient.class),
		CHAT_PERSISTENT_MESSAGE_TO_SERVER		(ChatPersistentMessageToServer.CRC, ChatPersistentMessageToServer.class),
		CHAT_DELETE_PERSISTENT_MESSAGE			(ChatDeletePersistentMessage.CRC, 	ChatDeletePersistentMessage.class),
		CHAT_REQUEST_PERSISTENT_MESSAGE			(ChatRequestPersistentMessage.CRC, 	ChatRequestPersistentMessage.class),
		CHAT_REQUEST_ROOM_LIST					(ChatRequestRoomList.CRC, 			ChatRequestRoomList.class),
		CHAT_ENTER_ROOM_BY_ID					(ChatEnterRoomById.CRC, 			ChatEnterRoomById.class),
		CHAT_QUERY_ROOM							(ChatQueryRoom.CRC, 				ChatQueryRoom.class),
		CHAT_ROOM_LIST							(ChatRoomList.CRC, 					ChatRoomList.class),
		CHAT_ROOM_MESSAGE						(ChatRoomMessage.CRC,				ChatRoomMessage.class),
		CHAT_SEND_TO_ROOM						(ChatSendToRoom.CRC, 				ChatSendToRoom.class),
		CHAT_REMOVE_AVATAR_FROM_ROOM			(ChatRemoveAvatarFromRoom.CRC, 		ChatRemoveAvatarFromRoom.class),
		CHAT_SERVER_STATUS						(ChatServerStatus.CRC, 				ChatServerStatus.class),
		CHAT_SYSTEM_MESSAGE						(ChatSystemMessage.CRC, 			ChatSystemMessage.class),
		CHAT_INVITE_AVATAR_TO_ROOM				(ChatInviteAvatarToRoom.CRC,		ChatInviteAvatarToRoom.class),
		CHAT_UNINVITE_FROM_ROOM					(ChatUninviteFromRoom.CRC,			ChatUninviteFromRoom.class),
		CHAT_KICK_AVATAR_FROM_ROOM				(ChatKickAvatarFromRoom.CRC,		ChatKickAvatarFromRoom.class),
		CHAT_BAN_AVATAR_FROM_ROOM				(ChatBanAvatarFromRoom.CRC,			ChatBanAvatarFromRoom.class),
		CHAT_UNBAN_AVATAR_FROM_ROOM				(ChatUnbanAvatarFromRoom.CRC,		ChatUnbanAvatarFromRoom.class),
		CHAT_ADD_MODERATOR_TO_ROOM				(ChatAddModeratorToRoom.CRC,		ChatAddModeratorToRoom.class),
		CHAT_REMOVE_MODERATOR_FROM_ROOM			(ChatRemoveModeratorFromRoom.CRC,	ChatRemoveModeratorFromRoom.class),
		CON_GENERIC_MESSAGE						(ConGenericMessage.CRC, 			ConGenericMessage.class),
		VOICE_CHAT_STATUS						(VoiceChatStatus.CRC, 				VoiceChatStatus.class),

		// Scene
		SCENE_END_BASELINES						(SceneEndBaselines.CRC, 				SceneEndBaselines.class),
		SCENE_CREATE_OBJECT_BY_NAME				(SceneCreateObjectByName.CRC, 			SceneCreateObjectByName.class),
		SCENE_CREATE_OBJECT_BY_CRC				(SceneCreateObjectByCrc.CRC, 			SceneCreateObjectByCrc.class),
		SCENE_DESTROY_OBJECT					(SceneDestroyObject.CRC, 				SceneDestroyObject.class),
		UPDATE_CONTAINMENT_MESSAGE				(UpdateContainmentMessage.CRC, 			UpdateContainmentMessage.class),
		UPDATE_CELL_PERMISSIONS_MESSAGE			(UpdateCellPermissionMessage.CRC, 		UpdateCellPermissionMessage.class),
		GET_MAP_LOCATIONS_MESSAGE				(GetMapLocationsMessage.CRC, 			GetMapLocationsMessage.class),
		GET_MAP_LOCATIONS_RESPONSE_MESSAGE		(GetMapLocationsResponseMessage.CRC, 	GetMapLocationsResponseMessage.class),

		// Spatial
		UPDATE_POSTURE_MESSAGE					(UpdatePostureMessage.CRC, 					UpdatePostureMessage.class),
		UPDATE_TRANSFORMS_MESSAGE				(UpdateTransformMessage.CRC, 				UpdateTransformMessage.class),
		UPDATE_TRANSFORM_WITH_PARENT_MESSAGE    (UpdateTransformWithParentMessage.CRC, 		UpdateTransformWithParentMessage.class),
		NEW_TICKET_ACTIVITY_RESPONSE_MESSAGE	(NewTicketActivityResponseMessage.CRC, 		NewTicketActivityResponseMessage.class),
		ATTRIBUTE_LIST_MESSAGE					(AttributeListMessage.CRC, 					AttributeListMessage.class),
		OPENED_CONTAINER_MESSAGE				(ClientOpenContainerMessage.CRC, 			ClientOpenContainerMessage.class),

		// Combat
		UPDATE_PVP_STATUS_MESSAGE				(UpdatePvpStatusMessage.CRC, 	UpdatePvpStatusMessage.class),
		GRANT_COMMAND_MESSAGE       			(GrantCommandMessage.CRC, 		GrantCommandMessage.class),

		// Server UI
		OBJECT_MENU_SELECT						(ObjectMenuSelect.CRC,		ObjectMenuSelect.class),
		SUI_CREATE_PAGE_MESSAGE					(SuiCreatePageMessage.CRC, 	SuiCreatePageMessage.class),
		SUI_EVENT_NOTIFICATION					(SuiEventNotification.CRC, 	SuiEventNotification.class),

		// Auction
		IS_VENDOR_OWNER_RESPONSE_MESSAGE		(IsVendorOwnerResponseMessage.CRC, 			IsVendorOwnerResponseMessage.class),
		AUCTION_QUERY_HEADERS_MESSAGE			(AuctionQueryHeadersMessage.CRC, 			AuctionQueryHeadersMessage.class),
		GET_AUCTION_DETAILS						(GetAuctionDetails.CRC, 					GetAuctionDetails.class),
		GET_AUCTION_DETAILS_RESPONSE			(GetAuctionDetailsResponse.CRC, 			GetAuctionDetailsResponse.class),
		CANCEL_LIVE_AUCTION_MESSAGE				(CancelLiveAuctionMessage.CRC, 				CancelLiveAuctionMessage.class),
		CANCEL_LIVE_AUCTION_RESPONSE_MESSAGE	(CancelLiveAuctionResponseMessage.CRC, 		CancelLiveAuctionResponseMessage.class),
		AUCTION_QUERY_HEADERS_RESPONSE_MESSAGE	(AuctionQueryHeadersResponseMessage.CRC, 	AuctionQueryHeadersResponseMessage.class),
		RETRIEVE_AUCTION_ITEM_MESSAGE			(RetrieveAuctionItemMessage.CRC, 			RetrieveAuctionItemMessage.class),
		RETRIEVE_AUCTION_ITEM_RESPONSE_MESSAGE	(RetrieveAuctionItemResponseMessage.CRC, 	RetrieveAuctionItemResponseMessage.class),
		IS_VENDOR_OWNER_MESSAGE					(IsVendorOwnerMessage.CRC,					IsVendorOwnerMessage.class),
		COMMODITIES_ITEM_TYPE_LIST_REPSONSE		(CommoditiesItemTypeListResponse.CRC,		CommoditiesItemTypeListResponse.class),
		COMMODITIES_ITEM_TYPE_LIST_REQUEST		(CommoditiesItemTypeListRequest.CRC,		CommoditiesItemTypeListRequest.class),
		
		// Travel
		ENTER_TICKET_PURCHASE_MODE_MESSAGE		(EnterTicketPurchaseModeMessage.CRC,		EnterTicketPurchaseModeMessage.class),
		PLANET_TRAVEL_POINT_LIST_REQUEST		(PlanetTravelPointListRequest.CRC,			PlanetTravelPointListRequest.class),
		PLANET_TRAVEL_POINT_LIST_RESPONSE		(PlanetTravelPointListResponse.CRC,			PlanetTravelPointListResponse.class),
		
		//Trade
		ABORT_TRADE_MESSAGE						(AbortTradeMessage.CRC,						AbortTradeMessage.class),
		ACCEPT_TRANSACTION_MESSAGE				(AcceptTransactionMessage.CRC,				AcceptTransactionMessage.class),
		ADD_ITEM_FAILED_MESSAGE					(AddItemFailedMessage.CRC,					AddItemFailedMessage.class),
		ADD_ITEM_MESSAGE						(AddItemMessage.CRC,						AddItemMessage.class),
		BEGIN_TRADE_MESSAGE						(BeginTradeMessage.CRC,						BeginTradeMessage.class),
		BEGIN_VERIFICATION_MESSAGE				(BeginVerificationMessage.CRC,				BeginVerificationMessage.class),
		DENY_TRADE_MESSAGE						(DenyTradeMessage.CRC,						DenyTradeMessage.class),
		GIVE_MONEY_MESSAGE						(GiveMoneyMessage.CRC,						GiveMoneyMessage.class),
		REMOVE_ITEM_MESSAGE						(RemoveItemMessage.CRC,						RemoveItemMessage.class),
		TRADE_COMPLETE_MESSAGE					(TradeCompleteMessage.CRC,					TradeCompleteMessage.class),
		UNACCEPT_TRANSACTION_MESSAGE			(UnAcceptTransactionMessage.CRC,			UnAcceptTransactionMessage.class),
		VERIFY_TRADE_MESSAGE					(VerifyTradeMessage.CRC,					VerifyTradeMessage.class),
	
		// GCW
		GCW_REGIONS_REQUEST_MESSAGE				(GcwRegionsReq.CRC,							GcwRegionsReq.class),
		GCW_REGIONS_RESPONSE_MESSAGE			(GcwRegionsRsp.CRC,							GcwRegionsRsp.class),
		GCW_GROUPS_RESPONSE_MESSAGE				(GcwGroupsRsp.CRC,							GcwGroupsRsp.class),
	
	UNKNOWN (0xFFFFFFFF, SWGPacket.class);
	
	private static final EnumLookup<Integer, PacketType> LOOKUP = new EnumLookup<>(PacketType.class, PacketType::getCrc);
	
	private final int crc;
	private final Class <? extends SWGPacket> c;
	
	PacketType(int crc, Class <? extends SWGPacket> c) {
		this.crc = crc;
		this.c = c;
	}
	
	public int getCrc() {
		return crc;
	}
	
	public Class<? extends SWGPacket> getSwgClass() {
		return c;
	}
	
	public static PacketType fromCrc(int crc) {
		return LOOKUP.getEnum(crc, PacketType.UNKNOWN);
	}
	
	public static SWGPacket getForCrc(int crc) {
		PacketType type = LOOKUP.getEnum(crc, PacketType.UNKNOWN);
		if (type == UNKNOWN)
			return null;
		Class <? extends SWGPacket> c = type.c;
		try {
			return c.getConstructor().newInstance();
		} catch (Exception e) {
			Log.e("Packet: [%08X] %s", crc, c.getName());
			Log.e(e);
		}
		return null;
	}
	
}
