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
package org.hibernate.jsr303.tck.tests.constraints.constraintcomposition;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Hardy Ferentschik
 */
public class FrenchZipcodeConstraintValidator implements ConstraintValidator<FrenchZipcode, String> {

	public void initialize(FrenchZipcode parameters) {
	}

	public boolean isValid(String zip, ConstraintValidatorContext constraintValidatorContext) {
		if ( zip == null ) {
			return true;
		}
		if ( "00000".equals( zip ) ) {
			constraintValidatorContext.disableDefaultError();
			constraintValidatorContext.buildErrorWithMessageTemplate( "00000 is a reserved code"  ).addError();
			return false;
		}
		else {
			return true;
		}
	}
}
