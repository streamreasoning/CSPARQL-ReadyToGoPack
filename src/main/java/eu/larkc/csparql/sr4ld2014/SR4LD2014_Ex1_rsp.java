/*******************************************************************************
 * Copyright 2014 Davide Barbieri, Emanuele Della Valle, Marco Balduini
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Acknowledgements:
 * 
 * This work was partially supported by the European project LarKC (FP7-215535) 
 * and by the European project MODAClouds (FP7-318484)
 ******************************************************************************/
package eu.larkc.csparql.sr4ld2014;

import it.polimi.deib.csparql_rest_api.RSP_services_csparql_API;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.sr4ld2014.streamer.RemoteFoursquareStreamer;

public class SR4LD2014_Ex1_rsp {

	private static Logger logger = LoggerFactory.getLogger(SR4LD2014_Ex1_rsp.class);
	
	/*
	 * Example 1 rsp: usage of remote instance of csparql engine using the java api for calling the rsp implementation for csparql engine  
	 */
	
	public static void main(String[] args) {

		try{
			
			//Configure log4j logger for the csparql engine
			try {
				PropertyConfigurator.configure(new URL("http://streamreasoning.org/configuration_files/csparql_readyToGoPack_log4j.properties"));
			} catch (MalformedURLException e) {
				logger.error(e.getMessage(), e);
			}
			
			//create a new instance of the api manager
			RSP_services_csparql_API api = new RSP_services_csparql_API("http://localhost:8175");
			
			String queryBody = "REGISTER STREAM IsInFs AS "
					+ "PREFIX : <http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
					+ "CONSTRUCT { ?person :isIn ?room } "
					+ "FROM STREAM <http://streamreasoning.org/streams/rfs> [RANGE 10s STEP 1s] "
					+ "WHERE { "
					+ "?person :posts [ :who ?person ; :where ?room ] "
					+ "}";

			RemoteFoursquareStreamer rfs = new RemoteFoursquareStreamer(api,"http://streamreasoning.org/streams/rfs", "http://www.streamreasoning.org/ontologies/sr4ld2014-onto#", 1000L);

			//Register new stream in the engine
			api.registerStream("http://streamreasoning.org/streams/rfs");
			Thread rfsThread = new Thread(rfs);

			//Register new query in the engine
			String queryURI = api.registerQuery("IsInFs", queryBody);

			//Attach a result consumer to the query result proxy to print the results on the console
			api.addObserver(queryURI, "http://localhost:8178/results");

			//Start the thread that put the triples in the engine
			rfsThread.start();

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
