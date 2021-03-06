// $Id$
/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.jsr303.tck.tests.bootstrap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.validation.Configuration;

import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.testharness.AbstractTest;
import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.ArtifactType;
import org.jboss.testharness.impl.packaging.Classes;
import org.testng.annotations.Test;

import org.hibernate.jsr303.tck.common.TCKValidationProvider;
import org.hibernate.jsr303.tck.common.TCKValidatorConfiguration;
import org.hibernate.jsr303.tck.util.TestUtil;

import static org.testng.Assert.assertTrue;

/**
 * @author Hardy Ferentschik
 */
@Artifact(artifactType = ArtifactType.JSR303)
@Classes({
		TestUtil.class,
		TestUtil.PathImpl.class,
		TestUtil.NodeImpl.class,
		TCKValidationProvider.class,
		TCKValidatorConfiguration.class,
		TCKValidationProvider.DummyValidatorFactory.class
})
public class ConfigurationTest extends AbstractTest {

	@Test
	@SpecAssertion(section = "4.4.3", id = "a")
	public void testProviderUnderTestDefinesSubInterfaceOfConfiguration() {
		boolean foundSubinterfaceOfConfiguration = false;
		Type[] types = TestUtil.getValidationProviderUnderTest().getClass().getGenericInterfaces();
		for ( Type type : types ) {
			if ( type instanceof ParameterizedType ) {
				ParameterizedType paramType = ( ParameterizedType ) type;
				Type[] typeArguments = paramType.getActualTypeArguments();
				for ( Type typeArgument : typeArguments ) {
					if ( typeArgument instanceof Class && Configuration.class.isAssignableFrom( ( Class ) typeArgument ) ) {
						foundSubinterfaceOfConfiguration = true;
					}
				}
			}
		}
		assertTrue( foundSubinterfaceOfConfiguration, "Could not find subinterface of Configuration" );
	}
}
