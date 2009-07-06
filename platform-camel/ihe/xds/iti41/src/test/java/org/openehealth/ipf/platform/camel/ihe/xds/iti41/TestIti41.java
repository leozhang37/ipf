/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.xds.iti41;

import static junit.framework.Assert.assertEquals;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.StandardTestContainer;

import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.AssigningAuthority;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Document;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.DocumentEntry;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Identifiable;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.LocalizedString;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.SubmissionSet;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.responses.Response;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.responses.Status;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.utils.LargeDataSource;
import javax.activation.DataHandler;

/**
 * Tests the ITI-41 transaction with a webservice and client adapter defined via URIs.
 * @author Jens Riemschneider
 */
public class TestIti41 extends StandardTestContainer {
    private static final String SERVICE1 = "xds-iti41://localhost:9091/xds-iti41-service1";
    private static final String SERVICE2 = "xds-iti41://localhost:9091/xds-iti41-service2";
    
    private ProvideAndRegisterDocumentSet request;
    private DocumentEntry docEntry;

    @BeforeClass
    public static void classSetUp() throws Exception {
        startServer(new CXFServlet(), "iti-41.xml");
        installTestInterceptors(Iti41TestAuditFinalInterceptor.class);
    }
    
    @Before
    public void setUp() {
        Identifiable patientID = new Identifiable("patient-id", new AssigningAuthority("1.2.3.4.5"));
        
        SubmissionSet submissionSet = new SubmissionSet();
        submissionSet.setPatientID(patientID);
        submissionSet.setUniqueID("229.6.58.29.24.1235");

        docEntry = new DocumentEntry();
        docEntry.setPatientID(patientID);
        docEntry.setComments(new LocalizedString("service 1"));
        docEntry.setEntryUUID("document 01");
        
        Document doc = new Document();
        doc.setDocumentEntry(docEntry);
        doc.setDataHandler(new DataHandler(new LargeDataSource()));
        
        request = new ProvideAndRegisterDocumentSet();
        request.setSubmissionSet(submissionSet);
        request.getDocuments().add(doc);
    }
    
    /** Calls the route attached to the ITI-41 endpoint. */
    @Test
    public void testIti41() {
        Response response1 = send(SERVICE1, "service 1");
        assertEquals(Status.SUCCESS, response1.getStatus());

        Response response2 = send(SERVICE2, "service 2");
        assertEquals(Status.SUCCESS, response2.getStatus());
    }

    private Response send(String endpoint, String value) {
        docEntry.setComments(new LocalizedString(value));
        return send(endpoint, request, Response.class);
    }
}