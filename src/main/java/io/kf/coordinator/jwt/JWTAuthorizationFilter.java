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

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class JWTAuthorizationFilter extends GenericFilterBean {

	private final Set<String> APPROVED_ROLES = new HashSet<>(Arrays.asList("ADMIN"));
	private final String REQUIRED_STATUS = "Approved";

	@Override
	@SneakyThrows
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
		val authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {

			val details = (OAuth2AuthenticationDetails) authentication.getDetails();
			val jwtDetails = (JWTDetails) details.getDecodedDetails();

			if (!validateTokenDetails(jwtDetails)) {
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}

	protected boolean validateTokenDetails(@NonNull JWTDetails jwtDetails) {
		return (validateUser(jwtDetails.getUser()) || validateApplication(jwtDetails.getApplication()));
	}

	/**
	 * Helper method used to validate user jwt content
	 * 
	 * @param maybeUser input jwt user.
	 * @return true/false
	 */
	protected boolean validateUser(Optional<JWTUser> maybeUser) {
		// User must have User role and Approved status

		// Check roles or type // maintain backward compatibility
		final Function<JWTUser, Boolean> checkUserDelegate = jwtUser -> {
			final String type = jwtUser.getType();
			final List<String> userRoles = jwtUser.getRoles();
			final String status = jwtUser.getStatus();

			final boolean hasUserRole = (!Objects.isNull(userRoles) && !userRoles.isEmpty())
					? !Collections.disjoint(userRoles, APPROVED_ROLES) // must be the same
					: !Objects.isNull(type) && !Collections.disjoint(Arrays.asList(type), APPROVED_ROLES);

			return hasUserRole && !Objects.isNull(status) && status.equalsIgnoreCase(REQUIRED_STATUS);
		};

		return maybeUser.filter(user -> checkUserDelegate.apply(user)).isPresent();
	}

	protected boolean validateApplication(Optional<JWTApplication> maybeApp) {
		// Application must have Approved status
		return maybeApp.filter(app -> app.getStatus().equalsIgnoreCase(REQUIRED_STATUS)).isPresent();
	}

}
