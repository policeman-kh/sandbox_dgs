package sandbox.dgs.component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class TracingInstrumentation extends SimpleInstrumentation {
    private static final String METRIC_NAME = "graphql.metrics";
    private final MeterRegistry meterRegistry;

    @Override
    public InstrumentationState createState() {
        return new TracingState();
    }

    @Override
    public InstrumentationContext<ExecutionResult> beginExecution(
            InstrumentationExecutionParameters parameters) {
        TracingState tracingState = parameters.getInstrumentationState();
        tracingState.sample = Timer.start(meterRegistry);
        return super.beginExecution(parameters);
    }

    @Override
    public DataFetcher<?> instrumentDataFetcher(DataFetcher<?> dataFetcher,
                                                InstrumentationFieldFetchParameters parameters) {
        // We only care about user code
        if (parameters.isTrivialDataFetcher()) {
            return dataFetcher;
        }
        return environment -> {
            Timer.Sample sample = Timer.start(meterRegistry);
            Object result = dataFetcher.get(environment);
            String[] tags = findDataFetcherTags(parameters);
            Timer timer = Timer.builder(METRIC_NAME)
                               .tags(List.of(Tag.of("type", tags[0]), Tag.of("method", tags[1])))
                               .register(meterRegistry);
            if (result instanceof CompletableFuture) {
                ((CompletableFuture<?>) result).whenComplete((r, ex) -> {
                    sample.stop(timer);
                });
            } else {
                sample.stop(timer);
            }
            return result;
        };
    }

    @Override
    public CompletableFuture<ExecutionResult> instrumentExecutionResult(ExecutionResult executionResult,
                                                                        InstrumentationExecutionParameters parameters) {
        TracingState tracingState = parameters.getInstrumentationState();
        tracingState.sample.stop(meterRegistry.timer(METRIC_NAME + ".total"));
        return super.instrumentExecutionResult(executionResult, parameters);
    }

    private String[] findDataFetcherTags(InstrumentationFieldFetchParameters parameters) {
        GraphQLOutputType type = parameters.getExecutionStepInfo().getParent().getType();
        GraphQLObjectType parent;
        if (type instanceof GraphQLNonNull) {
            parent = (GraphQLObjectType) ((GraphQLNonNull) type).getWrappedType();
        } else {
            parent = (GraphQLObjectType) type;
        }
        return new String[] { parent.getName(), parameters.getExecutionStepInfo().getPath().getSegmentName() };
    }

    static class TracingState implements InstrumentationState {
        Timer.Sample sample;
    }
}
