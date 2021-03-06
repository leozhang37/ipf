/*
 * Copyright 2009 InterComponentWare AG.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.core

import ca.uhn.hl7v2.model.Message
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultExchange
import org.junit.After
import org.junit.AfterClass
import org.openehealth.ipf.commons.ihe.core.atna.MockedSender
import org.openehealth.ipf.platform.camel.core.util.Exchanges
import org.openhealthtools.ihe.atna.auditor.context.AuditorModuleContext
import org.springframework.context.support.ClassPathXmlApplicationContext

import static org.junit.Assert.*

/**
 * Generic Unit Test container for MLLP components.
 * 
 * @author Dmytro Rud
 */
class MllpTestContainer {

    static ProducerTemplate producerTemplate
    static CamelContext camelContext
    static MockedSender auditSender
    static ClassPathXmlApplicationContext appContext

    static String TIMEOUT = '300000'
    
    /**
     * Initializes a test on the basis of a Spring descriptor.
     */
    static void init(String descriptorFile, boolean standalone) {
        appContext = new ClassPathXmlApplicationContext(descriptorFile)
        producerTemplate = appContext.getBean('template', ProducerTemplate.class)
        camelContext = appContext.getBean('camelContext', CamelContext.class)
        
        auditSender = new MockedSender()
        AuditorModuleContext.context.sender = auditSender
        AuditorModuleContext.context.config.auditRepositoryHost = 'localhost'
        AuditorModuleContext.context.config.auditRepositoryPort = 514
        AuditorModuleContext.context.config.auditSourceId = 'audit-source-id'
        AuditorModuleContext.context.config.auditEnterpriseSiteId = 'audit-enterprise-site-id'

        if (standalone) {
            Thread.currentThread().join()
        }
    }
    
    @After
    void tearDown() {
        auditSender?.messages.clear()
    }
    
    @AfterClass
    static void tearDownAfterClass() {
        appContext?.destroy()
    }
    
    
    /**
     * Checks whether the message represents a (positive) ACK.
     */
    static void assertACK(Message msg) {
        assertTrue(msg.MSH[9][1].value == 'ACK')
        assertFalse(msg.MSA[1].value[1] in ['R', 'E'])
    }
    
    
    /**
     * Checks whether the message represents a positive ReSPonse.
     */
    static void assertRSP(Message msg) {
        assertTrue(msg.MSH[9][1].value == 'RSP')
        assertFalse(msg.MSA[1].value[1] in ['R', 'E'])
    }
    
    
    /**
     * Checks whether the message represents a NAK.
     */
    static void assertNAK(Message msg) {
        assertTrue(msg.MSH[9][1].value == 'ACK')
        assertTrue(msg.MSA[1].value[1] in ['R', 'E'])
        assertNotNull(msg.ERR)
    }
    
    /**
     * Checks whether the message represents a NAK with segments QPD and QAK.
     */
    static void assertNAKwithQPD(Message msg, String messageType, String triggerEvent) {
        assertTrue(msg.MSH[9][1].value == messageType)
        assertTrue(msg.MSH[9][2].value == triggerEvent)
        assertTrue(msg.MSA[1].value[1] in ['R', 'E'])
        assertNotNull(msg.ERR)
        assertNotNull(msg.QAK)
        assertNotNull(msg.QPD)
    }
    
    /**
     * Sends a request into the route.
     */
    static Message send(String endpoint, Object body) {
        def exchange = new DefaultExchange(camelContext)
        exchange.in.body = body

        // Because during test shutdown there is a race condition between shutting down
        // the consumer and closing the sessions, we close the session right after use
        // to avoid ugly exceptions in the log.
        /*
        if (!endpoint.contains('disconnect')) {
            if (endpoint.contains('?')) {
                endpoint += '&disconnect=true'
            } else {
                endpoint += '?disconnect=true'
            }
        }
        */

        Exchange result = producerTemplate.send(endpoint, exchange)
        if (result.exception) {
            throw result.exception
        }
        def response = Exchanges.resultMessage(result)
        response.getBody(Message.class)
    }
    
    
    /**
     * Returns a sample HL7 message as String. 
     */
    static String getMessageString(String msh9, String msh12, boolean needPid = true) {
        def s = 'MSH|^~\\&|MESA_PD_SUPPLIER|XYZ_HOSPITAL|dummy|dummy|20081204114742||' +
                msh9 +
                '|123456|T|' +
                msh12 +
                '|||ER\n' +
                'EVN|A01|20081204114742\n'
        if(needPid) {
            s = s + 'PID|1||001^^^XREF2005~002^^^HIMSS2005||Multiple^Christof^Maria^Prof.^^^L|Eisner^^^^^^B|' +
                    '19530429|M|||Bahnhofstr. 1^^Testort^^01234^DE^H|||||||AccNr01^^^ANICPA|' +
                    '111-222-333|\n'
        }
        s = s + 'PV1|1|O|\n'
        return s
    }

    /**
     * Returns a sample HL7 message as String. This message is substantially different
     */
    static String getMessageString10(String msh9, String msh12, boolean needPid = true) {
        def s = 'MSH|^~\\&|MESA_PD_SUPPLIER|XYZ_HOSPITAL|dummy|dummy|20081204114742||' +
                msh9 +
                '|123456|T|' +
                msh12 +
                '|||ER\n' +
                'EVN|A31|20081204114742\n'
        if(needPid) {
            s = s + 'PID|||001^^^XREF2005&1.2.3&ISO~002^^^HIMSS2005&1.2.3&ISO||Multiple^Christof^Maria^Prof.^^^L||\n'
        }
        s = s + 'PV1||N|\n'
        return s
    }
}
