package com.edtech.processor.topology;

import com.edtech.processor.dto.ExamEvent;
import com.edtech.processor.dto.SuspicionScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProctoringTopology {

    @Value("${app.topic.exam-events}")
    private String examEventsTopic;

    @Value("${app.topic.suspicion-scores}")
    private String suspicionScoresTopic;

    @Autowired
    public void buildTopology(StreamsBuilder streamsBuilder) {
        JsonSerde<ExamEvent> eventSerde = new JsonSerde<>(ExamEvent.class);
        JsonSerde<SuspicionScore> scoreSerde = new JsonSerde<>(SuspicionScore.class);

        KStream<String, ExamEvent> eventStream = streamsBuilder.stream(
                examEventsTopic,
                Consumed.with(Serdes.String(), eventSerde));

        // Rule: 3 Tab Switches in 10s
        eventStream
                .filter((key, value) -> "TAB_SWITCH".equals(value.getEventType()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .filter((key, count) -> count >= 3)
                .map((key, count) -> {
                    String sessionId = key.key();
                    SuspicionScore score = SuspicionScore.builder()
                            .sessionId(sessionId)
                            .score(20.0 * count) // Simple logic: 20 points per violation block
                            .reason("High frequency tab switching detected: " + count + " times in 10s")
                            .timestamp(System.currentTimeMillis())
                            .build();
                    return new KeyValue<>(sessionId, score);
                })
                .to(suspicionScoresTopic, Produced.with(Serdes.String(), scoreSerde));

        // Rule: Focus Lost
        eventStream
                .filter((key, value) -> "FOCUS_LOST".equals(value.getEventType()))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(60)))
                .count()
                .toStream()
                .filter((key, count) -> count >= 5)
                .map((key, count) -> {
                    String sessionId = key.key();
                    SuspicionScore score = SuspicionScore.builder()
                            .sessionId(sessionId)
                            .score(15.0)
                            .reason("Frequent focus loss detected")
                            .timestamp(System.currentTimeMillis())
                            .build();
                    return new KeyValue<>(sessionId, score);
                })
                .to(suspicionScoresTopic, Produced.with(Serdes.String(), scoreSerde));
    }
}
