package com.cogover.template.server.network.rpc_server.handler.interceptor;

import com.cogover.rpc.server.handler.HandlerInterceptor;
import com.cogover.rpc.server.packet.request.RpcRequest;
import com.cogover.rpc.server.packet.response.RpcResponse;
import com.cogover.template.server.Start;
import io.netty.util.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import lombok.extern.log4j.Log4j2;

/**
 * @author huydn on 15/9/24 21:56
 */
@Log4j2
public class TracerInterceptor implements HandlerInterceptor {

    private static final AttributeKey<Span> ROOT_SPAN = AttributeKey.valueOf("ROOT_SPAN");

    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response) {
        log.info("================== preHandle AddTracer is Calling");

        String traceId = null;
        String spanId = null;
        if (request.getBody() != null) {
//            traceId = request.getBody().optString("traceId", null);
//            spanId = request.getBody().optString("spanId", null);
        }

        Span rootSpan;
        if (traceId == null || spanId == null) {
            rootSpan = Start.tracer
                    .spanBuilder("Request: service=" + request.getService() + ", id=" + request.getId())
                    .startSpan();
            rootSpan.setAttribute("root", true);
        } else {
            //neu nhan duoc traceId va spanId tu module khac truyen qua: day la sub-request (ko phai request goc)
            SpanContext parentSpanContext = SpanContext.createFromRemoteParent(
                    traceId,
                    spanId,
                    TraceFlags.getSampled(),
                    TraceState.getDefault()
            );
            Context parentContext = Context.root().with(Span.wrap(parentSpanContext));
            rootSpan = Start.tracer.spanBuilder("child").setParent(parentContext).startSpan();
            rootSpan.setAttribute("root", false);
        }

        if (request.getBody() != null) {
            //rootSpan.setAttribute("request", request.getBody().toString());
            Attributes requestAttributes = Attributes.builder()
                    .put("request.body", request.getBody().toString())
                    .build();
            rootSpan.addEvent("Request received", requestAttributes);
        }

        rootSpan.makeCurrent();
        request.setAttribute(ROOT_SPAN, rootSpan);

        return true;
    }

    @Override
    public void postHandle(RpcRequest request, RpcResponse response) {
        Span rootSpan = TracerInterceptor.rootSpan(request);

        if (request.getBody() != null) {
            //rootSpan.setAttribute("response", response.getBody().toString());
            Attributes requestAttributes = Attributes.builder()
                    .put("request.body", response.getBody().toString())
                    .build();
            rootSpan.addEvent("Response sent", requestAttributes);
        }

        rootSpan.end();
        log.info("==================postHandle AddTracer is Calling");
    }

    public static Span rootSpan(RpcRequest request) {
        return request.getAttribute(ROOT_SPAN);
    }
}
