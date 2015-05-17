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

import java.io.IOException;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * A dummy module used for testing module lister.
 *
 * @author Ashish Shinde
 *
 */
@AlchemyJsonModule(Environment.Test)
public class DummyTestModule extends SimpleModule {

    /**
     * A test class for testing objectmapper configuration.
     *
     * @author Ashish Shinde
     *
     */
    @Data
    public static class TestClass {
        @JsonProperty
        private final int intVal;
    }

    /**
     * Dummy noop deserializer.
     *
     * @author Ashish Shinde
     *
     */
    public static class TestClassDeserializer extends JsonDeserializer<TestClass> {
        /**
         * The returned class's intValue.
         */
        public static final int MAGIC_VALUE = 545454;

        /*
         * (non-Javadoc)
         * @see
         * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
         * .jackson.core.JsonParser,
         * com.fasterxml.jackson.databind.DeserializationContext)
         */
        @Override
        public TestClass deserialize(final JsonParser jp, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return new TestClass(MAGIC_VALUE);
        }

    }

    /**
     * The serial version ID.
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see
     * com.fasterxml.jackson.databind.module.SimpleModule#setupModule(com.fasterxml
     * .jackson.databind.Module.SetupContext)
     */
    @Override
    public void setupModule(final SetupContext context) {
        addDeserializer(TestClass.class, new TestClassDeserializer());
        super.setupModule(context);
    }
}
