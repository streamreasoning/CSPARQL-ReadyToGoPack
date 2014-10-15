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
package eu.larkc.csparql.readytogopack;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import eu.larkc.csparql.readytogopack.streamer.BasicIntegerRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.BasicRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.CloudMonitoringRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.DoorsTestStreamGenerator;
import eu.larkc.csparql.readytogopack.streamer.LBSMARDFStreamTestGenerator;

public class HelloWorldCSPARQL {

	private static Logger logger = LoggerFactory.getLogger(HelloWorldCSPARQL.class);
	
	public static void main(String[] args) {
		
		try {
			PropertyConfigurator.configure(new URL("http://streamreasoning.org/configuration_files/csparql_readyToGoPack_log4j.properties"));
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
		}
		
		// examples of streams and queries

		final int WHO_LIKES_WHAT = 0;
		final int HOW_MANY_USERS_LIKE_THE_SAME_OBJ = 1;
		final int MULTI_STREAM = 2;
		final int FIND_OPINION_MAKERS = 3;
		final int STREAMING_AND_EXTERNAL_STATIC_RDF_GRAPH = 4;
		final int DOOR_TEST = 5;
		final int CLOUD_MONITORING_TEST = 6;
		final int COMPOSABILITY = 7;
		final int PERCENTILE = 8;

		// put here the example you want to run
		
		int key = WHO_LIKES_WHAT;

		// initializations
		
//		String streamURI = "http://myexample.org/stream";
		String query = null;
		String queryDownStream = null;
		RdfStream tg = null;
		RdfStream anotherTg = null;

		switch (key) {
		case WHO_LIKES_WHAT:
			
			logger.debug("WHO_LIKES_WHAT example");
			
			query = "REGISTER QUERY WhoLikesWhat AS "
					+ "PREFIX ex: <http://myexample.org/> "
					+ "SELECT ?s ?o "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 5s STEP 1s] "
					+ "WHERE { ?s ex:likes ?o }";
			tg = new LBSMARDFStreamTestGenerator("http://myexample.org/stream");

			break;
		case HOW_MANY_USERS_LIKE_THE_SAME_OBJ:
			
			logger.debug("HOW_MANY_USERS_LIKE_THE_SAME_OBJ example");

			query = "REGISTER QUERY HowManyUsersLikeTheSameObj AS "
					+ "PREFIX ex: <http://myexample.org/> "
					+ "SELECT ?o (count(?s) as ?countUsers) "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 5s STEP 1s] "
					+ "WHERE { ?s ex:likes ?o } " + "GROUP BY ?o ";
			tg = new LBSMARDFStreamTestGenerator("http://myexample.org/stream");
			break;

		case MULTI_STREAM:
			
			logger.debug("MULTI_STREAM example");

			query = "REGISTER QUERY TrendyObjectsOnMultipleSocialNetworks AS "
					+ "PREFIX ex: <http://myexample.org/> "
					+ "SELECT ?o (count(?s) as ?countUsers) "
					+ "FROM STREAM <http://myexample.org/stream1> [RANGE 5s STEP 1s] "
					+ "FROM STREAM <http://myexample.org/stream2> [RANGE 5s STEP 1s] "
					+ "WHERE { ?s ex:likes ?o }" + "GROUP BY ?o "
					+ "ORDER BY DESC(?countUsers)";
			tg = new LBSMARDFStreamTestGenerator("http://myexample.org/stream1");
			anotherTg = new LBSMARDFStreamTestGenerator(
					"http://myexample.org/stream2");
			break;

		case FIND_OPINION_MAKERS:
			
			logger.debug("FIND_OPINION_MAKERS example");

			query = ""
					+ "REGISTER QUERY FindOpinionMakers AS "
					+ "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
					+ "PREFIX ex: <http://myexample.org/> "
					+ "SELECT ?opinionMaker ?o (COUNT(?follower) AS ?n) "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 10s STEP 5s]"
					+ "WHERE { "
					+ "?opinionMaker ex:likes ?o . "
					+ "?follower ex:likes ?o . "
					+ "FILTER(?opinionMaker!=?follower)"
					+ "FILTER (f:timestamp(?follower,ex:likes,?o) > f:timestamp(?opinionMaker,ex:likes,?o)) "
					+ "} " + "GROUP BY ?opinionMaker ?o "
					+ "HAVING (COUNT(?follower)>3)";
			tg = new LBSMARDFStreamTestGenerator("http://myexample.org/stream");

			break;

		case STREAMING_AND_EXTERNAL_STATIC_RDF_GRAPH:
			
			logger.debug("STREAMING_AND_EXTERNAL_STATIC_RDF_GRAPH example");

			query = ""
					+ "REGISTER QUERY StreamingAndExternalStaticRdfGraph AS "
					+ "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
					+ "PREFIX ex: <http://myexample.org/> "
					+ "SELECT ?opinionMaker ?o (COUNT(?follower) AS ?n) "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 10s STEP 5s]"
					+ "FROM <http://streamreasoning.org/larkc/csparql/LBSMA-static-k.rdf> "
					+ "WHERE { "
					+ "?opinionMaker ex:likes ?o . "
					+ "?follower ex:follows ?opinionMaker . "
					+ "?follower ex:likes ?o . "
					+ "FILTER(?opinionMaker!=?follower)"
					+ "FILTER (f:timestamp(?follower,ex:likes,?o) > f:timestamp(?opinionMaker,ex:likes,?o)) "
					+ "} " 
					+ "GROUP BY ?opinionMaker ?o "
					+ "HAVING (COUNT(?follower)>3)";

			tg = new LBSMARDFStreamTestGenerator("http://myexample.org/stream");
			break;

		case DOOR_TEST:
			
			logger.debug("DOOR_TEST example");

			query = "REGISTER QUERY test AS "
					+ "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
					+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
					+ "PREFIX iri: <http://www.streamreasoning.org/csparql/> "
					+ "SELECT ?s (f:timestamp(?s,?p,iri:opened) AS ?ts ) "
					+ "FROM STREAM <http://www.streamreasoning.org/csparql> [RANGE 5s STEP 5s] "
					+ "WHERE { "
					+ "?s ?p iri:opened . "
					+ "?s ?p iri:closed . "
					+ "FILTER(f:timestamp(?s,?p,iri:opened) > f:timestamp(?s,?p,iri:closed)) "
					+ "}";
			tg = new DoorsTestStreamGenerator(
					"http://www.streamreasoning.org/csparql");

			break;

		case CLOUD_MONITORING_TEST:
			
			logger.debug("CLOUD_MONITORING_TEST example");

			query = "REGISTER QUERY HelloWorld AS "
					+ "PREFIX mc: <http://www.modaclouds.eu/ontologies/2013/2/monitoring#> "
					+ "CONSTRUCT { [] a mc:CPUUtilizationAlert ; mc:isOn ?vm . } "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 10s STEP 5s] "
					+ "FROM <http://streamreasoning.org/modaclouds/ModaCloudsSK.rdf> "
					+ "WHERE { " + "?m a mc:MySQL ; " + "mc:isIn ?vm . "
					+ "?vm mc:exposes ?dc . " + "?dc mc:observes ?o . "
					+ "?o mc:hasMonitoredMetric mc:CPUUtilization ; "
					+ "mc:isAbout ?m ; " + "mc:hasValue ?v . " + "} "
					+ "GROUP BY ?vm " + "HAVING(avg(?v) > 40) ";

			tg = new CloudMonitoringRDFStreamTestGenerator(
					"http://myexample.org/stream");
			break;

		case COMPOSABILITY:
			
			logger.debug("COMPOSABILITY example");

			query = "REGISTER STREAM UpStreamQuery AS "
					+ "CONSTRUCT {?s <http://myexample.org/stream2/P> ?o }"
					+ "FROM STREAM <http://myexample.org/stream1> [RANGE 5s STEP 1s] "
					+ "WHERE { ?s ?p ?o }";
			queryDownStream = "REGISTER STREAM DownStreamQuery AS "
					+ "SELECT ?s ?p ?o "
					+ "FROM STREAM <http://myexample.org/stream2> [RANGE 5s STEP 1s] "
					+ "WHERE { ?s ?p ?o }";
			tg = new BasicRDFStreamTestGenerator("http://myexample.org/stream1");
			anotherTg = new RDFStreamFormatter("http://myexample.org/stream2");
			break;
			
		case PERCENTILE:
			
			logger.debug("PERCENTILE example");

			query = "REGISTER QUERY HelloWorld AS "
					+ "PREFIX mc: <http://www.modaclouds.eu/ontologies/2013/2/monitoring#> "
					+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
					+ "SELECT ?s (COUNT(?o1) AS ?ok) (COUNT(?o) AS ?tot) ((COUNT(?o1) / COUNT(?o)) AS ?fr) "
					+ "FROM STREAM <http://myexample.org/stream> [RANGE 10s STEP 10s] "
					+ "WHERE { " +
					"{ " +
					"?s ?p ?o . " +
					"} " +
					"UNION " +
					"{ " +
					"?s ?p ?o1 " +
					"FILTER(?o1 > \"8\"^^xsd:integer) " +
					"} " +
					"} " +
					"GROUP BY ?s " +
					"HAVING (?fr > 0.1)";
					
			tg = new BasicIntegerRDFStreamTestGenerator("http://myexample.org/stream");
			break;

		default:
			System.exit(0);
			break;
		}

		// Initialize C-SPARQL Engine

		CsparqlEngine engine = new CsparqlEngineImpl();
		
		/*
		 * Choose one of the the following initialize methods: 
		 * 1 - initialize() - Inactive timestamp function - Inactive injecter 
		 * 2 - initialize(int* queueDimension) - Inactive timestamp function -
		 *     Active injecter with the specified queue dimension (if 
		 *     queueDimension = 0 the injecter will be inactive) 
		 * 3 - initialize(boolean performTimestampFunction) - if
		 *     performTimestampFunction = true, the timestamp function will be
		 *     activated - Inactive injecter 
		 * 4 - initialize(int queueDimension, boolean performTimestampFunction) - 
		 *     if performTimestampFunction = true, the timestamp function will
		 *     be activated - Active injecter with the specified queue dimension
		 *     (if queueDimension = 0 the injecter will be inactive)
		 */
		
		engine.initialize(true);

		// Register an RDF Stream

		engine.registerStream(tg);

		// Start Streaming (this is only needed for the example, normally streams are external
		// C-SPARQL Engine users are supposed to write their own adapters to create RDF streams

		final Thread t = new Thread((Runnable) tg);
		t.start();
		if (anotherTg != null) {
			engine.registerStream(anotherTg);
			if (key != COMPOSABILITY) {
				final Thread t2 = new Thread((Runnable) anotherTg);
				t2.start();
			}
		}

		// Register a C-SPARQL query

		CsparqlQueryResultProxy c1 = null;
		CsparqlQueryResultProxy c2 = null;

		if (key != COMPOSABILITY) {

			try {
				c1 = engine.registerQuery(query, false);
				logger.debug("Query: {}", query);
				logger.debug("Query Start Time : {}", System.currentTimeMillis());
			} catch (final ParseException ex) {
				logger.error(ex.getMessage(), ex);
			}

			// Attach a Result Formatter to the query result proxy

			if (c1 != null) {
				c1.addObserver(new ConsoleFormatter());
			}

		} else {
			try {
				c1 = engine.registerQuery(query, false);
				logger.debug("Query: {}", query);
				logger.debug("Query Start Time : {}", System.currentTimeMillis());
			} catch (final ParseException ex) {
				logger.error(ex.getMessage(), ex);
			}

			// Attach a Result Formatter to the query result proxy

			if (c1 != null) {
				c1.addObserver((RDFStreamFormatter) anotherTg);

				try {
					c2 = engine.registerQuery(queryDownStream, false);
					logger.debug("Query: {}", query);
					logger.debug("Query Start Time : {}", System.currentTimeMillis());
				} catch (final ParseException ex) {
					logger.error(ex.getMessage(), ex);
				}

				if (c2 != null) {
					c2.addObserver(new ConsoleFormatter());

				}

			}
		}

		// leave the system running for a while
		// normally the C-SPARQL Engine should be left running
		// the following code shows how to stop the C-SPARQL Engine gracefully
		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

		if (key != COMPOSABILITY) {
			// clean up (i.e., unregister query and stream)
			engine.unregisterQuery(c1.getId());

			((LBSMARDFStreamTestGenerator) tg).pleaseStop();

			engine.unregisterStream(tg.getIRI());

			if (anotherTg != null) {
				engine.unregisterStream(anotherTg.getIRI());
			}
		} else {
			// clean up (i.e., unregister query and stream) 
			engine.unregisterQuery(c1.getId());
			engine.unregisterQuery(c2.getId());

			((LBSMARDFStreamTestGenerator) tg).pleaseStop();

			engine.unregisterStream(tg.getIRI());
			engine.unregisterStream(anotherTg.getIRI());
		}

		System.exit(0);

		

	}

}
