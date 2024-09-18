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

package ch.puzzle.itc.mobiliar.business.resourcerelation.control;

import ch.puzzle.itc.mobiliar.business.environment.entity.ContextEntity;
import ch.puzzle.itc.mobiliar.business.resourcerelation.entity.ConsumedResourceRelationEntity;
import ch.puzzle.itc.mobiliar.business.resourcerelation.entity.ResourceRelationContextEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ResourceRelationContextRepositoryTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    ResourceRelationContextRepository resourceRelationContextRepository;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateResourceRelationContext() throws Exception {
        ConsumedResourceRelationEntity consumedResourceRelationEntity = mock(ConsumedResourceRelationEntity.class);
        ContextEntity contextEntity = mock(ContextEntity.class);

        ResourceRelationContextEntity resRelCtx = resourceRelationContextRepository.createResourceRelationContext(
                  consumedResourceRelationEntity, contextEntity);
        verify(entityManager, times(1)).persist(resRelCtx);
        assertEquals(consumedResourceRelationEntity, resRelCtx.getContextualizedObject());
        assertEquals(contextEntity, resRelCtx.getContext());
    }
}