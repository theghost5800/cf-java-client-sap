package com.sap.cloudfoundry.client.facade.adapters;

import java.util.Optional;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBroker;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerRelationships;
import org.immutables.value.Value;

import com.sap.cloudfoundry.client.facade.domain.CloudServiceBroker;
import com.sap.cloudfoundry.client.facade.domain.ImmutableCloudServiceBroker;

@Value.Immutable
public abstract class RawCloudServiceBroker extends RawCloudEntity<CloudServiceBroker> {

    @Value.Parameter
    public abstract ServiceBroker getResource();

    @Override
    public CloudServiceBroker derive() {
        ServiceBroker resource = getResource();
        String spaceGuid = getSpaceGuid(resource);
        return ImmutableCloudServiceBroker.builder()
                                          .metadata(parseResourceMetadata(resource))
                                          .name(resource.getName())
                                          .url(resource.getUrl())
                                          .spaceGuid(spaceGuid)
                                          .build();
    }

    private String getSpaceGuid(ServiceBroker resource) {
        return Optional.ofNullable(resource.getRelationships())
                       .map(ServiceBrokerRelationships::getSpace)
                       .map(ToOneRelationship::getData)
                       .map(Relationship::getId)
                       .orElse(null);
    }

}
