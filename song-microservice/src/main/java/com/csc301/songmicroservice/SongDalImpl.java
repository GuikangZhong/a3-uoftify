package com.csc301.songmicroservice;

import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SongDalImpl implements SongDal {

	private final MongoTemplate db;

	@Autowired
	public SongDalImpl(MongoTemplate mongoTemplate) {
		this.db = mongoTemplate;
	}

	@Override
	public DbQueryStatus addSong(Song songToAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbQueryStatus findSongById(String songId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbQueryStatus getSongTitleById(String songId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbQueryStatus deleteSongById(String songId) {
		DbQueryStatus dbQueryStatus = null;
		//Initialize the query object
		Query query = new Query();
		//Specify query to delete a song by id
		query.addCriteria(Criteria.where("_id").is(songId));
		try {
			// try to remove the song
			DeleteResult deleteResult = db.remove(query, Song.class);
			if (deleteResult.getDeletedCount() == 0) {
				// if the input id does not exist, return not found
				dbQueryStatus = new DbQueryStatus("Id does not exist", DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			} else {
				dbQueryStatus = new DbQueryStatus("Successfully deleted", DbQueryExecResult.QUERY_OK);
			}
		} catch (Exception e) {
			dbQueryStatus = new DbQueryStatus("Server error", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbQueryStatus;
	}

	@Override
	public DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement) {
		// TODO Auto-generated method stub
		return null;
	}
}