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
package org.hibernate.jsr303.tck.tests.metadata;

import java.util.Map;
import java.util.Set;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;

import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.testharness.AbstractTest;
import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.ArtifactType;
import org.jboss.testharness.impl.packaging.Classes;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import org.hibernate.jsr303.tck.util.TestUtil;
import static org.hibernate.jsr303.tck.util.TestUtil.getConstraintDescriptorsFor;


/**
 * @author Hardy Ferentschik
 */
@Artifact(artifactType = ArtifactType.JSR303)
@Classes({ TestUtil.class, TestUtil.PathImpl.class, TestUtil.NodeImpl.class })
public class ConstraintDescriptorTest extends AbstractTest {

	@Test
	@SpecAssertion(section = "5.5", id = "k")
	public void testReportAsSingleViolation() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Order.class, "orderNumber" );
		assertFalse( descriptor.isReportAsSingleViolation() );

		descriptor = getConstraintDescriptor( Person.class, "firstName" );
		assertTrue( descriptor.isReportAsSingleViolation() );
	}

	@Test
	@SpecAssertion(section = "5.5", id = "l")
	public void testEmptyComposingConstraints() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Order.class, "orderNumber" );
		assertTrue( descriptor.getComposingConstraints().isEmpty() );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "5.5", id = "a"),
			@SpecAssertion(section = "5.5", id = "c")
	})
	public void testAnnotationAndMapParametersReflectParameterOverriding() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "firstName" );
		Set<ConstraintDescriptor<?>> composingDescriptors = descriptor.getComposingConstraints();
		assertEquals( composingDescriptors.size(), 2, "Wrong number of composing constraints" );
		boolean hasSize = false;
		for ( ConstraintDescriptor<?> desc : composingDescriptors ) {
			if ( desc.getAnnotation().annotationType().equals( Size.class ) ) {
				hasSize = true;
				Size sizeAnn = ( Size ) desc.getAnnotation();
				assertEquals( sizeAnn.min(), 5, "The min parameter should reflect the overridden parameter" );
				assertEquals(
						desc.getAttributes().get( "min" ),
						5,
						"The min parameter should reflect the overridden parameter"
				);
			}
			else if ( desc.getAnnotation().annotationType().equals( NotNull.class ) ) {
			}
			else {
				fail( "Unexpected annotation." );
			}
		}
		assertTrue( hasSize, "Size composed annotation not found" );
	}

	@Test
	@SpecAssertion(section = "5.5", id = "b")
	public void testGetAttributesFromConstraintDescriptor() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Order.class, "orderNumber" );
		Map<String, Object> attributes = descriptor.getAttributes();
		assertTrue( attributes.containsKey( "message" ) );
		assertTrue( attributes.containsKey( "groups" ) );
	}

	@Test
	@SpecAssertion(section = "5.5", id = "d")
	public void testGetGroups() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "firstName" );
		Set<Class<?>> groups = descriptor.getGroups();
		assertTrue( groups.size() == 1 );
		assertEquals( groups.iterator().next(), Person.PersonValidation.class, "Wrong group" );
	}

	@Test
	@SpecAssertion(section = "5.5", id = "d")
	public void testGetGroupsOnInterface() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "lastName" );
		Set<Class<?>> groups = descriptor.getGroups();
		assertTrue( groups.size() == 1 );
		assertEquals( groups.iterator().next(), Default.class, "Wrong group" );
	}

	@Test
	@SpecAssertion(section = "5.5", id = "d")
	public void testGetGroupsWithImplicitGroup() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Man.class, "lastName" );
		Set<Class<?>> groups = descriptor.getGroups();
		assertTrue( groups.size() == 2 );
		for ( Class<?> group : groups ) {
			if ( !( group.equals( Default.class ) || group.equals( Person.class ) ) ) {
				fail( "Invalid group." );
			}
		}
	}

	@Test
	@SpecAssertion(section = "5.5", id = "e")
	public void testDefaultGroupIsReturnedIfNoGroupSpecifiedInDeclaration() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Order.class, "orderNumber" );
		Set<Class<?>> groups = descriptor.getGroups();
		assertTrue( groups.size() == 1 );
		assertEquals( groups.iterator().next(), Default.class, "Wrong group" );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "5.5", id = "f"),
			@SpecAssertion(section = "5.5", id = "l")
	})
	public void testComposingConstraints() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "firstName" );
		Set<ConstraintDescriptor<?>> composingDescriptors = descriptor.getComposingConstraints();
		assertEquals( composingDescriptors.size(), 2, "Wrong number of composing constraints" );
		for ( ConstraintDescriptor<?> desc : composingDescriptors ) {
			assertTrue( desc.getGroups().size() == 1 );
			assertEquals( desc.getGroups().iterator().next(), Person.PersonValidation.class, "Wrong group" );
		}
	}

	@Test
	@SpecAssertion(section = "5.5", id = "g")
	public void testPayload() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "firstName" );
		Set<Class<? extends Payload>> payload = descriptor.getPayload();
		assertTrue( payload.size() == 1 );
		assertEquals( payload.iterator().next(), Severity.Info.class, "Wrong payload" );

		descriptor = getConstraintDescriptor( Order.class, "orderNumber" );
		payload = descriptor.getPayload();
		assertTrue( payload != null );
		assertTrue( payload.size() == 0 );
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "5.5", id = "h"),
			@SpecAssertion(section = "5.5", id = "i")
	})
	public void testComposingConstraintsPayload() {
		ConstraintDescriptor<?> descriptor = getConstraintDescriptor( Person.class, "firstName" );
		Set<ConstraintDescriptor<?>> composingDescriptors = descriptor.getComposingConstraints();
		assertEquals( composingDescriptors.size(), 2, "Wrong number of composing constraints" );
		for ( ConstraintDescriptor<?> desc : composingDescriptors ) {
			assertTrue( desc.getGroups().size() == 1 );
			assertEquals( desc.getPayload().iterator().next(), Severity.Info.class, "Wrong payload" );
		}
	}

	private ConstraintDescriptor<?> getConstraintDescriptor(Class<?> clazz, String property) {
		Set<ConstraintDescriptor<?>> descriptors = getConstraintDescriptorsFor( clazz, property );
		assertTrue( descriptors.size() == 1, "There should only by one descriptor." );
		return descriptors.iterator().next();
	}
}
