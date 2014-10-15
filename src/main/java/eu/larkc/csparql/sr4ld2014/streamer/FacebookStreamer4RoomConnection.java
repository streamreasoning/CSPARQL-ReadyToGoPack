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

import java.util.Random;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;

public class FacebookStreamer4RoomConnection extends RdfStream implements Runnable  {

	private long sleepTime;
	private String baseUri;

	public FacebookStreamer4RoomConnection(String iri, String baseUri,long sleepTime) {
		super(iri);
		this.sleepTime = sleepTime;
		this.baseUri = baseUri;
	}

	public void run() {

		Random random = new Random();
		int senderIndex;
		int subjectIndex;
		int postIndex;
		int numberOfPerson;

		while(true){
			try{

				numberOfPerson = random.nextInt(3);
				senderIndex = random.nextInt(5);
				subjectIndex = random.nextInt(5);
				postIndex = random.nextInt(Integer.MAX_VALUE);

				RdfQuadruple q = new RdfQuadruple(baseUri + "person" + senderIndex, baseUri + "posts", baseUri + "post" + postIndex, System.currentTimeMillis());
				this.put(q);
				if(numberOfPerson < 2){
					q = new RdfQuadruple(baseUri+"post" + postIndex, baseUri + "who", baseUri + "person" + subjectIndex, System.currentTimeMillis());
					this.put(q);
				} else {
					q = new RdfQuadruple(baseUri+"post" + postIndex, baseUri + "who", baseUri + "person" + senderIndex, System.currentTimeMillis());
					this.put(q);
					q = new RdfQuadruple(baseUri+"post" + postIndex, baseUri + "who", baseUri + "person" + subjectIndex, System.currentTimeMillis());
					this.put(q);
				}
				q = new RdfQuadruple(baseUri+"post" + postIndex, baseUri + "where", baseUri + "room", System.currentTimeMillis());
				this.put(q);

				Thread.sleep(sleepTime);

			} catch(Exception e){
				e.printStackTrace();
			}
		}

	}

}
