/*
 * Copyright (C) 2015 Alchemy Inject Authors
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.google.inject.AbstractModule;

/**
 * Unit tests for {@link AlchemyModuleFilter}.
 *
 * @author ashish
 *
 */
public class AlchemyModuleFilterTest {
    /**
     * Module to filter out.
     *
     * @author ashish
     *
     */
    private static class ToFilter extends AbstractModule {

        /*
         * (non-Javadoc)
         * @see com.google.inject.AbstractModule#configure()
         */
        @Override
        protected void configure() {

        }

    }

    /**
     * Module to retain.
     *
     * @author ashish
     *
     */
    private static class ToRetain extends AbstractModule {

        /*
         * (non-Javadoc)
         * @see com.google.inject.AbstractModule#configure()
         */
        @Override
        protected void configure() {

        }

    }

    /**
     * Test method for
     * {@link com.strandls.alchemy.inject.AlchemyModuleFilter#apply(com.google.inject.Module)}
     * .
     */
    @Test
    public void testApply() {
        final AlchemyModuleFilter filter =
                new AlchemyModuleFilter(new HashSet<>(Arrays.asList("(?i).*tofilter.*")));
        assertFalse(filter.apply(new ToFilter()));
        assertTrue(filter.apply(new ToRetain()));
    }

}
