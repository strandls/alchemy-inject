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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * Unit tests for {@link AlchemyModuleFilterConfiguration}.
 *
 * @author ashish
 *
 */
public class AlchemyModuleFilterConfigurationTest {
    private Map<Environment, Set<String>> expectedResult;

    /**
     * Setup expected result.
     *
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        expectedResult =
                mapper.readValue("{'Prod' : ['(?i).*dummy.*'], 'Test':['ToFilter1', 'ToFilter2'],"
                        + " 'All':['(?i).*dummy.*', 'ToFilter1', 'ToFilter2']}",
                        new TypeReference<Map<Environment, Set<String>>>() {
                        });
    }

    /**
     * Test method for
     * {@link com.strandls.alchemy.inject.AlchemyModuleFilterConfiguration#getFilterConfiguration(com.strandls.alchemy.inject.AlchemyModule.Environment)}
     * .
     */
    @Test
    public void testGetFilter() {
        final AlchemyModuleFilterConfiguration configuration =
                new AlchemyModuleFilterConfiguration();
        for (final Environment env : Environment.values()) {
            if (expectedResult.containsKey(env)) {
                assertEquals(expectedResult.get(env), configuration.getFilterConfiguration(env));
            }
        }
    }

}
