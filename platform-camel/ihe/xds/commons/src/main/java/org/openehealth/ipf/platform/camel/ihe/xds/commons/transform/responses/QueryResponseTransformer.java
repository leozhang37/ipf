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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.responses;

import static org.apache.commons.lang.Validate.notNull;

import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLClassification;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLAssociation;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLQueryResponse;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLFactory;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLExtrinsicObject;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLObjectLibrary;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.EbXMLRegistryPackage;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Association;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.DocumentEntry;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Folder;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.ObjectReference;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.SubmissionSet;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Vocabulary;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.responses.QueryResponse;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.AssociationTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.DocumentEntryTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.FolderTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.SubmissionSetTransformer;

/**
 * Transforms between {@link QueryResponse} and the {@link EbXMLQueryResponse} representation.
 * @author Jens Riemschneider
 */
public class QueryResponseTransformer {    
    private final EbXMLFactory factory;
    private final SubmissionSetTransformer submissionSetTransformer;
    private final DocumentEntryTransformer documentEntryTransformer;
    private final FolderTransformer folderTransformer;
    private final AssociationTransformer associationTransformer;

    /**
     * Constructs the transformer.
     * @param factory
     *          the factory for ebXML objects.
     */
    public QueryResponseTransformer(EbXMLFactory factory) {
        notNull(factory, "factory cannot be null");
        this.factory = factory;
    
        submissionSetTransformer = new SubmissionSetTransformer(factory);
        documentEntryTransformer = new DocumentEntryTransformer(factory);
        folderTransformer = new FolderTransformer(factory);
        associationTransformer = new AssociationTransformer(factory);
    }

    /**
     * Transforms a {@link QueryResponse} to a {@link EbXMLQueryResponse}.
     * @param response
     *          the response.
     * @return the ebXML representation.
     */
    public EbXMLQueryResponse toEbXML(QueryResponse response) {
        if (response == null) {
            return null;
        }
        
        EbXMLObjectLibrary library = factory.createObjectLibrary();        
        EbXMLQueryResponse ebXML = factory.createAdhocQueryResponse(library);
        ebXML.setStatus(response.getStatus());
        ebXML.setErrors(response.getErrors());
        
        for (DocumentEntry docEntry : response.getDocumentEntries()) {
            ebXML.addExtrinsicObject(documentEntryTransformer.toEbXML(docEntry, library));
        }
        
        for (Folder folder : response.getFolders()) {
            ebXML.addRegistryPackage(folderTransformer.toEbXML(folder, library));
            addClassification(ebXML, folder.getEntryUUID(), Vocabulary.FOLDER_CLASS_NODE, library);
        }
        
        for (SubmissionSet set : response.getSubmissionSets()) {
            ebXML.addRegistryPackage(submissionSetTransformer.toEbXML(set, library));
            addClassification(ebXML, set.getEntryUUID(), Vocabulary.SUBMISSION_SET_CLASS_NODE, library);
        }
        
        for (Association association : response.getAssociations()) {
            ebXML.addAssociation(associationTransformer.toEbXML(association, library));
        }
        
        for (ObjectReference ref : response.getReferences()) {
            ebXML.addReference(ref);
        }
        
        return ebXML;
    }
    
    /**
     * Transforms a {@link EbXMLRegistryResponse} to a {@link Response}.
     * @param ebXML
     *          the ebXML representation.
     * @return the response.
     */
    public QueryResponse fromEbXML(EbXMLQueryResponse ebXML) {
        if (ebXML == null) {
            return null;
        }
        
        QueryResponse response = new QueryResponse();
        response.setStatus(ebXML.getStatus());
        response.getErrors().addAll(ebXML.getErrors());
        
        for (EbXMLExtrinsicObject extrinsic : ebXML.getExtrinsicObjects(Vocabulary.DOC_ENTRY_CLASS_NODE)) {
            response.getDocumentEntries().add(documentEntryTransformer.fromEbXML(extrinsic));
        }

        for (EbXMLRegistryPackage regPackage : ebXML.getRegistryPackages(Vocabulary.FOLDER_CLASS_NODE)) {
            response.getFolders().add(folderTransformer.fromEbXML(regPackage));
        }

        for (EbXMLRegistryPackage regPackage : ebXML.getRegistryPackages(Vocabulary.SUBMISSION_SET_CLASS_NODE)) {
            response.getSubmissionSets().add(submissionSetTransformer.fromEbXML(regPackage));
        }
        
        for (EbXMLAssociation association : ebXML.getAssociations()) {
            response.getAssociations().add(associationTransformer.fromEbXML(association));
        }
        
        return response;
    }

    private void addClassification(EbXMLQueryResponse ebXML, String classified, String node, EbXMLObjectLibrary library) {
        EbXMLClassification classification = factory.createClassification(library);
        classification.setClassifiedObject(classified);
        classification.setClassificationNode(node);
        ebXML.addClassification(classification);
    }    
}
