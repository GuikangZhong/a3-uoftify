package com.csc301.profilemicroservice;

import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.csc301.profilemicroservice.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class ProfileController {
	public static final String KEY_USER_NAME = "userName";
	public static final String KEY_USER_FULLNAME = "fullName";
	public static final String KEY_USER_PASSWORD = "password";

	@Autowired
	private final ProfileDriverImpl profileDriver;

	@Autowired
	private final PlaylistDriverImpl playlistDriver;

	OkHttpClient client = new OkHttpClient();

	public ProfileController(ProfileDriverImpl profileDriver, PlaylistDriverImpl playlistDriver) {
		this.profileDriver = profileDriver;
		this.playlistDriver = playlistDriver;
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> addSong(@RequestParam Map<String, String> params,
			HttpServletRequest request) {
		// get request body
		String userName = params.get(KEY_USER_NAME);
		String fullName = params.get(KEY_USER_FULLNAME);
		String password = params.get(KEY_USER_PASSWORD);

		// run query
		DbQueryStatus dbQueryStatus = profileDriver.createUserProfile(userName, fullName, password);

		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("POST %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}

	@RequestMapping(value = "/followFriend/{userName}/{friendUserName}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> followFriend(@PathVariable("userName") String userName,
			@PathVariable("friendUserName") String friendUserName, HttpServletRequest request) {

		// run query
		DbQueryStatus dbQueryStatus = profileDriver.followFriend(userName, friendUserName);

		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}

	@RequestMapping(value = "/getAllFriendFavouriteSongTitles/{userName}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getAllFriendFavouriteSongTitles(@PathVariable("userName") String userName,
			HttpServletRequest request) {

		// run query
		DbQueryStatus dbQueryStatus = profileDriver.getAllSongFriendsLike(userName);


		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), dbQueryStatus.getData());
		return response;
	}


	@RequestMapping(value = "/unfollowFriend/{userName}/{friendUserName}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> unfollowFriend(@PathVariable("userName") String userName,
			@PathVariable("friendUserName") String friendUserName, HttpServletRequest request) {
		// run query
		DbQueryStatus dbQueryStatus = profileDriver.unfollowFriend(userName, friendUserName);

		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}

	@RequestMapping(value = "/likeSong/{userName}/{songId}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> likeSong(@PathVariable("userName") String userName,
			@PathVariable("songId") String songId, HttpServletRequest request) throws IOException {

		//verify the song id
		HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:3001/updateSongFavouritesCount/" + songId +
				"?shouldDecrement=false").newBuilder();
		String url = urlBuilder.build().toString();

		Request mongoRequest = new Request.Builder().url(url).put(new RequestBody() {
			@Nullable
			@Override
			public MediaType contentType() {
				return null;
			}

			@Override
			public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

			}
		}).build();

		Response response1 = client.newCall(mongoRequest).execute();
		System.out.println(response1);

		// run query
		DbQueryStatus dbQueryStatus = playlistDriver.likeSong(userName, songId);


		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}

	@RequestMapping(value = "/unlikeSong/{userName}/{songId}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> unlikeSong(@PathVariable("userName") String userName,
			@PathVariable("songId") String songId, HttpServletRequest request) {

		// run query
		DbQueryStatus dbQueryStatus = playlistDriver.unlikeSong(userName, songId);

		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}

	@RequestMapping(value = "/deleteAllSongsFromDb/{songId}", method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> deleteAllSongsFromDb(@PathVariable("songId") String songId,
			HttpServletRequest request) {

		// run query
		DbQueryStatus dbQueryStatus = playlistDriver.deleteSongFromDb(songId);

		// construct and send response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("path", String.format("PUT %s", Utils.getUrl(request)));
		response.put("message", dbQueryStatus.getMessage());
		Utils.setResponseStatus(response, dbQueryStatus.getdbQueryExecResult(), null);
		return response;
	}
}