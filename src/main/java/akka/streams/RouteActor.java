package akka.streams;

import akka.NotUsed;
import akka.http.javadsl.model.*;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.AsyncHttpClient;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.Query;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RouteActor {
    private ActorMaterializer materializer;
    RouteActor(ActorMaterializer materializer){
        this.materializer = materializer;
    }
    public Flow<HttpRequest,HttpResponse, NotUsed> createRoute(){
        return Flow.of(HttpRequest.class)
                .map(this::parseQuery)
                .mapAsync(5,this::sendRequest)
                .map(this::convertIntoResponse);
    }

    private Request parseQuery(HttpRequest req){
        Query qry = req.getUri().query();
        Optional<String> url = qry.get("testUrl");
        Optional<String> count = qry.get("count");
        System.out.println(url.get());
        System.out.println(count.get());
        return new Request(url.get(),count.get());
    }

    private CompletionStage<SaveResultMessage> checkRequest(Request r){
        return Patternsa
    }

    private CompletionStage<SaveResultMessage> sendRequest(Request r){
        Sink<Request,CompletionStage<Long>> testSink =
                Flow.<Request>create()
                .mapConcat(t-> Collections.nCopies(t.getCount(),t))
                .mapAsync(5,this::getTime)
                .toMat(Sink.fold(0L, (agg, next) -> agg + next),  Keep.right());
        return Source.from(Collections.singletonList(r))
                .toMat(testSink, Keep.right())
                .run(materializer)
                .thenApply(res->new SaveResultMessage(r,res/r.getCount()));
    }


    private CompletionStage<Long> getTime(Request r){
        long start = System.currentTimeMillis();
        AsyncHttpClient client = Dsl.asyncHttpClient();
        CompletionStage<Long> whenResponse = client.prepareGet(r.getUrl()).execute()
                .toCompletableFuture()
                .thenCompose(w -> CompletableFuture.completedFuture(
                        System.currentTimeMillis()-start
                ));
        return  whenResponse;
    }
    private HttpResponse convertIntoResponse(SaveResultMessage r){
        HttpResponse res = HttpResponse
                .create()
                .withEntity(ContentTypes.APPLICATION_JSON, ByteString.fromString(r.getRequest().getUrl()+" "+r.getRequest().getCount()+
                        " "+r.getResult()+"\n"));
        return  res;
    }
}
