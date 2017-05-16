package com.mastfrog.netty.http.client;

import com.mastfrog.acteur.headers.Method;
import com.mastfrog.util.thread.Receiver;
import io.netty.handler.codec.http.HttpResponse;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by vitaliy.kuzmich on 5/16/17.
 */
public class RedirectLoopTest {
	@Test
	public void testRedirect() throws InterruptedException {
		HttpClient client = HttpClient.builder().threadCount(4).followRedirects().build();
		HttpRequestBuilder builder = client.request(Method.GET);
		//google should redirect to www.google.com, but loop here
		builder.setURL("http://google.com");
		ResponseFuture fut = builder.on(StateType.HeadersReceived, new Receiver<HttpResponse>() {
			@Override
			public void receive(HttpResponse response) {

				System.out.println(response.getDecoderResult().toString());
			}
		}).on(StateType.Error, new Receiver<Throwable>() {
			@Override
			//it seems google redirects too much ?
			public void receive(Throwable object) {
				object.printStackTrace();
			}
		}).execute();
		fut.await(5, TimeUnit.SECONDS);
	}
}
