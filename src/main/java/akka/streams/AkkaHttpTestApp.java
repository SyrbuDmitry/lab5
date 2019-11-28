package akka.streams;

import akka.NotUsed;

import akka.actor.ActorSystem;

import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;

import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;


import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpTestApp extends AllDirectives {
    public static void main(String[] args) throws Exception {

        ActorSystem system = ActorSystem.create("routes");
        AsyncHttpClient client = asyncHttpClient();
        final Http http = Http.get(system);

        final ActorMaterializer materializer = ActorMaterializer.create(system);

        RouteActor routeActor = new RouteActor(client,materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = routeActor.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8085),
                materializer
        );
        System.out.println("Server online at http://localhost:8085/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

}
