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
package org.openehealth.ipf.commons.ihe.pixpdqv3.translation

import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult
import org.openehealth.ipf.modules.hl7.message.MessageUtils;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter
import static org.openehealth.ipf.commons.ihe.pixpdqv3.translation.Utils.*

/**
 * PIX Query Response translator HL7 v2 to v3.
 * @author Marek V�clav�k, Dmytro Rud
 */
class PixQueryResponse2to3Translator implements Hl7TranslatorV2toV3 {

    /**
     * Predefined fix value of
     * <code>//registrationEvent/custodian/assignedEntity/id/@root</code>.
     * In productive environments to be set to the <code>id/@root</code>
     * of the device representing the MPI.
     */
    String mpiSystemIdRoot = '1.2.3'

    /**
     * Predefined fix value of
     * <code>//registrationEvent/custodian/assignedEntity/id/@extension</code>.
     * In productive environments to be set to the <code>id/@extension</code>
     * of the device representing the MPI.
     */
    String mpiSystemIdExtension = ''

    /**
     * Predefined fix value of <code>//acknowledgementDetail/code/@codeSystem</code>.
     */
    String errorCodeSystem = '2.16.840.1.113883.12.357'

    /**
     * <tt>root</tt> attribute of message's <tt>id</tt> element.
     */
    String messageIdRoot = '1.2.3'


    /**
     * Translates HL7 v2 response messages <tt>RSP^K23</tt> and <tt>ACK</tt> 
     * into HL7 v3 message <tt>PRPA_IN201310UV02</tt>.
     */
    String translateV2toV3(MessageAdapter rsp, String origMessage) {
        def writer  = new StringWriter()
        def builder = getBuilder(writer)

        def xml = slurp(origMessage)

        def messageTimestamp = dropTimeZone(rsp.MSH[7][1].value) ?: MessageUtils.hl7Now()
        def status = getStatusInformation(rsp, xml)
        
        builder.PRPA_IN201310UV02(
                'ITSVersion': 'XML_1.0',
                'xmlns':      'urn:hl7-org:v3',
                'xmlns:xsi':  'http://www.w3.org/2001/XMLSchema-instance',
                'xmlns:xsd':  'http://www.w3.org/2001/XMLSchema') 
        {
            buildInstanceIdentifier(builder, 'id', false, this.messageIdRoot, rsp.MSH[10].value)
            creationTime(value: messageTimestamp)
            interactionId(root: '2.16.840.1.113883.1.6', extension: 'PRPA_IN201310UV02')
            processingCode(code: 'P')
            processingModeCode(code: 'T')
            acceptAckCode(code: 'NE')
            buildReceiverAndSender(builder, xml, 'urn:hl7-org:v3')
            createQueryAcknowledgementElement(builder, xml, status, this.errorCodeSystem) 

            controlActProcess(classCode: 'CACT', moodCode: 'EVN') {
                code(code: 'PRPA_TE201310UV02', codeSystem: '2.16.840.1.113883.1.6')
                effectiveTime(value: messageTimestamp)
                
                if (rsp.MSH[9][1].value == 'RSP') {
					def pid3collection = rsp.QUERY_RESPONSE.PID[3]()
                    if (pid3collection) {
                        subject(typeCode: 'SUBJ') {
                            registrationEvent(classCode: 'REG', moodCode: 'EVN') {
                                statusCode(code: 'active') {
                                    subject1(typeCode: 'SBJ') {
                                        patient(classCode: 'PAT') {
                                            for(pid3 in pid3collection) {   
                                                buildInstanceIdentifier(builder, 'id', false, 
                                                        pid3[4][2].value, pid3[1].value, pid3[4][1].value) 
                                            }
											statusCode(code: 'active')
										}
									}
									createCustodian(builder, this.mpiSystemIdRoot, this.mpiSystemIdExtension)
                                }
                            }                            
                        }
				    }
				}
                
                queryAck {
                    buildInstanceIdentifier(builder, 'queryId', false, 
                            xml.id.@root.text(), xml.id.@extension.text())
                    queryResponseCode(code: status.responseStatus)
                }
                XmlYielder.yieldElement(xml.controlActProcess.queryByParameter, builder, 'urn:hl7-org:v3')
            }
        }

        return writer.toString()
    }

     
    private Map getStatusInformation(MessageAdapter rsp, GPathResult xml) {
        def responseStatus = 'OK'
        def errorText      = ''
        def errorCode      = ''
        def errorLocations = []
        def ackCode        = rsp.MSA[1].value
        
        responseStatus = ackCode
        errorCode = rsp.ERR[3][1].value ?: ''
        errorText = "PIXv2 Interface Reported [${rsp.ERR[6].value ?: ''} ${rsp.ERR[7].value ?: ''} ${rsp.MSA[3].value ?: ''}]"

        // collect error locations
        errorLocations = rsp.ERR[2]()?.collect { err ->
            if ((err[1].value == 'QPD') && (err[2].value == '1') && (err[3].value == '4')) {
                return '/' + xml.interactionId.@extension.text() +
                       '/controlActProcess/queryByParameter/parameterList/dataSource' +
                       (err[4].value ? ('[' + (err[4].value + 1 ) + ']') : '') + '/value'
            }
            return '/'
        }
        
        return [ackCode:        ackCode, 
                errorText:      errorText, 
                errorCode:      errorCode, 
                errorLocations: errorLocations, 
                responseStatus: responseStatus]
        
    }
}