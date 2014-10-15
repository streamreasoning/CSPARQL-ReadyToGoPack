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
package eu.larkc.csparql.sr4ld2014.streamer;

import it.polimi.deib.csparql_rest_api.RSP_services_csparql_API;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

public class RemoteFoursquareStreamer implements Runnable  {

	private RSP_services_csparql_API csparqlAPI;
	private String streamName;
	private long sleepTime;
	private String baseUri;
	private boolean keepRunning = true;

	private Logger logger = LoggerFactory.getLogger(RemoteFoursquareStreamer.class.getName());

	public RemoteFoursquareStreamer(RSP_services_csparql_API csparqlAPI, String iri, String baseUri, long sleepTime) {
		super();
		this.csparqlAPI = csparqlAPI;
		this.streamName = iri;
		this.sleepTime = sleepTime;
		this.baseUri = baseUri;
	}

	public void run() {

		Model m;
		Random random = new Random();
		int senderIndex;
		int roomIndex;
		int postIndex;

		while(keepRunning){
			try {
				senderIndex = random.nextInt(5);
				roomIndex = random.nextInt(5);
				postIndex = random.nextInt(Integer.MAX_VALUE);

				m = ModelFactory.createDefaultModel();
				m.add(new ResourceImpl(baseUri + "person" + senderIndex), new PropertyImpl(baseUri + "posts"), new ResourceImpl(baseUri + "post" + postIndex));
				m.add(new ResourceImpl(baseUri+"post" + postIndex), new PropertyImpl(baseUri + "who"), new ResourceImpl(baseUri + "person" + senderIndex));
				m.add(new ResourceImpl(baseUri+"post" + postIndex), new PropertyImpl(baseUri + "where"), new ResourceImpl(baseUri + "room" + roomIndex));

				csparqlAPI.feedStream(streamName, m);
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				logger.error("Error while launching the sleep operation", e);
			}
		}
	}

	public void stopStream(){
		keepRunning = false;
	}

}
