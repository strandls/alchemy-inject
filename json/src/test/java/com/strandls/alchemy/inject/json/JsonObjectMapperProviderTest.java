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

package com.strandls.alchemy.inject.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.strandls.alchemy.inject.json.DummyTestModule.TestClass;
import com.strandls.alchemy.inject.json.DummyTestModule.TestClassDeserializer;

/**
 * Unit test for {@link JsonObjectMapperProvider}.
 *
 * @author ashish
 *
 */
public class JsonObjectMapperProviderTest {
    /**
     * Setup guice berry.
     */
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(JsonTestModule.class);

    /**
     * Guice injected object mapper.
     */
    @Inject
    ObjectMapper objectMapper;

    /**
     * Test method for
     * {@link com.strandls.alchemy.json.JsonObjectMapperProvider#get()}.
     *
     * @throws IOException
     * @throws JsonParseException
     */
    @Test
    public void testGet() throws JsonParseException, IOException {
        // if the test module we registered then the deserialized class int
        // value will be the magic value.
        assertEquals(new TestClass(TestClassDeserializer.MAGIC_VALUE),
                objectMapper.readValue("{}", TestClass.class));
    }
}
