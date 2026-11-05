package com.acme.coldtrace.platform.billing.application.internal.services;

import com.acme.coldtrace.platform.billing.application.ports.AssetUsageReader;
import com.acme.coldtrace.platform.billing.application.model.OrganizationSubscriptionUsage;
import com.acme.coldtrace.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

/**
 * Builds organization usage snapshots for subscription entitlement evaluation.
 *
 * @since 1.0
 */
@Service
public class OrganizationSubscriptionUsageService {
    private final AssetUsageReader assetManagementContextFacade;
    private final IamContextFacade iamContextFacade;

    public OrganizationSubscriptionUsageService(
            AssetUsageReader assetManagementContextFacade,
            IamContextFacade iamContextFacade
    ) {
        this.assetManagementContextFacade = assetManagementContextFacade;
        this.iamContextFacade = iamContextFacade;
    }

    /**
     * Counts supported resources for the organization.
     *
     * @param organizationId organization identifier
     * @return current usage snapshot
     */
    public OrganizationSubscriptionUsage snapshotFor(Long organizationId) {
        var usage = assetManagementContextFacade.fetch(organizationId);
        return new OrganizationSubscriptionUsage(
                usage.locations(),
                usage.assets(),
                usage.iotDevices(),
                iamContextFacade.countUsersByOrganizationId(organizationId)
        );
    }
}
