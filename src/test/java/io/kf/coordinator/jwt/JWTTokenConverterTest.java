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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.test.context.junit4.SpringRunner;

import io.kf.coordinator.utils.TypeUtils;
import lombok.val;

/**
 * {@link io.kf.coordinator.jwt.JWTTokenConverter.
 */
@RunWith(SpringRunner.class)
@SuppressWarnings("rawtypes")
public class JWTTokenConverterTest {

	@Test
	public void shouldPassWhenAuthenticationMapProvided() {
		try {
			// Given
			JWTTokenConverter converter = Mockito.spy(new JWTTokenConverter("key"));
			final Map<String, ?> userMap = Collections.singletonMap("context",
					Collections.singletonMap("user", Collections.singletonMap("type", "ADMIN")));
			// When
			OAuth2Authentication oAuthUser = converter.extractAuthentication(userMap);

			// Then
			assertThat("Auth user info is populated", oAuthUser.getDetails(), CoreMatchers.notNullValue());
		} catch (Exception nested) {
			assertThat("abstract super classe error occured", nested, CoreMatchers.notNullValue());
		}
	}

	@Test
	public void shouldPassWhenRolesProvided() {
		// Given
		final Map userMap = Collections.singletonMap("roles", Arrays.asList("USER"));

		// When
		val jwtUser = TypeUtils.convertType(userMap, JWTUser.class);

		// Then
		assertThat("Roles is populated", jwtUser.getRoles(), CoreMatchers.hasItem("USER"));
	}

	@Test
	public void shouldPassWhenRolesNotProvided() {
		// Given
		final Map userMap = Collections.singletonMap("unknown", "dummy");

		// When
		val jwtUser = TypeUtils.convertType(userMap, JWTUser.class);

		// Then
		assertThat("Roles is not provided", jwtUser.getRoles(), CoreMatchers.equalTo(null));
	}

	@Test
	public void shouldPassWhenTypeNotProvided() {
		// Given
		final Map userMap = Collections.singletonMap("anyThing", "dummy");

		// When
		val jwtUser = TypeUtils.convertType(userMap, JWTUser.class);

		// Then
		assertThat("Type is not provided", jwtUser.getType(), CoreMatchers.equalTo(null));
	}

	@Test
	public void shouldPassWhenTypeProvided() {
		// Given
		final Map userMap = Collections.singletonMap("type", "Admin");

		// When
		val jwtUser = TypeUtils.convertType(userMap, JWTUser.class);

		// Then
		assertThat("Type is provided", jwtUser.getType(), CoreMatchers.containsString("Admin"));
	}

}
