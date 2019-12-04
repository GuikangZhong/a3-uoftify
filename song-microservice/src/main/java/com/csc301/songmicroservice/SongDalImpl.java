package com.csc301.songmicroservice;

import com.mongodb.bulk.UpdateRequest;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbQueryStatus updateSongFavouritesCount(String songId, boolean shouldDecrement) {
		DbQueryStatus dbQueryStatus;
		//Initialize the query and update object
		Query query = new Query();
		Update update = new Update();
		//Specify the query to find the song and change its count
		// if the decrement is true, and the favourite count of the song is greater than 0
		if (shouldDecrement) {
			query.addCriteria(new Criteria().andOperator(Criteria.where("_id").is(songId),
					Criteria.where("songAmountFavourites").gt(0)));
			// decrement the count
			update.inc("songAmountFavourites", -1);
		} else {
			query.addCriteria(Criteria.where("_id").is(songId));
			//increment the count
			update.inc("songAmountFavourites", 1);
		}
		try {
			UpdateResult updateResult = db.updateFirst(query, update, Song.class);
			if (updateResult.getMatchedCount() == 0) {
				dbQueryStatus = new DbQueryStatus("The song is not found or its count is already zero", DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			} else {
				dbQueryStatus = new DbQueryStatus("The count is updated", DbQueryExecResult.QUERY_OK);
			}
		} catch (Exception e) {
			dbQueryStatus = new DbQueryStatus("Server error", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
		return dbQueryStatus;
	}
}