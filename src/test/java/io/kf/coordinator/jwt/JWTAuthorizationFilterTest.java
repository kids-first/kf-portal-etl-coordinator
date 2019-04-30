/*
 * Copyright (c) 2017. The Ontario Institute for Cancer Research. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kf.coordinator.jwt;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link io.kf.coordinator.jwt.JWTAuthorizationFilter.
 */
@RunWith(SpringRunner.class)
public class JWTAuthorizationFilterTest {

	private JWTAuthorizationFilter jWTAuthorizationFilter;

	@Before
	public void setUp() throws Exception {
		jWTAuthorizationFilter = Mockito.spy(new JWTAuthorizationFilter());
	}

	@Test
	public void shouldPassOnStatusApprovedAndRole() {
		// Given
		JWTUser user = new JWTUser();
		user.setType(null);
		user.setRoles(Arrays.asList("ADMIN"));
		user.setStatus("Approved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User valid on role and status", userValidated, CoreMatchers.is(true));
	}

	@Test
	public void shouldFailOnStatusNotApprovedAndRole() {
		// Given
		JWTUser user = new JWTUser();
		user.setType(null);
		user.setRoles(Arrays.asList("ADMIN"));
		user.setStatus("NotApproved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User NOT valid on role and status", userValidated, CoreMatchers.is(false));
	}

	@Test
	public void shouldPassOnStatusApprovedAndType() {
		// Given
		JWTUser user = new JWTUser();
		user.setType("ADMIN");
		user.setRoles(null);
		user.setStatus("Approved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User valid on type and status", userValidated, CoreMatchers.is(true));
	}

	@Test
	public void shouldFailOnStatusNotApprovedAndType() {
		// Given
		JWTUser user = new JWTUser();
		user.setType("ADMIN");
		user.setRoles(null);
		user.setStatus("NotApproved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User NOT valid on type and status", userValidated, CoreMatchers.is(false));
	}

	@Test
	public void shouldFailOnNoTypeNoRole() {
		// Given
		JWTUser user = new JWTUser();
		user.setType(null);
		user.setRoles(null);
		user.setStatus("Approved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User NOT valid on NO type No role", userValidated, CoreMatchers.is(false));
	}

	@Test
	public void shouldPassOnTypeAndRoleFirst() {
		// Given
		JWTUser user = new JWTUser();
		user.setType("ADMIN");
		user.setRoles(Arrays.asList("ADMIN"));
		user.setStatus("Approved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User valid on type and role first", userValidated, CoreMatchers.is(true));
	}

	@Test
	public void shouldFailOnStatusNotApprovedAndNoTypeNoRole() {
		// Given
		JWTUser user = new JWTUser();
		user.setType(null);
		user.setRoles(null);
		user.setStatus("NotApproved");

		// When
		boolean userValidated = jWTAuthorizationFilter.validateUser(Optional.of(user));

		// Then
		assertThat("User NOT valid on NO type No role not approved", userValidated, CoreMatchers.is(false));
	}

}
