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
package org.openehealth.ipf.platform.camel.ihe.mllp.core;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.component.mina2.Mina2Component;
import org.apache.camel.component.mina2.Mina2Endpoint;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2ConfigurationHolder;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.Hl7v2Interceptor;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.consumer.ConsumerAdaptingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic Camel component for MLLP.
 * @author Dmytro Rud
 */
public abstract class MllpComponent<ConfigType extends MllpEndpointConfiguration>
        extends Mina2Component implements Hl7v2ConfigurationHolder
{
    private static final transient Logger LOG = LoggerFactory.getLogger(MllpComponent.class);
    
    public static final String ACK_TYPE_CODE_HEADER = ConsumerAdaptingInterceptor.ACK_TYPE_CODE_HEADER;
    
    private static final String DEFAULT_HL7_CODEC_FACTORY_BEAN_NAME = "#hl7codec";

    protected MllpComponent() {
        super();
    }

    /**
     * Camel context-based constructor.
     *
     * @param camelContext camel context
     */
    protected MllpComponent(CamelContext camelContext) {
        super(camelContext);
    }


    /**
     * Creates a configuration object.
     * @param parameters
     *      URL parameters.
     * @return
     *      configuration object filled with values from the provided parameter map.
     */
    protected abstract ConfigType createConfig(Map<String, Object> parameters) throws Exception;


    /**
     * Creates an endpoint object.
     * @param wrappedEndpoint
     *      standard Camel MINA2 endpoint instance.
     * @param config
     *      endpoint configuration.
     * @return
     *      configured MLLP endpoint instance which wraps the MINA2 one.
     */
    protected abstract MllpEndpoint createEndpoint(Mina2Endpoint wrappedEndpoint, ConfigType config);


    /**
     * Creates and configures the endpoint.
     */
    @Override
    protected Endpoint createEndpoint(
            String uri,
            String remaining, 
            Map<String, Object> parameters) throws Exception
    {
        // explicitly overwrite some standard camel-mina parameters
        if (parameters == Collections.EMPTY_MAP) {
            parameters = new HashMap<>();
        }
        parameters.put("sync", true);
        parameters.put("lazySessionCreation", true);
        parameters.put("transferExchange", false);
        if (! parameters.containsKey("codec")) {
            parameters.put("codec", DEFAULT_HL7_CODEC_FACTORY_BEAN_NAME);
        }

        ConfigType config = createConfig(parameters);

        Charset charset = null;
        try {
            charset = ((HL7MLLPCodec) config.getCodecFactory()).getCharset();
        } catch(ClassCastException cce) {
            LOG.error("Unsupported HL7 codec factory type " + config.getCodecFactory().getClass().getName());
        }
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        parameters.put("encoding", charset.name());
        
        // construct the endpoint
        Endpoint endpoint = super.createEndpoint(uri, "tcp://" + remaining, parameters);
        Mina2Endpoint minaEndpoint = (Mina2Endpoint) endpoint;

        // wrap and return
        return createEndpoint(minaEndpoint, config);
    }


    /**
     * @return
     *      a list of component-specific (i.e. transaction-specific)
     *      HL7v2 interceptors which will be integrated into the interceptor
     *      chain of each consumer instance created by this component.
     *      <p>
     *      Per default returns an empty list.
     *      <br>
     *      When overwriting this method, please note:
     *      <ul>
     *          <li>Neither the returned list nor any element of it
     *              are allowed to be <code>null</code>.
     *          <li>Each invocation should return freshly created instances
     *              of interceptors (like prototype-scope beans in Spring),
     *              because interceptors cannot be reused by multiple endpoints.
     *      </ul>
     */
    public List<Hl7v2Interceptor> getAdditionalConsumerInterceptors() {
        return Collections.emptyList();
    }


    /**
     * @return
     *      a list of component-specific (i.e. transaction-specific)
     *      HL7v2 interceptors which will be integrated into the interceptor
     *      chain of each consumer instance created by this component.
     *      <p>
     *      Per default returns an empty list.
     *      <br>
     *      When overwriting this method, please note:
     *      <ul>
     *          <li>Neither the returned list nor any element of it
     *              are allowed to be <code>null</code>.
     *          <li>Each invocation should return freshly created instances
     *              of interceptors (like prototype-scope beans in Spring),
     *              because interceptors cannot be reused by multiple endpoints.
     *      </ul>
     */
    public List<Hl7v2Interceptor> getAdditionalProducerInterceptors() {
        return Collections.emptyList();
    }

}
