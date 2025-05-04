package com.cogover.template.server.config;

import io.opentelemetry.api.OpenTelemetry;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * https://github.com/open-telemetry/opentelemetry-java-examples#java-opentelemetry-examples
 * <p>
 * https://github.com/open-telemetry/opentelemetry-java-examples/tree/main/jaeger
 *
 * @author huydn on 15/9/24 15:12
 */
@Log4j2
public class OpenTelemetryConfig {

    /**
     * @param jaegerEndpoint http://localhost:4317 (OtlpGrpcSpanExporter)
     */
    public static OpenTelemetry initOpenTelemetry(String jaegerEndpoint, boolean enabled) {
        if (!enabled) {
            log.info("OpenTelemetry is disabled");
            return OpenTelemetry.noop();
        }

        log.info("OpenTelemetry is enabled");

        Resource serviceNameResource = Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "auth-server-1"));

        // Export traces over OTLP
        OtlpGrpcSpanExporter otlpExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(jaegerEndpoint)
                .setTimeout(30, TimeUnit.SECONDS)
                .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(otlpExporter).build())
                .setResource(Resource.getDefault().merge(serviceNameResource))
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

        return openTelemetry;
    }

}
