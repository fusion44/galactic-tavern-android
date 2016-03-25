package me.stammberger.galactictavern.stores;

import me.stammberger.galactictavern.models.orgs.Organization;

public interface OrganizationStoreInterface {
    /**
     * Searches for an organization using its id
     *
     * @return {@link Organization} object with organization data. Null if org with this id was not found
     */
    Organization getOrganization(String id);
}
