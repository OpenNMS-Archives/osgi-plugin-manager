/*
 * Copyright 2014 OpenNMS Group Inc., Entimoss ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opennms.karaf.featuremgr.test;

import java.net.URI;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeatureState;
import org.apache.karaf.features.FeaturesListener;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;

public class MockFeaturesService implements FeaturesService {

	@Override
	public void validateRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRepository(URI uri, boolean install) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRepository(URI uri, boolean uninstall) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Repository[] listRequiredRepositories() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Repository[] listRepositories() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Repository getRepository(String repoName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Repository getRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRepositoryName(URI uri) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResolutionOutputFile(String outputFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeature(String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeature(String name, EnumSet<Option> options)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeature(String name, String version) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeature(String name, String version,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeature(Feature f, EnumSet<Option> options)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeatures(Set<String> features, EnumSet<Option> options)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void installFeatures(Set<String> features, String region,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRequirements(Map<String, Set<String>> requirements,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeature(String name, EnumSet<Option> options)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeature(String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeature(String name, String version,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeature(String name, String version) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeatures(Set<String> features, EnumSet<Option> options)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallFeatures(Set<String> features, String region,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRequirements(Map<String, Set<String>> requirements,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFeaturesState(
			Map<String, Map<String, FeatureState>> stateChanges,
			EnumSet<Option> options) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Feature[] listFeatures() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature[] listRequiredFeatures() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature[] listInstalledFeatures() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Set<String>> listRequirements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequired(Feature f) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInstalled(Feature f) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Feature getFeature(String name, String version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature getFeature(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature[] getFeatures(String name, String version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Feature[] getFeatures(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshRepository(URI uri) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public URI getRepositoryUriFor(String name, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRepositoryNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerListener(FeaturesListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterListener(FeaturesListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public FeatureState getState(String featureId) {
		// TODO Auto-generated method stub
		return null;
	}

}
