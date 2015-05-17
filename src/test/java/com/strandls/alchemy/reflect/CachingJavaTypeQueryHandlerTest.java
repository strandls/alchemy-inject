/*
 * Copyright (C) 2015 Strand Life Sciences.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strandls.alchemy.reflect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.strandls.alchemy.inject.AlchemyModule;

/**
 * Unit tests for {@link CachingJavaTypeQueryHandler}.
 *
 * @author Ashish Shinde
 *
 */
public class CachingJavaTypeQueryHandlerTest {
    /**
     * Setup guice berry.
     */
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InjectTestModule.class);

    /**
     * The query handler.
     */
    @Inject
    CachingJavaTypeQueryHandler queryHandler;

    /**
     * Test method for
     * {@link com.strandls.alchemy.cdo.common.reflect.CachingJavaTypeQueryHandler#getSubTypesOf(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public void testGetSubTypesOf() {
        // should have results for epackage sub types
        @SuppressWarnings("rawtypes")
        final Set<Class<? extends List>> result = queryHandler.getSubTypesOf(".*", List.class);
        assertFalse(result.isEmpty());

        // make sure caching works and we get cached result for the same query
        // as above.
        assertSame(result, queryHandler.getSubTypesOf(".*", List.class));

        // should not have results for epackage in some random package pattern.
        Assert.assertTrue(queryHandler.getSubTypesOf(UUID.randomUUID().toString(), List.class)
                .isEmpty());
    }

    /**
     * Test method for
     * {@link com.strandls.alchemy.cdo.common.reflect.CachingJavaTypeQueryHandler#getTypesAnnotatedWith(java.lang.String, java.lang.Class)}
     * .
     */
    @Test
    public void testGetTypesAnnotatedWith() {
        // should have results for epackage sub types
        Assert.assertFalse(queryHandler.getTypesAnnotatedWith(".*", AlchemyModule.class).isEmpty());

        // should not have results for epackage in some random package pattern.
        Assert.assertTrue(queryHandler.getTypesAnnotatedWith(UUID.randomUUID().toString(),
                AlchemyModule.class).isEmpty());
    }
}
