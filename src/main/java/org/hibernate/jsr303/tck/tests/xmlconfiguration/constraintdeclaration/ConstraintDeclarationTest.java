// $Id$
/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

import javax.validation.Configuration;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.testharness.AbstractTest;
import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.ArtifactType;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.testharness.impl.packaging.Resource;
import org.jboss.testharness.impl.packaging.jsr303.ValidationXml;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

import org.hibernate.jsr303.tck.util.TestUtil;

/**
 * @author Hardy Ferentschik
 */
@Artifact(artifactType = ArtifactType.JSR303)
@Classes({ TestUtil.class, TestUtil.PathImpl.class, TestUtil.NodeImpl.class })
@ValidationXml(value = "validation-ConstraintDeclarationTest.xml")
@Resource(source = "package-constraints-ConstraintDeclarationTest.xml",
		destination = "WEB-INF/classes/org/hibernate/jsr303/tck/tests/xmlconfiguration/constraintdeclaration/package-constraints-ConstraintDeclarationTest.xml")
public class ConstraintDeclarationTest extends AbstractTest {

	@Test
	@SpecAssertion(section = "7.1.1", id = "d")
	public void testConstraintAnnotationsArePerDefaultIgnoredForXmlConfiguredEntities() {
		Validator validator = TestUtil.getValidatorUnderTest();
		BeanDescriptor beanDescriptor = validator.getConstraintsForClass( Package.class );
		assertFalse( beanDescriptor.isBeanConstrained(), "With xml configuration there should be no constraint." );

		Configuration<?> config = TestUtil.getConfigurationUnderTest();
		config.ignoreXmlConfiguration();
		validator = config.buildValidatorFactory().getValidator();
		beanDescriptor = validator.getConstraintsForClass( Package.class );
		assertTrue(
				beanDescriptor.isBeanConstrained(),
				"If xml configuration is ignored Package should have a single constraint."
		);
	}
}
