// $Id: ValidPackage.java 17427 2009-08-27 09:47:28Z hardy.ferentschik $
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

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * @author Hardy Ferentschik
 */
@Constraint(validatedBy = ValidPackage.ValidPackageValidator.class)
@Documented
@Target({ METHOD, FIELD, TYPE })
@Retention(RUNTIME)
public @interface ValidPackage {
	public abstract String message() default "User information is not consistent.";

	public abstract Class<?>[] groups() default { };

	public abstract Class<? extends Payload>[] payload() default { };

	public class ValidPackageValidator implements ConstraintValidator<ValidPackage, Package> {

		public void initialize(ValidPackage parameters) {
		}

		public boolean isValid(Package p, ConstraintValidatorContext constraintValidatorContext) {
			return false;
		}
	}
}