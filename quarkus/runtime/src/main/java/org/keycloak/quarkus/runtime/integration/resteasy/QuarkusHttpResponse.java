/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.quarkus.runtime.integration.resteasy;

import jakarta.ws.rs.core.HttpHeaders;
import org.jboss.resteasy.reactive.server.core.ResteasyReactiveRequestContext;
import org.jboss.resteasy.reactive.server.vertx.VertxResteasyReactiveRequestContext;
import org.keycloak.http.HttpCookie;
import org.keycloak.http.HttpResponse;

import java.util.HashSet;
import java.util.Set;

public final class QuarkusHttpResponse implements HttpResponse {

    private final ResteasyReactiveRequestContext requestContext;

    private Set<HttpCookie> cookies;

    public QuarkusHttpResponse(ResteasyReactiveRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public int getStatus() {
        VertxResteasyReactiveRequestContext serverHttpResponse = (VertxResteasyReactiveRequestContext) requestContext.serverResponse();
        return serverHttpResponse.vertxServerResponse().getStatusCode();
    }

    @Override
    public void setStatus(int statusCode) {
        requestContext.serverResponse().setStatusCode(statusCode);
    }

    @Override
    public void addHeader(String name, String value) {
        requestContext.serverResponse().addResponseHeader(name, value);
    }

    @Override
    public void setHeader(String name, String value) {
        requestContext.serverResponse().setResponseHeader(name, value);
    }

    @Override
    public void setCookieIfAbsent(HttpCookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie is null");
        }

        if (cookies == null) {
            cookies = new HashSet<>();
        }

        if (cookies.add(cookie)) {
            addHeader(HttpHeaders.SET_COOKIE, cookie.toHeaderValue());
        }
    }

}
