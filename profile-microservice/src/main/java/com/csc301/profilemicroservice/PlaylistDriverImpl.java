package com.csc301.profilemicroservice;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Repository;
import org.neo4j.driver.v1.Transaction;

import static org.neo4j.driver.v1.Values.parameters;

@Repository
public class PlaylistDriverImpl implements PlaylistDriver {

	Driver driver = ProfileMicroserviceApplication.driver;

	public static void InitPlaylistDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nPlaylist:playlist) ASSERT exists(nPlaylist.plName)";
				trans.run(queryStr);
				trans.success();
			}
			session.close();
		}
	}

	@Override
	public DbQueryStatus likeSong(String userName, String songId) {

		try (Session session = driver.session()) {

			StatementResult statementResult;

			try (Transaction trans = session.beginTransaction()) {
				statementResult = trans.run("match (pl:playlist {plName: $plName}) " +
								"merge (s:song {songId: $songId}) merge (pl) -[:includes]-> (s) return pl",
						parameters( "plName", (userName+"-favorites"), "songId", songId));
				trans.success();
			}
			session.close();

			if (statementResult.hasNext())
				return new DbQueryStatus("Successfully liked song.", DbQueryExecResult.QUERY_OK);
			else
				return new DbQueryStatus("User not Found.", DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			return new DbQueryStatus("Failure!", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}

	}

	@Override
	public DbQueryStatus unlikeSong(String userName, String songId) {

		try (Session session = driver.session()) {
			StatementResult statementResult = null;
			DbQueryStatus dbQueryStatus;
			String message;

			// check whether or not the user have the song in his favorites list
			try (Transaction trans = session.beginTransaction()) {
				statementResult = trans.run("match (pl:playlist {plName: $plName}) -[r:includes]-> " +
								"(s:song {songId: $songId}) return r",
						parameters( "plName", (userName+"-favorites"), "songId", songId));

				// if they are friends
				if (statementResult.hasNext()) {
					trans.run("match (pl:playlist {plName: $plName}) -[r:includes]-> " +
									"(s:song {songId: $songId}) delete r",
							parameters( "plName", (userName+"-favorites"), "songId", songId));
					trans.success();
					message = "User successfully unliked the song.";
					dbQueryStatus =  new DbQueryStatus(message, DbQueryExecResult.QUERY_OK);
				} else {	// otherwise
					message = "User is not exist or the song is not in his favorites list.";
					dbQueryStatus =  new DbQueryStatus(message, DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
				}
			}

			session.close();
			return dbQueryStatus;
		} catch (Exception e) {
			e.printStackTrace();
			return new DbQueryStatus("Internal Error", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
	}

	@Override
	public DbQueryStatus deleteSongFromDb(String songId) {
		
		return null;
	}
}
