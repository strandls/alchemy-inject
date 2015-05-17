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

package com.strandls.alchemy.inject;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * @author Ashish Shinde
 *
 */
public class AlchemyModuleListerTest {

    /**
     * Test method for
     * {@link com.strandls.alchemy.inject.AlchemyModuleLister#getModules(com.google.inject.Environment)}
     * .
     */
    @Test
    public void testGetModules() {
        final AlchemyModuleLister lister = new AlchemyModuleLister();
        test(lister, Environment.Prod, 2);
        test(lister, Environment.Test, 4);
        test(lister, Environment.All, 2);
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
    private void test(final AlchemyModuleLister lister, final Environment env,
            final int expectedCount) {
        final Collection<Module> modules = lister.getModules(env);

        // add environment binding
        modules.add(new AbstractModule() {

            @Override
            protected void configure() {
                bind(Environment.class).toInstance(env);
            }
        });

        assertEquals(expectedCount, modules.size());

        // make sure prod modules work correctly.
        Guice.createInjector(modules);
    }
}
