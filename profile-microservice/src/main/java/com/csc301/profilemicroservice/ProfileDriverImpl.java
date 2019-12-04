package com.csc301.profilemicroservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import org.springframework.stereotype.Repository;
import org.neo4j.driver.v1.Transaction;

import static org.neo4j.driver.v1.Values.parameters;

@Repository
public class ProfileDriverImpl implements ProfileDriver {

	Driver driver = ProfileMicroserviceApplication.driver;

	public static void InitProfileDb() {
		String queryStr;

		try (Session session = ProfileMicroserviceApplication.driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.userName)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT exists(nProfile.password)";
				trans.run(queryStr);

				queryStr = "CREATE CONSTRAINT ON (nProfile:profile) ASSERT nProfile.userName IS UNIQUE";
				trans.run(queryStr);

				trans.success();
			}
			session.close();
		}
	}
	
	@Override
	public DbQueryStatus createUserProfile(String userName, String fullName, String password) {

		try (Session session = driver.session()) {
			try (Transaction trans = session.beginTransaction()) {
				trans.run("CREATE (p:profile) SET p.userName = $userName, p.name = $fullName, p.password = $password",
						parameters( "userName", userName, "fullName", fullName, "password", password) );
				trans.success();
			}
			session.close();

			return new DbQueryStatus("Success for creating a profile.", DbQueryExecResult.QUERY_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new DbQueryStatus("Failure, the user name already exist!", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
	}

	@Override
	public DbQueryStatus followFriend(String userName, String frndUserName) {

		try (Session session = driver.session()) {
			StatementResult statementResult = null;
			try (Transaction trans = session.beginTransaction()) {
				statementResult = trans.run("match (p:profile {userName: $userName})" +
								", (f:profile {userName: $frndUserName}) merge ((p) -[r:follows]->(f)) return r",
						parameters( "userName", userName, "frndUserName", frndUserName));
				trans.success();
			}
			session.close();

			String message;
			if (statementResult.hasNext()) {			// if successfully follows
				message = "User " + userName + " successfully follows User " + frndUserName;
				return new DbQueryStatus(message, DbQueryExecResult.QUERY_OK);
			} else {									// otherwise
				message = "User " + userName + " or User " + frndUserName + " not found";
				return new DbQueryStatus(message, DbQueryExecResult.QUERY_ERROR_NOT_FOUND);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new DbQueryStatus("Internal Error", DbQueryExecResult.QUERY_ERROR_GENERIC);
		}
	}

	@Override
	public DbQueryStatus unfollowFriend(String userName, String frndUserName) {
		
		return null;
	}

	@Override
	public DbQueryStatus getAllSongFriendsLike(String userName) {
			
		return null;
	}
}
