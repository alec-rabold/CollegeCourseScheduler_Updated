package io.collegeplanner.my.collegecoursescheduler.service;

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class FirehoseStreamService {

    @Autowired
    private final AmazonKinesisFirehose firehoseClient;

    public FirehoseStreamService(final AmazonKinesisFirehose firehoseClient) {
        this.firehoseClient = firehoseClient;
    }

    public void addToStream(final String streamName, final String message) {
        final Record record = new Record().withData(ByteBuffer.wrap(message.getBytes()));
        final PutRecordRequest putRecordRequest = new PutRecordRequest()
                .withDeliveryStreamName(streamName)
                .withRecord(record);
        firehoseClient.putRecord(putRecordRequest);
    }
}
