/*******************************************************************************
 * Copyright 2013 Davide Barbieri, Emanuele Della Valle, Marco Balduini
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;

public class DoorsTestStreamGenerator extends RdfStream implements Runnable {

	/** The logger. */
	protected final Logger logger = LoggerFactory
			.getLogger(BasicRDFStreamTestGenerator.class);	

	public DoorsTestStreamGenerator(final String iri) {
		super(iri);
	}

	@Override
	public void run() {

		try {

			Thread.sleep(700);
			
			Thread.sleep(1000);
			
			RdfQuadruple q = new RdfQuadruple(getIRI() + "/d1", getIRI()+"/status", getIRI() + "/opened", System.currentTimeMillis());
			this.put(q);

//			System.out.println(q.toString());
			
			q = new RdfQuadruple(getIRI() + "/d1", getIRI()+"/status", getIRI() + "/closed", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());
			
			q = new RdfQuadruple(getIRI() + "/d2", getIRI()+"/status", getIRI() + "/closed", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());

			Thread.sleep(1000);

			q = new RdfQuadruple(getIRI() + "/d2", getIRI()+"/status", getIRI() + "/opened", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());
			
			Thread.sleep(4000);

			q = new RdfQuadruple(getIRI() + "/d1", getIRI()+"/status", getIRI() + "/opened", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());
			
			q = new RdfQuadruple(getIRI() + "/d1", getIRI()+"/status", getIRI() + "/closed", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());
			
			Thread.sleep(1000);
			
			q = new RdfQuadruple(getIRI() + "/d1", getIRI()+"/status", getIRI() + "/opened", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());

			Thread.sleep(2000);

			q = new RdfQuadruple(getIRI() + "/d2", getIRI()+"/status", getIRI() + "/closed", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());

			Thread.sleep(2000);

			q = new RdfQuadruple(getIRI() + "/d3", getIRI()+"/status", getIRI() + "/closed", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());
			
			Thread.sleep(2000);

			q = new RdfQuadruple(getIRI() + "/d3", getIRI()+"/status", getIRI() + "/opened", System.currentTimeMillis());
			this.put(q);
			
//			System.out.println(q.toString());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
