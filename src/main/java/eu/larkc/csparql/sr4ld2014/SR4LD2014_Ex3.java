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

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import eu.larkc.csparql.sr4ld2014.streamer.FacebookStreamer;
import eu.larkc.csparql.sr4ld2014.streamer.FoursquareStreamer;

public class SR4LD2014_Ex3 {

	private static Logger logger = LoggerFactory.getLogger(SR4LD2014_Ex3.class);
	
	/*
	 * Example 3: usage of local instance of csparql and perform a query chain
	 */
	
	public static void main(String[] args) {

		try{
			
			//Configure log4j logger for the csparql engine
			PropertyConfigurator.configure("log4j_configuration/csparql_readyToGoPack_log4j.properties");
			
			//Stop logging the debug level to gain a better view on the console
			org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
			
			CsparqlEngineImpl engine = new CsparqlEngineImpl();
			engine.initialize();
			
			String isInFsBody = "REGISTER STREAM IsInFs AS "
					+ "PREFIX : <http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
					+ "CONSTRUCT { ?person :isIn ?room } "
					+ "FROM STREAM <http://streamreasoning.org/streams/fs> [RANGE 10s STEP 1s] "
					+ "WHERE { "
					+ "?person :posts [ :who ?person ; :where ?room ] "
					+ "}";
			
			String isWithFbBody = "REGISTER STREAM IsWithFb AS "
					+ "PREFIX : <http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
					+ "CONSTRUCT { ?person1 :isWith ?person } "
					+ "FROM STREAM <http://streamreasoning.org/streams/fb> [RANGE 10s STEP 1s] "
					+ "WHERE { "
					+ "?person1 :posts [ :who ?person1, ?person ] . "
					+ "FILTER (?person1 != ?person) "
					+ "}";
			
			String isInWithBody = "REGISTER STREAM isInWith AS "
					+ "PREFIX : <http://www.streamreasoning.org/ontologies/sr4ld2014-onto#> "
					+ "CONSTRUCT { ?person :isIn ?room } "
					+ "FROM STREAM <http://streamreasoning.org/streams/IsInFs> [RANGE 10s STEP 1s] "
					+ "FROM STREAM <http://streamreasoning.org/streams/IsWithFb> [RANGE 10s STEP 1s] "
					+ "WHERE { "
					+ "?person :isWith ?person1 . "
					+ "?person1 :isIn ?room . "
					+ "FILTER (?person1 != ?person) "
					+ "}";

			FoursquareStreamer fs = new FoursquareStreamer("http://streamreasoning.org/streams/fs", "http://www.streamreasoning.org/ontologies/sr4ld2014-onto#", 1000L);
			FacebookStreamer fb = new FacebookStreamer("http://streamreasoning.org/streams/fb", "http://www.streamreasoning.org/ontologies/sr4ld2014-onto#", 1000L);

			//Register the 2 different streams, foursquare and facebook
			engine.registerStream(fs);
			engine.registerStream(fb);

			Thread fsThread = new Thread(fs);
			Thread fbThread = new Thread(fb);

			//Register the simple query on facebook and foursquare stream
			CsparqlQueryResultProxy isInFsProxy = engine.registerQuery(isInFsBody, false);
			CsparqlQueryResultProxy isWithFbProxy = engine.registerQuery(isWithFbBody, false);
			
			//Create new stream formatter to create new RDF stream from the results of a query
			RDFStreamFormatter isInFsStreamFormatter = new RDFStreamFormatter("http://streamreasoning.org/streams/IsInFs");
			RDFStreamFormatter isWithFbStreamFormatter = new RDFStreamFormatter("http://streamreasoning.org/streams/IsWithFb");
			
			//Register the new streams in the engine. The isInFsStreamFormatter and isWithFbStreamFormatter will contain the results of the IsInFs and IsWithFb query
			engine.registerStream(isInFsStreamFormatter);
			engine.registerStream(isWithFbStreamFormatter);

			//Add the observers to queries to get the results of the queries instantaneously
			isInFsProxy.addObserver(isInFsStreamFormatter);
			isWithFbProxy.addObserver(isWithFbStreamFormatter);

			//Register the query to consume the results of the 2 registered queries
			CsparqlQueryResultProxy isInWithProxy = engine.registerQuery(isInWithBody, false);	
			
			//Attach a result consumer to the query result proxy to print the results on the console
			isInWithProxy.addObserver(new ConsoleFormatter());

			//Start streaming data
			fsThread.start();
			fbThread.start();

		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
