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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml;

import static org.apache.commons.lang.Validate.notNull;
import static org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Vocabulary.*;

import java.util.ArrayList;
import java.util.List;

import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLClassification;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLFactory;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLObjectLibrary;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLRegistryPackage;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Recipient;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.SubmissionSet;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.RecipientTransformer;

/**
 * Transforms between a {@link SubmissionSet} and its ebXML representation.
 * @author Jens Riemschneider
 */
public class SubmissionSetTransformer extends XDSMetaClassTransformer<EbXMLRegistryPackage, SubmissionSet> {
    private final EbXMLFactory factory;
    
    private final AuthorTransformer authorTransformer;
    private final CodeTransformer codeTransformer;

    private final RecipientTransformer recipientTransformer = new RecipientTransformer();

    public SubmissionSetTransformer(EbXMLFactory factory) {
        super(SUBMISSION_SET_PATIENT_ID_EXTERNAL_ID,
                SUBMISSION_SET_LOCALIZED_STRING_PATIENT_ID,
                SUBMISSION_SET_UNIQUE_ID_EXTERNAL_ID,
                SUBMISSION_SET_LOCALIZED_STRING_UNIQUE_ID);
        
        notNull(factory, "factory cannot be null");

        this.factory = factory;
        authorTransformer = new AuthorTransformer(factory);
        codeTransformer = new CodeTransformer(factory);
    }

    @Override
    protected EbXMLRegistryPackage createEbXMLInstance(String id, EbXMLObjectLibrary objectLibrary) {
        return factory.createRegistryPackage(id, objectLibrary);
    }

    @Override
    protected SubmissionSet createMetaClassInstance() {
        return new SubmissionSet();
    }
    
    @Override
    protected void addAttributes(SubmissionSet metaData, EbXMLRegistryPackage ebXML, EbXMLObjectLibrary objectLibrary) {
        super.addAttributes(metaData, ebXML, objectLibrary);
        ebXML.setHome(metaData.getHomeCommunityId());
    }
    
    @Override
    protected void addAttributesFromEbXML(SubmissionSet metaData, EbXMLRegistryPackage ebXML) {
        super.addAttributesFromEbXML(metaData, ebXML);
        metaData.setHomeCommunityId(ebXML.getHome());
    }

    @Override
    protected void addSlots(SubmissionSet metaData, EbXMLRegistryPackage ebXML, EbXMLObjectLibrary objectLibrary) {
        super.addSlots(metaData, ebXML, objectLibrary);
        
        List<String> slotValues = new ArrayList<String>();
        for (Recipient recipient : metaData.getIntendedRecipients()) {
            slotValues.add(recipientTransformer.toEbXML(recipient));
        }
        ebXML.addSlot(SLOT_NAME_INTENDED_RECIPIENT, slotValues.toArray(new String[0]));
        
        ebXML.addSlot(SLOT_NAME_SUBMISSION_TIME, metaData.getSubmissionTime());        
    }
    
    @Override
    protected void addSlotsFromEbXML(SubmissionSet metaData, EbXMLRegistryPackage ebXML) {
        super.addSlotsFromEbXML(metaData, ebXML);
        
        List<Recipient> recipients = metaData.getIntendedRecipients();
        for (String slotValue : ebXML.getSlotValues(SLOT_NAME_INTENDED_RECIPIENT)) {
            recipients.add(recipientTransformer.fromEbXML(slotValue));
        }

        metaData.setSubmissionTime(ebXML.getSingleSlotValue(SLOT_NAME_SUBMISSION_TIME));
    }
    
    @Override
    protected void addClassificationsFromEbXML(SubmissionSet set, EbXMLRegistryPackage regPackage) {
        super.addClassificationsFromEbXML(set, regPackage);
        
        EbXMLClassification author = regPackage.getSingleClassification(SUBMISSION_SET_AUTHOR_CLASS_SCHEME);
        set.setAuthor(authorTransformer.fromEbXML(author));
 
        EbXMLClassification contentType = regPackage.getSingleClassification(SUBMISSION_SET_CONTENT_TYPE_CODE_CLASS_SCHEME);
        set.setContentTypeCode(codeTransformer.fromEbXML(contentType));
    }
    
    @Override
    protected void addClassifications(SubmissionSet set, EbXMLRegistryPackage regPackage, EbXMLObjectLibrary objectLibrary) {
        super.addClassifications(set, regPackage, objectLibrary);
        
        EbXMLClassification author = authorTransformer.toEbXML(set.getAuthor(), objectLibrary);
        regPackage.addClassification(author, SUBMISSION_SET_AUTHOR_CLASS_SCHEME);

        EbXMLClassification contentType = codeTransformer.toEbXML(set.getContentTypeCode(), objectLibrary);
        regPackage.addClassification(contentType, SUBMISSION_SET_CONTENT_TYPE_CODE_CLASS_SCHEME);
    }
    
    @Override
    protected void addExternalIdentifiers(SubmissionSet metaData, EbXMLRegistryPackage ebXML, EbXMLObjectLibrary objectLibrary) {
        super.addExternalIdentifiers(metaData, ebXML, objectLibrary);

        ebXML.addExternalIdentifier(metaData.getSourceID(), 
                SUBMISSION_SET_SOURCE_ID_EXTERNAL_ID,
                SUBMISSION_SET_LOCALIZED_STRING_SOURCE_ID);
    }
    
    @Override
    protected void addExternalIdentifiersFromEbXML(SubmissionSet metaData, EbXMLRegistryPackage ebXML) {
        super.addExternalIdentifiersFromEbXML(metaData, ebXML);

        String sourceID = ebXML.getExternalIdentifierValue(SUBMISSION_SET_SOURCE_ID_EXTERNAL_ID);
        metaData.setSourceID(sourceID);
    }
}
