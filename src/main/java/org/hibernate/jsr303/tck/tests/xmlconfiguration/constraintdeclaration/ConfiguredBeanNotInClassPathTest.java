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
package org.hibernate.jsr303.tck.tests.xmlconfiguration.constraintdeclaration;

import javax.validation.ValidationException;

import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.testharness.AbstractTest;
import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.ArtifactType;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.testharness.impl.packaging.Resource;
import org.jboss.testharness.impl.packaging.jsr303.ValidationXml;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import org.hibernate.jsr303.tck.util.TestUtil;

/**
 * @author Hardy Ferentschik
 */
@Artifact(artifactType = ArtifactType.JSR303)
@Classes({ TestUtil.class, TestUtil.PathImpl.class, TestUtil.NodeImpl.class })
@ValidationXml(value = "validation-ConfiguredBeanNotInClassPathTest.xml")
@Resource(source = "constraints-ConfiguredBeanNotInClassPathTest.xml",
		destination = "WEB-INF/classes/org/hibernate/jsr303/tck/tests/xmlconfiguration/constraintdeclaration/constraints-ConfiguredBeanNotInClassPathTest.xml")
public class ConfiguredBeanNotInClassPathTest extends AbstractTest {

	@Test
	@SpecAssertion(section = "7.1.1", id = "f")
	public void testExceptionIsThrownForUnknownBeanNameInXml() {
		try {
			TestUtil.getValidatorUnderTest();
			fail( "Test should have thrown an exception due to wrong class name" );
		}
		catch ( ValidationException e ) {
			//success
		}
	}
}
