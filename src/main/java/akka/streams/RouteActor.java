package akka.streams;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.http.javadsl.model.*;
import akka.http.scaladsl.model.Uri;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import javafx.util.Pair;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.asynchttpclient.AsyncHttpClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

public class RouteActor {
    private ActorMaterializer materializer;
    private AsyncHttpClient client;


    RouteActor(AsyncHttpClient client,ActorMaterializer materializer){
        this.materializer = materializer;
        this.client = client;
    }
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        System.out.println("CREATING ROUTE STAGE");
        return Flow.of(HttpRequest.class)
                .map(this::parseQuery)
                .mapAsync(5,this::sendRequest)
                .map(this::convertIntoResponse);
    }

    private Request parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        Optional<String> url = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        System.out.println(url.get()+" "+ count.get());
        return new Request(url.get(),count.get());
    }

    private CompletionStage<Long> sendRequest(Request r){
        Sink<Request,CompletionStage<Long>> testSink =
                Flow.<Request>create()
                .mapConcat(t-> Collections.nCopies(t.getCount(),t))
                .mapAsync(5,this::getTime)
                .toMat(Sink.fold(0L, (agg, next) -> agg + next),  Keep.right());
        return Source.from(Collections.singletonList(r))
                .toMat(testSink, Keep.right()).run(materializer);
    }


    private CompletionStage<Long> getTime(Request r){
        Instant startTime = Instant.now();

        CompletionStage<Long> whenResponse = client.prepareGet(r.getUrl()).execute()
                .toCompletableFuture()
                .thenCompose(w -> CompletableFuture.completedFuture(
                        Duration.between(startTime,Instant.now()).getSeconds()
                ));
        return  whenResponse;
    }
    private HttpResponse convertIntoResponse(Long r){
        HttpResponse res = HttpResponse
                .create()
                .withEntity(ContentTypes.APPLICATION_JSON, ByteString.fromString(String.valueOf(r)));
        return  res;
    }
}
