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

package ch.puzzle.itc.mobiliar.business.globalfunction.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import ch.puzzle.itc.mobiliar.test.testrunner.PersistenceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import ch.puzzle.itc.mobiliar.business.globalfunction.entity.GlobalFunctionEntity;

@RunWith(PersistenceTestRunner.class)
public class GlobalFunctionsRepositoryTestWithPersistence {

    @Spy
    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testSaveDuplicateNameGlobalFunction() throws Exception {

        // given
        GlobalFunctionEntity gFunction = new GlobalFunctionEntity();
        gFunction.setName("twice");

        GlobalFunctionRepository funRepo = new GlobalFunctionRepository();
        funRepo.entityManager = entityManager;
        assertTrue(funRepo.saveFunction(gFunction));

        // when
        GlobalFunctionEntity gFunction2 = new GlobalFunctionEntity();
        gFunction2.setName("twice");

        // then
        assertFalse(funRepo.saveFunction(gFunction2));

    }

    @Test
    public void testSaveAndDeleteGlobalFunction() throws Exception {

        // given
        GlobalFunctionEntity gFunction = new GlobalFunctionEntity();
        gFunction.setName("once");

        GlobalFunctionRepository funRepo = new GlobalFunctionRepository();
        funRepo.entityManager = entityManager;

        assertTrue(funRepo.saveFunction(gFunction));
        assertTrue(funRepo.isExistingName(gFunction));

        // when
        funRepo.deleteFunction(gFunction);

        // then
        assertFalse(funRepo.isExistingName(gFunction));

    }
}