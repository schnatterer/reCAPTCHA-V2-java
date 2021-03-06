/**
 * MIT License
 *
 * Copyright (c) 2017 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.recaptchav2java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import java.util.function.Function;

/**
 * HTTP-related logic for reCAPTCHA.
 */
class Http {
    private static final Logger LOG = LoggerFactory.getLogger(Http.class);
    private Http() {}

    static String post(String url, String urlParameters) {
        try {
            return postWithExceptions(url, urlParameters);
        } catch (ProcessingException e){
            throw new ReCaptchaException("I/O error sending or receiving the response ", e);
        }
    }

    private static String postWithExceptions(String url, String urlParameters) {
        return withClient(url, client ->
            client.target(url)
                .request()
                .post(Entity.json(urlParameters))
                .readEntity(String.class));
    }

    private static String withClient(String url, Function<Client, String> runnable) {
        Client client = null;
        try {
            LOG.trace("Creating client {}", url);
            client = ClientBuilder.newClient();
            return runnable.apply(client);
        } finally {
            if (client != null) {
                LOG.trace("Closing client for url {}", url);
                client.close();
            }
        }
    }
}
