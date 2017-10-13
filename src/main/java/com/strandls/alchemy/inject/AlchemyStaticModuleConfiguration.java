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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides a static set of modules to be used instead of using reflection.
 *
 * @author Ashish Shinde
 *
 */
@Singleton
@Slf4j
public class AlchemyStaticModuleConfiguration {
	/**
	 * Name of the .ini file for configuration.
	 */
	private static final String MODULE_CONFIGURATION_NAME = "alchemy-modules.ini";

	/**
	 * The static module configuration property used in the .ini file.
	 */
	private static final String STATIC_PROPERTY = ".static";

	/**
	 * Mapping from the environment to static configuration.
	 */
	private final Map<Environment, List<String>> environmentStaticModuleConfig;

	/**
	 * Creates the configuration object.
	 */
	public AlchemyStaticModuleConfiguration() {
		environmentStaticModuleConfig = new HashMap<>();
		loadConfiguration();
	}

	/**
	 * Return the static module list to use for an environment. Includes static
	 * module lists for compatible environments as well.
	 *
	 * @param env
	 *            the environment.
	 * @return the the static module list to use for the environment.
	 */
	public Set<String> getStaticModuleConfiguration(final Environment env) {
		final Set<String> staticModuleNames = new HashSet<>();
		for (final Entry<Environment, List<String>> entry : environmentStaticModuleConfig.entrySet()) {
			final Environment configEnv = entry.getKey();
			if (env.isCompatible(configEnv)) {
				// include static modules from all compatible environments.
				staticModuleNames.addAll(entry.getValue());
			}
		}
		return staticModuleNames;
	}

	/**
	 * Load static module list configuration into the internal map.
	 */
	private void loadConfiguration() {
		environmentStaticModuleConfig.clear();
		final Configuration configuration = readConfiguration();
		for (final Environment env : Environment.values()) {
			final List<String> staticModuleConfig = Lists.transform(configuration.getList(env.name() + STATIC_PROPERTY),
					new Function<Object, String>() {

						@Override
						public String apply(final Object input) {
							final String pattern = ObjectUtils.toString(input);
							log.info("For environment {} found module static class {} ", env, pattern);
							return pattern;
						}
					});
			environmentStaticModuleConfig.put(env, Collections.unmodifiableList(staticModuleConfig));
		}
	}

	/**
	 * @return configuration read from the config file.
	 */
	private Configuration readConfiguration() {
		final CompositeConfiguration config = new CompositeConfiguration();
		config.addConfiguration(new SystemConfiguration());
		try {
			config.addConfiguration(new HierarchicalINIConfiguration(MODULE_CONFIGURATION_NAME));
		} catch (final ConfigurationException e) {
			// ignore if the configuration file is missing. This means no
			// static modules intended.
			log.warn("Error loading configuration file {}", e);
		}
		return config;
	}
}
