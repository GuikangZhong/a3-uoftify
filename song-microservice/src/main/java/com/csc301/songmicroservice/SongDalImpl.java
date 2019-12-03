package com.csc301.songmicroservice;

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
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(songId));
		Song song = db.findOne(query, Song.class);
		return null;
	}

	@Override
	public DbQueryStatus getSongTitleById(String songId) {
		// Initialize the query
		DbQueryStatus dbQueryStatus = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(songId));
		try{
			//find the song
			Song song = db.findOne(query, Song.class);
			// create an appropriate query status
			dbQueryStatus = new DbQueryStatus(null, DbQueryExecResult.QUERY_OK);
			dbQueryStatus.setData(song.getSongName());
			return dbQueryStatus;
		} catch (NullPointerException e) {
			// if the result not found
			dbQueryStatus = new DbQueryStatus(null, DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		} catch (Exception e) {
			// if the server has error
			dbQueryStatus = new DbQueryStatus(null, DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbQueryStatus;
	}

	@Override
	public DbQueryStatus deleteSongById(String songId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement) {
		// TODO Auto-generated method stub
		return null;
	}
}