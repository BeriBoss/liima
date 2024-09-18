package ch.puzzle.itc.mobiliar.business.resourcerelation.control;

import ch.puzzle.itc.mobiliar.builders.ResourceEntityBuilder;
import ch.puzzle.itc.mobiliar.builders.ResourceGroupEntityBuilder;
import ch.puzzle.itc.mobiliar.business.auditview.control.AuditService;
import ch.puzzle.itc.mobiliar.business.auditview.control.ThreadLocalUtil;
import ch.puzzle.itc.mobiliar.business.foreignable.entity.ForeignableOwner;
import ch.puzzle.itc.mobiliar.business.integration.entity.util.ResourceTypeEntityBuilder;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceEntity;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceGroupEntity;
import ch.puzzle.itc.mobiliar.business.resourcegroup.entity.ResourceTypeEntity;
import ch.puzzle.itc.mobiliar.business.security.control.PermissionService;
import ch.puzzle.itc.mobiliar.common.exception.ElementAlreadyExistsException;
import ch.puzzle.itc.mobiliar.common.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import jakarta.persistence.EntityManager;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ResourceRelationServiceTest {

    @Mock
    EntityManager entityManager;

    @Mock
    PermissionService permissionService;

    @Spy
    AuditService auditService = spy(new AuditService());

    @Spy
    @InjectMocks
    ResourceRelationService resourceRelationService = spy(new ResourceRelationService());

    @Before
    public void setUp() {
        ThreadLocalUtil.destroy();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void storeResourceWhenAddRelationByGroup_provided() throws ResourceNotFoundException, ElementAlreadyExistsException {
        // given
        int resourceId = 5555;
        int slaveId = 2;
        boolean provided = true;
        String relationName = "ad22";
        String typeIdentifier = "tytype";
        ForeignableOwner foreignableOwner = ForeignableOwner.getSystemOwner();
        ResourceTypeEntity resourceType = new ResourceTypeEntityBuilder().name("typename").build();
        ResourceEntity resourceEntity = new ResourceEntityBuilder().withId(resourceId).withType(resourceType).build();
        ResourceGroupEntity resourceGroupEntity = new ResourceGroupEntityBuilder().buildResourceGroupEntity("myName", Collections.EMPTY_SET, true);
        resourceGroupEntity.setResourceType(resourceType);
        when(entityManager.find(ResourceEntity.class, resourceId)).thenReturn(resourceEntity);
        when(entityManager.find(ResourceGroupEntity.class, slaveId)).thenReturn(resourceGroupEntity);
        doNothing().when(resourceRelationService).doAddResourceRelationForAllReleases(resourceId, slaveId, provided, relationName, typeIdentifier, foreignableOwner);

        // when
        resourceRelationService.addRelationByGroup(resourceId, slaveId, provided, relationName, typeIdentifier, foreignableOwner);

        // then
        assertThat((Integer) ThreadLocalUtil.getThreadVariable(ThreadLocalUtil.KEY_RESOURCE_ID), is(resourceId));
    }

    @Test
    public void storeResourceWhenAddRelationByGroup_consumed() throws ResourceNotFoundException, ElementAlreadyExistsException {
        // given
        int resourceId = 5555;
        int slaveId = 2;
        boolean provided = false;
        String relationName = "ad22";
        String typeIdentifier = "tytype";
        ForeignableOwner foreignableOwner = ForeignableOwner.getSystemOwner();
        ResourceTypeEntity resourceType = new ResourceTypeEntityBuilder().name("typename").build();
        ResourceEntity resourceEntity = new ResourceEntityBuilder().withId(resourceId).withType(resourceType).build();
        ResourceGroupEntity resourceGroupEntity = new ResourceGroupEntityBuilder().buildResourceGroupEntity("myName", Collections.EMPTY_SET, true);
        resourceGroupEntity.setResourceType(resourceType);
        when(entityManager.find(ResourceEntity.class, resourceId)).thenReturn(resourceEntity);
        when(entityManager.find(ResourceGroupEntity.class, slaveId)).thenReturn(resourceGroupEntity);
        doNothing().when(resourceRelationService).doAddResourceRelationForAllReleases(resourceId, slaveId, provided, relationName, typeIdentifier, foreignableOwner);

        // when
        resourceRelationService.addRelationByGroup(resourceId, slaveId, provided, relationName, typeIdentifier, foreignableOwner);

        // then
        assertThat((Integer) ThreadLocalUtil.getThreadVariable(ThreadLocalUtil.KEY_RESOURCE_ID), is(resourceId));
    }

//    storeResourceWhenAddRelationByGroup_consumed
}
