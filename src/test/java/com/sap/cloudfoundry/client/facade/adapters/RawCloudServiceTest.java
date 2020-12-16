package com.sap.cloudfoundry.client.facade.adapters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.serviceInstances.ServiceInstanceResource;
import org.cloudfoundry.client.v3.serviceInstances.ServiceInstanceType;
import org.cloudfoundry.client.v3.serviceofferings.BrokerCatalog;
import org.cloudfoundry.client.v3.serviceofferings.Features;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingRelationships;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.junit.jupiter.api.Test;

import com.sap.cloudfoundry.client.facade.domain.CloudServiceInstance;
import com.sap.cloudfoundry.client.facade.domain.ImmutableCloudServiceInstance;

public class RawCloudServiceTest {

    private static final String NAME = "my-db";
    private static final String OFFERING_NAME = "postgresql";
    private static final String PLAN_NAME = "v9.4-small";
    private static final Map<String, Object> CREDENTIALS = buildTestCredentials();
    private static final List<String> TAGS = Arrays.asList("test-tag-1", "test-tag-2");

    @Test
    public void testDerive() {
        RawCloudEntityTest.testDerive(buildExpectedService(), buildRawService());
    }

    @Test
    public void testDeriveWithUserProvidedService() {
        RawCloudEntityTest.testDerive(buildExpectedUserProvidedService(), buildRawUserProvidedService());
    }

    private static CloudServiceInstance buildExpectedService() {
        return ImmutableCloudServiceInstance.builder()
                                            .metadata(RawCloudEntityTest.EXPECTED_METADATA_PARSED_FROM_V3_RESOURCE)
                                            .name(NAME)
                                            .plan(PLAN_NAME)
                                            .label(OFFERING_NAME)
                                            .type(ServiceInstanceType.MANAGED)
                                            .tags(TAGS)
                                            .build();
    }

    private static CloudServiceInstance buildExpectedUserProvidedService() {
        return ImmutableCloudServiceInstance.builder()
                                            .metadata(RawCloudEntityTest.EXPECTED_METADATA_PARSED_FROM_V3_RESOURCE)
                                            .name(NAME)
                                            .type(ServiceInstanceType.USER_PROVIDED)
                                            .credentials(CREDENTIALS)
                                            .tags(TAGS)
                                            .build();
    }

    private static RawCloudServiceInstance buildRawService() {
        return ImmutableRawCloudServiceInstance.builder()
                                               .resource(buildTestResource(false))
                                               .servicePlan(RawCloudServicePlanTest.buildTestServicePlan(PLAN_NAME))
                                               .serviceOffering(buildTestServiceOffering())
                                               .build();
    }

    private static RawCloudServiceInstance buildRawUserProvidedService() {
        return ImmutableRawCloudServiceInstance.builder()
                                               .resource(buildTestResource(true))
                                               .credentials(CREDENTIALS)
                                               .build();
    }

    private static ServiceInstanceResource buildTestResource(boolean isUserProvided) {
        ServiceInstanceResource.Builder serviceInstanceResourceBuilder = ServiceInstanceResource.builder()
                                                                                                .id(RawCloudEntityTest.GUID_STRING)
                                                                                                .createdAt(RawCloudEntityTest.CREATED_AT_STRING)
                                                                                                .updatedAt(RawCloudEntityTest.UPDATED_AT_STRING)
                                                                                                .name(NAME)
                                                                                                .type(ServiceInstanceType.MANAGED)
                                                                                                .addAllTags(TAGS);
        if (isUserProvided) {
            serviceInstanceResourceBuilder.type(ServiceInstanceType.USER_PROVIDED);
        }
        return serviceInstanceResourceBuilder.build();
    }

    private static ServiceOfferingResource buildTestServiceOffering() {
        return ServiceOfferingResource.builder()
                                      .id(RawCloudEntityTest.GUID_STRING)
                                      .createdAt(RawCloudEntityTest.CREATED_AT_STRING)
                                      .updatedAt(RawCloudEntityTest.UPDATED_AT_STRING)
                                      .available(RawCloudServiceOfferingTest.ACTIVE)
                                      .name(OFFERING_NAME)
                                      .brokerCatalog(BrokerCatalog.builder()
                                                                  .brokerCatalogId(RawCloudServiceOfferingTest.UNIQUE_ID)
                                                                  .features(Features.builder()
                                                                                    .bindable(RawCloudServiceOfferingTest.BINDABLE)
                                                                                    .allowContextUpdates(RawCloudServiceOfferingTest.ALLOW_CONTEXT_UPDATES)
                                                                                    .bindingsRetrievable(RawCloudServiceOfferingTest.BINDINGS_RETRIEVABLE)
                                                                                    .instancesRetrievable(RawCloudServiceOfferingTest.INSTANCES_RETRIEVABLE)
                                                                                    .planUpdateable(RawCloudServiceOfferingTest.PLAN_UPDATEABLE)
                                                                                    .build())
                                                                  .build())
                                      .relationships(ServiceOfferingRelationships.builder()
                                                                                 .serviceBroker(ToOneRelationship.builder()
                                                                                                                 .data(Relationship.builder()
                                                                                                                                   .id(RawCloudServiceOfferingTest.SERVICE_BROKER_GUID)
                                                                                                                                   .build())
                                                                                                                 .build())
                                                                                 .build())
                                      .shareable(RawCloudServiceOfferingTest.SHAREABLE)
                                      .description(RawCloudServiceOfferingTest.DESCRIPTION)
                                      .documentationUrl(RawCloudServiceOfferingTest.DOCUMENTATION_URL)
                                      .build();
    }

    private static Map<String, Object> buildTestCredentials() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("foo", "bar");
        parameters.put("baz", false);
        parameters.put("qux", 3.141);
        return parameters;
    }

}
