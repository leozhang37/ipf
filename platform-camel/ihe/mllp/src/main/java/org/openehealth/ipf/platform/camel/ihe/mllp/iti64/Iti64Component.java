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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti64;

import ca.uhn.hl7v2.ErrorCode;
import ca.uhn.hl7v2.Version;
import org.apache.camel.CamelContext;
import org.openehealth.ipf.gazelle.validation.profile.pixpdq.PixPdqTransactions;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2TransactionConfiguration;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.NakFactory;
import org.openehealth.ipf.commons.ihe.hl7v2.definitions.HapiContextFactory;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditStrategy;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTransactionComponent;

/**
 * Camel component for ITI-64 (XAD-PID Change Management - XPID).
 * @author Boris Stanojevic
 */
public class Iti64Component extends MllpTransactionComponent<Iti64AuditDataset> {
    public static final Hl7v2TransactionConfiguration CONFIGURATION =
        new Hl7v2TransactionConfiguration(
                new Version[] {Version.V25},
                "XPID adapter",
                "IPF",
                ErrorCode.APPLICATION_INTERNAL_ERROR,
                ErrorCode.APPLICATION_INTERNAL_ERROR,
                new String[] {"ADT"},
                new String[] {"A43"},
                new String[] {"ACK"},
                new String[] {"*"},
                new boolean[] {true},
                new boolean[] {false},
                HapiContextFactory.createHapiContext(PixPdqTransactions.ITI64));

    private static final MllpAuditStrategy<Iti64AuditDataset> CLIENT_AUDIT_STRATEGY = new Iti64AuditStrategy(false);
    private static final MllpAuditStrategy<Iti64AuditDataset> SERVER_AUDIT_STRATEGY = new Iti64AuditStrategy(true);
    private static final NakFactory NAK_FACTORY = new NakFactory(CONFIGURATION);


    public Iti64Component() {
        super();
    }

    public Iti64Component(CamelContext camelContext) {
        super(camelContext);
    }
    
    @Override
    public MllpAuditStrategy<Iti64AuditDataset> getClientAuditStrategy() {
        return CLIENT_AUDIT_STRATEGY;
    }

    @Override
    public MllpAuditStrategy<Iti64AuditDataset> getServerAuditStrategy() {
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
}
