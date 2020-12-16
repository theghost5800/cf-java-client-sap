package com.sap.cloudfoundry.client.facade.adapters;

import java.util.List;

import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.immutables.value.Value;

import com.sap.cloudfoundry.client.facade.domain.CloudServiceOffering;
import com.sap.cloudfoundry.client.facade.domain.CloudServicePlan;
import com.sap.cloudfoundry.client.facade.domain.Derivable;
import com.sap.cloudfoundry.client.facade.domain.ImmutableCloudServiceOffering;

@Value.Immutable
public abstract class RawCloudServiceOffering extends RawCloudEntity<CloudServiceOffering> {

    public abstract ServiceOfferingResource getResource();

    public abstract List<Derivable<CloudServicePlan>> getServicePlans();

    @Override
    public CloudServiceOffering derive() {
        ServiceOfferingResource resource = getResource();
        return ImmutableCloudServiceOffering.builder()
                                            .metadata(parseResourceMetadata(resource))
                                            .name(resource.getName())
                                            .isActive(resource.getAvailable())
                                            .isBindable(resource.getBrokerCatalog()
                                                                .getFeatures()
                                                                .getBindable())
                                            .description(resource.getDescription())
                                            .isShareable(resource.getShareable())
                                            .extra(resource.getBrokerCatalog()
                                                           .getMetadata())
                                            .docUrl(resource.getDocumentationUrl())
                                            .brokerId(resource.getRelationships()
                                                              .getServiceBroker()
                                                              .getData()
                                                              .getId())
                                            .uniqueId(resource.getBrokerCatalog()
                                                              .getBrokerCatalogId())
                                            .servicePlans(derive(getServicePlans()))
                                            .build();
    }

}
