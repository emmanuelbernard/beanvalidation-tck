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
package org.hibernate.jsr303.tck.tests.constraints.groups;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.GroupDefinitionException;
import javax.validation.GroupSequence;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.testharness.AbstractTest;
import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.ArtifactType;
import org.jboss.testharness.impl.packaging.Classes;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

import org.hibernate.jsr303.tck.util.TestUtil;
import static org.hibernate.jsr303.tck.util.TestUtil.assertConstraintViolation;
import static org.hibernate.jsr303.tck.util.TestUtil.assertCorrectConstraintViolationMessages;
import static org.hibernate.jsr303.tck.util.TestUtil.assertCorrectNumberOfViolations;

/**
 * Tests for redefining the default group sequence.
 *
 * @author Hardy Ferentschik
 */
@Artifact(artifactType = ArtifactType.JSR303)
@Classes({ TestUtil.class, TestUtil.PathImpl.class, TestUtil.NodeImpl.class })
public class DefaultGroupRedefinitionTest extends AbstractTest {

	@Test
	@SpecAssertion(section = "3.4.3", id = "a")
	public void testRedefiningDefaultGroup() {
		Address address = new Address();
		address.setStreet( "Guldmyntgatan" );
		address.setCity( "Gothenborg" );

		Validator validator = TestUtil.getValidatorUnderTest();

		Set<ConstraintViolation<Address>> constraintViolations = validator.validate( address );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		ConstraintViolation<Address> violation = constraintViolations.iterator().next();
		assertConstraintViolation( violation, Address.class, null, "zipcode" );
		assertCorrectConstraintViolationMessages( constraintViolations, "Zipcode may not be null" );

		address.setZipcode( "41841" );

		// now the second group in the re-defined default group causes an error
		constraintViolations = validator.validate( address );
		assertCorrectNumberOfViolations( constraintViolations, 1 );

		violation = constraintViolations.iterator().next();
		assertConstraintViolation( violation, Address.class, address, "" );
		assertCorrectConstraintViolationMessages( constraintViolations, "Zip code is not coherent." );
	}

	@Test
	@SpecAssertion(section = "3.4.3", id = "a")
	public void testValidatingAgainstRedefinedDefaultGroup() {
		Car car = new Car();
		car.setType( "A" );

		Validator validator = TestUtil.getValidatorUnderTest();

		// if the group sequence would not be properly redefined there would be no error when validating default.

		Set<ConstraintViolation<Car>> constraintViolations = validator.validate( car );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertCorrectConstraintViolationMessages(
				constraintViolations, "Car type has to be between 2 and 20 characters."
		);

		constraintViolations = validator.validateProperty( car, "type" );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertCorrectConstraintViolationMessages(
				constraintViolations, "Car type has to be between 2 and 20 characters."
		);

		constraintViolations = validator.validateValue( Car.class, "type", "A" );
		assertCorrectNumberOfViolations( constraintViolations, 1 );
		assertCorrectConstraintViolationMessages(
				constraintViolations, "Car type has to be between 2 and 20 characters."
		);
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "3.4.3", id = "c"),
			@SpecAssertion(section = "3.4.3", id = "d")
	})
	public void testGroupSequenceContainingDefault() {
		Address address = new AddressWithDefaultInGroupSequence();
		Validator validator = TestUtil.getValidatorUnderTest();
		try {
			validator.validate( address );
			fail( "It should not be allowed to have Default.class in the group sequence of a class." );
		}
		catch ( GroupDefinitionException e ) {
			// success
		}
	}

	@Test
	@SpecAssertions({
			@SpecAssertion(section = "3.4.3", id = "c"),
			@SpecAssertion(section = "3.4.3", id = "d")
	})
	public void testGroupSequenceWithNoImplicitDefaultGroup() {
		Address address = new AddressWithNoImplicitDefaultGroupSequence();
		Validator validator = TestUtil.getValidatorUnderTest();
		try {
			validator.validate( address );
			fail( "A valid group sequence definition must contain the class itself as implicit default group." );
		}
		catch ( GroupDefinitionException e ) {
			// success
		}
	}

	private void assertAssertionType(Set<ConstraintViolation<Address>> violations) {
		for ( ConstraintViolation<Address> violation : violations ) {
			// cast is required for JDK 5 - at least on Mac OS X
			Annotation ann = ( Annotation ) violation.getConstraintDescriptor().getAnnotation();
			assertEquals(
					NotNull.class,
					ann.annotationType(),
					"Wrong assertion type"
			);
		}
	}

	@GroupSequence({ Default.class, Address.HighLevelCoherence.class })
	public class AddressWithDefaultInGroupSequence extends Address {

	}

	@GroupSequence({ Address.HighLevelCoherence.class })
	public class AddressWithNoImplicitDefaultGroupSequence extends Address {

	}
}
