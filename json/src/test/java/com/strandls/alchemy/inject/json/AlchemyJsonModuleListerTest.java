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

package com.strandls.alchemy.inject.json;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.Module;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * @author Ashish Shinde
 *
 */
public class AlchemyJsonModuleListerTest {
    /**
     * Setup guice berry.
     */
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(JsonTestModule.class);

    @Inject
    /**
     * The module lister.
     */
    public AlchemyJsonModuleLister lister;

    /**
     * Test method for
     * {@link com.strandls.alchemy.json.AlchemyJsonModuleLister#getModules(com.strandls.alchemy.inject.AlchemyModule.Environment)}
     * .
     */
    @Test
    public void testGetModules() {

        test(lister, Environment.Prod, 2);
        test(lister, Environment.Test, 2);
        test(lister, Environment.All, 3);
    }

    /**
     * Test modules for given environment.
     *
     * @param lister
     *            the lister
     * @param env
     *            the environment
     * @param expectedCount
     *            expected count of modules
     */
    private void test(final AlchemyJsonModuleLister lister, final Environment env,
            final int expectedCount) {
        final Collection<Module> modules = lister.getModules(env);
        assertEquals(expectedCount, modules.size());
    }
}
