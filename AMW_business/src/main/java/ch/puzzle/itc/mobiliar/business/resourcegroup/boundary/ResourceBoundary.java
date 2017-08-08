/*
 * AMW - Automated Middleware allows you to manage the configurations of
 * your Java EE applications on an unlimited number of different environments
 * with various versions, including the automated deployment of those apps.
 * Copyright (C) 2013-2016 by Puzzle ITC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.puzzle.itc.mobiliar.business.resourcegroup.boundary;

import ch.puzzle.itc.mobiliar.business.domain.commons.CommonDomainService;
import ch.puzzle.itc.mobiliar.business.environment.control.ContextDomainService;
import ch.puzzle.itc.mobiliar.business.foreignable.entity.ForeignableOwner;
import ch.puzzle.itc.mobiliar.business.releasing.boundary.ReleaseLocator;
import ch.puzzle.itc.mobiliar.business.releasing.control.ReleaseMgmtPersistenceService;
import ch.puzzle.itc.mobiliar.business.releasing.entity.ReleaseEntity;
import ch.puzzle.itc.mobiliar.business.resourcegroup.control.ResourceGroupRepository;
import ch.puzzle.itc.mobiliar.business.resourcegroup.control.ResourceTypeProvider;
import ch.puzzle.itc.mobiliar.business.resourcegroup.control.ResourceTypeRepository;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.*;
import ch.puzzle.itc.mobiliar.business.security.boundary.PermissionBoundary;
import ch.puzzle.itc.mobiliar.business.security.entity.Action;
import ch.puzzle.itc.mobiliar.business.security.entity.Permission;
import ch.puzzle.itc.mobiliar.business.security.interceptor.HasPermission;
import ch.puzzle.itc.mobiliar.common.exception.ElementAlreadyExistsException;
import ch.puzzle.itc.mobiliar.common.exception.NotAuthorizedException;
import ch.puzzle.itc.mobiliar.common.exception.ResourceNotFoundException;
import ch.puzzle.itc.mobiliar.common.exception.ResourceTypeNotFoundException;
import ch.puzzle.itc.mobiliar.common.util.DefaultResourceTypeDefinition;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ResourceBoundary {

    @Inject
    private CommonDomainService commonService;

    @Inject
    private ContextDomainService contextDomainService;

    @Inject
    private ResourceTypeProvider resourceTypeProvider;

    @Inject
    private EntityManager entityManager;

    @Inject
    private Logger log;

    @Inject
    private PermissionBoundary permissionBoundary;

    @Inject
    private ReleaseLocator releaseLocator;

    @Inject
    private ReleaseMgmtPersistenceService releaseService;

    @Inject
    private ResourceGroupRepository resourceGroupRepository;

    @Inject
    ResourceTypeRepository resourceTypeRepository;

    /**
     * Create new instance resourceType (any resourceType)
     */
    public Resource createNewResourceByName(ForeignableOwner creatingOwner, String newResourceName, String resourceTypeName,
                                            String releaseName)
            throws ResourceNotFoundException, ResourceTypeNotFoundException, ElementAlreadyExistsException {
        ResourceTypeEntity resourceTypeEntity = resourceTypeRepository.getByName(resourceTypeName);
        if (resourceTypeEntity == null) {
            String message = "ResourceType '" + resourceTypeName + "' existiert nicht";
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
        ReleaseEntity release;
        try {
            release = releaseLocator.getReleaseByName(releaseName);
        } catch (Exception e)  {
            String message = "Release '" + releaseName + "' existiert nicht";
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
        return createNewResourceByName(creatingOwner, newResourceName, resourceTypeEntity, release.getId(), true);
    }

    /**
     * Create new instance resourceType (any resourceType)
     */
    public Resource createNewResourceByName(ForeignableOwner creatingOwner, String newResourceName, Integer resourceTypeId,
                                            Integer releaseId)
            throws ResourceTypeNotFoundException, ElementAlreadyExistsException {
        ResourceTypeEntity resourceTypeEntity = commonService.getResourceTypeEntityById(resourceTypeId);
        return createNewResourceByName(creatingOwner, newResourceName, resourceTypeEntity, releaseId, false);
    }

    private Resource createNewResourceByName(ForeignableOwner creatingOwner, String newResourceName,
                                             ResourceTypeEntity resourceTypeEntity, Integer releaseId, boolean canCreateReleaseOfExisting)
            throws ElementAlreadyExistsException, ResourceTypeNotFoundException {
        if (!permissionBoundary.canCreateResourceInstance(resourceTypeEntity)){
            throw new NotAuthorizedException("Permission Denied");
        }
        ResourceEntity resourceEntity = createResourceEntityByNameForResourceType(creatingOwner, newResourceName,
                resourceTypeEntity.getId(), releaseId, canCreateReleaseOfExisting);
        Resource resource = Resource.createByResource(creatingOwner, resourceEntity, resourceTypeEntity,
                contextDomainService.getGlobalResourceContextEntity());
        entityManager.persist(resource.getEntity());
        entityManager.flush();
        log.info("Neue Resource " + newResourceName + "in DB persistiert");
        return resource;
    }

    /**
     * Creates a new resource for the given name, type and release
     *
     * @param newResourceName
     * @param resourceTypeId
     * @param releaseId
     * @param canCreateReleaseOfExisting if true, resource will be created if resources with same name in other releases exist. if false an ElementAlreadyExistsException will thrown in that case
     * @return new created ResourceEntity
     * @throws ElementAlreadyExistsException
     * @throws ResourceTypeNotFoundException
     */
    private ResourceEntity createResourceEntityByNameForResourceType(ForeignableOwner creatingOwner, String newResourceName, Integer resourceTypeId, int releaseId, boolean canCreateReleaseOfExisting) throws ElementAlreadyExistsException,
            ResourceTypeNotFoundException {
        ReleaseEntity release = releaseService.getById(releaseId);
        ResourceTypeEntity type = commonService.getResourceTypeEntityById(resourceTypeId);
        ResourceGroupEntity group = resourceGroupRepository.loadUniqueGroupByNameAndType(newResourceName,
                resourceTypeId);
        ResourceEntity resourceEntity = null;
        if (group == null) {
            // if group does not exists a new resource with a new group can be created
            resourceEntity = ResourceFactory.createNewResourceForOwner(newResourceName, creatingOwner);
            log.info("Neue Resource " + newResourceName + "inkl. Gruppe erstellt für Release "
                    + release.getName());
        }
        else if(canCreateReleaseOfExisting)  {
            // check if group contains resource for release
            for (ResourceEntity r : group.getResources()) {
                if (r.getRelease().getId().equals(releaseId)) {
                    resourceEntity = r;
                    break;
                }
            }
            if (resourceEntity == null) {
                // if resource is null, a new resource for the existing group can be created
                resourceEntity = ResourceFactory.createNewResourceForOwner(group, creatingOwner);
                log.info("Neue Resource " + newResourceName
                        + "für existierende Gruppe erstellt für Release " + release.getName());
            }
            else {
                // if resource with given name, type and release already exists throw an exeption
                String message = "The "+type.getName()+" with name: " + newResourceName
                        + " already exists in release "+release.getName();
                log.info(message);
                throw new ElementAlreadyExistsException(message, Resource.class, newResourceName);
            }
        }else{
            // if it is not allowed to create a new release for existing throw an exception
            String message = "The "+type.getName()+" with name: " + newResourceName
                    + " already exists.";
            log.info(message);
            throw new ElementAlreadyExistsException(message, Resource.class, newResourceName);
        }
        resourceEntity.setRelease(release);
        return resourceEntity;
    }

    /**
     * @param applicationName
     * @param releaseId
     * @param canCreateReleaseOfExisting if true, resource will be created if resources with same name in other releases exist. if false an ElementAlreadyExistsException will thrown in that case
     * @return
     * @throws ResourceTypeNotFoundException
     * @throws ElementAlreadyExistsException
     */
    private Application createUniqueApplicationByName(ForeignableOwner creatingOwner,String applicationName, int releaseId, boolean canCreateReleaseOfExisting)
            throws ResourceTypeNotFoundException, ElementAlreadyExistsException {
        ResourceTypeEntity resourceTypeEntity = resourceTypeProvider
                .getOrCreateDefaultResourceType(DefaultResourceTypeDefinition.APPLICATION);
        ResourceEntity resourceEntity = createResourceEntityByNameForResourceType(creatingOwner, applicationName,
                resourceTypeEntity.getId(), releaseId, canCreateReleaseOfExisting);
        Application application = Application.createByResource(resourceEntity, resourceTypeProvider,
                contextDomainService.getGlobalResourceContextEntity());
        return application;
    }

    /**
     * Creates a new Application for given ApplicationServer in the given release.<br>
     * If ApplicationServer does not exist it will be also created
     *
     * @param applicationName
     * @param appReleaseId
     * @return created application
     * @throws ElementAlreadyExistsException
     * @throws ResourceTypeNotFoundException
     */
    public Application createNewUniqueApplicationForAppServer(ForeignableOwner creatingOwner, String applicationName, Integer asGroupId, Integer appReleaseId, Integer asReleaseId)
            throws ElementAlreadyExistsException, ResourceNotFoundException, ResourceTypeNotFoundException {

        ResourceEntity asResource = commonService.getResourceEntityByGroupAndRelease(asGroupId, asReleaseId);

        if(!permissionBoundary.canCreateAppAndAddToAppServer(asResource)){
            throw new NotAuthorizedException("Missing Permission");
        }

        ApplicationServer as = ApplicationServer.createByResource(asResource, resourceTypeProvider,
                contextDomainService.getGlobalResourceContextEntity());
        Application app = createUniqueApplicationByName(creatingOwner, applicationName, appReleaseId, false);
        as.addApplication(app, creatingOwner);

        entityManager.persist(as.getEntity());
        entityManager.flush();
        log.info("Application " + applicationName + " für ApplicationServer " + asResource.getName()
                + " in DB persistiert");
        return app;
    }

    /**
     * Creates an application for the special applicationserver "Applications without application server"
     *
     * @param creatingOwner
     * @param fceKey
     * @param fceLink
     * @param applicationName
     * @param releaseId
     * @param canCreateReleaseOfExisting if true, resource will be created if resources with same name in other releases exist. if false an ElementAlreadyExistsException will thrown in that case
     * @return created application
     * @throws ElementAlreadyExistsException
     * @throws ResourceTypeNotFoundException
     */
    @HasPermission(permission = Permission.RESOURCE, action = Action.CREATE)
    public Application createNewApplicationWithoutAppServerByName(ForeignableOwner creatingOwner, String fceKey, String fceLink, String applicationName, Integer releaseId, boolean canCreateReleaseOfExisting)
            throws ElementAlreadyExistsException, ResourceTypeNotFoundException {
        Application app = createUniqueApplicationByName(creatingOwner, applicationName, releaseId, canCreateReleaseOfExisting);
        app.getEntity().setExternalKey(fceKey);
        app.getEntity().setExternalLink(fceLink);
        ApplicationServer applicationCollectorGroup = commonService.createOrGetApplicationCollectorServer();
        applicationCollectorGroup.addApplication(app, creatingOwner);
        entityManager.persist(applicationCollectorGroup.getEntity());
        entityManager.flush();
        return app;
    }

    /**
     * Updates (merges) an existing ResourceEntity
     * used by MaiaAmwFederationServiceImportHandler
     *
     * @param resourceEntity
     */
    public void updateResource(ResourceEntity resourceEntity) {
        if (resourceEntity.getId() != null) {
            if (permissionBoundary.hasPermission(Permission.RESOURCE, null, Action.UPDATE, resourceEntity, resourceEntity.getResourceType())) {
                entityManager.merge(resourceEntity);
            }
        }
    }

}