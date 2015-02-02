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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti9;

import java.util.Collections;
import java.util.List;

import ca.uhn.hl7v2.ErrorCode;
import ca.uhn.hl7v2.Version;
import org.apache.camel.CamelContext;
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.CustomModelClassUtils;
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.HapiContextFactory;
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2TransactionConfiguration;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.NakFactory;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.Hl7v2Interceptor;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.consumer.ConsumerSegmentEchoingInterceptor;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTransactionComponent;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.QpdAwareNakFactory;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.QueryAuditDataset;

/**
 * Camel component for ITI-9 (PIX Query).
 *
 * @author Dmytro Rud
 */
public class Iti9Component extends MllpTransactionComponent<QueryAuditDataset> {
    public static final Hl7v2TransactionConfiguration CONFIGURATION =
            new Hl7v2TransactionConfiguration(
                    new Version[] {Version.V25},
                    "PIX adapter",
                    "IPF",
                    ErrorCode.APPLICATION_INTERNAL_ERROR,
                    ErrorCode.APPLICATION_INTERNAL_ERROR,
                    new String[]{"QBP"},
                    new String[]{"Q23"},
                    new String[]{"RSP"},
                    new String[]{"K23"},
                    new boolean[]{true},
                    new boolean[]{false},
                    HapiContextFactory.createHapiContext(
                            CustomModelClassUtils.createFactory("pix", "2.5"),
                            PixPdqTransactions.ITI9));

    private static final MllpAuditStrategy<QueryAuditDataset> CLIENT_AUDIT_STRATEGY =
            new Iti9ClientAuditStrategy();
    private static final MllpAuditStrategy<QueryAuditDataset> SERVER_AUDIT_STRATEGY =
            new Iti9ServerAuditStrategy();
    private static final NakFactory NAK_FACTORY =
            new QpdAwareNakFactory(CONFIGURATION, "RSP", "K23");


    public Iti9Component() {
        super();
    }

    public Iti9Component(CamelContext camelContext) {
        super(camelContext);
    }

    @Override
    public MllpAuditStrategy<QueryAuditDataset> getClientAuditStrategy() {
        return CLIENT_AUDIT_STRATEGY;
    }

    @Override
    public MllpAuditStrategy<QueryAuditDataset> getServerAuditStrategy() {
        return SERVER_AUDIT_STRATEGY;
    }

    @Override
    public Hl7v2TransactionConfiguration getHl7v2TransactionConfiguration() {
        return CONFIGURATION;
    }

    @Override
    public NakFactory getNakFactory() {
        return NAK_FACTORY;
    }

    @Override
    public List<Hl7v2Interceptor> getAdditionalConsumerInterceptors() {
        return Collections.<Hl7v2Interceptor>singletonList(new ConsumerSegmentEchoingInterceptor("QPD"));
    }
}
