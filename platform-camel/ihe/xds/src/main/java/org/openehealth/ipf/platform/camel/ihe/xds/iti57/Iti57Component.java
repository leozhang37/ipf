/*
 * Copyright 2013 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.xds.iti57;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditStrategy;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.commons.ihe.xds.iti57.Iti57ClientAuditStrategy;
import org.openehealth.ipf.commons.ihe.xds.iti57.Iti57PortType;
import org.openehealth.ipf.commons.ihe.xds.iti57.Iti57ServerAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.SimpleWsProducer;
import org.openehealth.ipf.platform.camel.ihe.xds.XdsEndpoint;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * The Camel component for the ITI-57 transaction.
 */
public class Iti57Component extends AbstractWsComponent<WsTransactionConfiguration> {
    protected final static WsTransactionConfiguration WS_CONFIG = new WsTransactionConfiguration(
            new QName("urn:ihe:iti:xds-b:2010", "DocumentRegistry_Service", "ihe"),
            Iti57PortType.class,
            new QName("urn:ihe:iti:xds-b:2010", "DocumentRegistry_Binding_Soap12", "ihe"),
            false,
            "wsdl/iti57.wsdl",
            true,
            false,
            false,
            false);

    @Override
    @SuppressWarnings("unchecked") // Required because of base class
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        return new XdsEndpoint(uri, remaining, this,
                getCustomInterceptors(parameters),
                getFeatures(parameters),
                getSchemaLocations(parameters),
                getProperties(parameters));
    }

    @Override
    public WsTransactionConfiguration getWsTransactionConfiguration() {
        return WS_CONFIG;
    }

    @Override
    public WsAuditStrategy getClientAuditStrategy() {
        return new Iti57ClientAuditStrategy();
    }

    @Override
    public WsAuditStrategy getServerAuditStrategy() {
        return new Iti57ServerAuditStrategy();
    }

    @Override
    public Iti57Service getServiceInstance(AbstractWsEndpoint<?> endpoint) {
        return new Iti57Service();
    }

    @Override
    public SimpleWsProducer<SubmitObjectsRequest, RegistryResponseType> getProducer(
            AbstractWsEndpoint<?> endpoint,
            JaxWsClientFactory clientFactory)
    {
        return new SimpleWsProducer<>(
                endpoint, clientFactory, SubmitObjectsRequest.class,RegistryResponseType.class);
    }
}
