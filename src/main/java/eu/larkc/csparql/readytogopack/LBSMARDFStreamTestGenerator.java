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

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;

public class LBSMARDFStreamTestGenerator extends RdfStream implements Runnable {

	/** The logger. */
	protected final Logger logger = LoggerFactory
			.getLogger(LBSMARDFStreamTestGenerator.class);	

	private int c = 1;
	private int ct = 1;
	private boolean keepRunning = false;

	public LBSMARDFStreamTestGenerator(final String iri) {
		super(iri);
	}

	public void pleaseStop() {
		keepRunning = false;
	}

	@Override
	public void run() {



		keepRunning = true;
		
		while (keepRunning) {
			final RdfQuadruple q = new RdfQuadruple(super.getIRI()+"/user" + this.c,
					"http://myexample.org/likes", "http://myexample.org/O" + this.c, System.currentTimeMillis());

			this.put(q);
			//          logger.info(q.toString());
			ct++;

			double n = Math.random()*5;

			for (int i=0;i<n;i++) {
				final RdfQuadruple q1 = new RdfQuadruple(super.getIRI()+"/user" + this.c+i,
						"http://myexample.org/likes", "http://myexample.org/O" + this.c, System.currentTimeMillis());
				this.put(q1);
				//         logger.info(q1.toString());
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ct++;
			}

			if(c%10==0) logger.info(ct+ " triples streamed so far");


			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.c++;
		}
	}

	public static String dumpRelatedStaticKnowledge(int maxUser) {

		Model m = ModelFactory.createDefaultModel(); 
		for (int j=0;j<maxUser;j++) {
			for (int i=0;i<5;i++) {      
				m.add(new ResourceImpl("http://myexample.org/user" + j+i), new PropertyImpl("http://myexample.org/follows"), new ResourceImpl("http://myexample.org/user" + j));
			}
		}
		StringWriter sw = new StringWriter(); 
		m.write(sw, "RDF/XML");
		return sw.toString();
	}

	public static void main(String[] args) {
		System.out.println(dumpRelatedStaticKnowledge(10));
	}
}
