package com.amazonaws.samples;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ListStreamsRequest;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.model.StreamDescription;

public class AmazonKinesisRecordProducerSample {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\jolc2\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */
    
	private static AmazonKinesis kinesis;

    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\jolc2\\.aws\\credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\jolc2\\.aws\\credentials), and is in valid format.",
                    e);
        }

        kinesis = AmazonKinesisClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion("us-east-1")
            .build();
    }

    public static void main(String[] args) throws Exception {
        init();

        //final String myStreamName = AmazonKinesisApplicationSample.SAMPLE_APPLICATION_STREAM_NAME;
        
        //Name of Stream (Jorge - 04/11/2018.)
        final String myStreamName = "Java-K450-V5";
        final Integer myStreamSize = 450;

        // Describe the stream and check if it exists.
        DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest().withStreamName(myStreamName);
        try {
            StreamDescription streamDescription = kinesis.describeStream(describeStreamRequest).getStreamDescription();
            System.out.printf("Stream %s has a status of %s.\n", myStreamName, streamDescription.getStreamStatus());

            if ("DELETING".equals(streamDescription.getStreamStatus())) {
                System.out.println("Stream is being deleted. This sample will now exit.");
                System.exit(0);
            }

            // Wait for the stream to become active if it is not yet ACTIVE.
            if (!"ACTIVE".equals(streamDescription.getStreamStatus())) {
                waitForStreamToBecomeAvailable(myStreamName);
            }
        } catch (ResourceNotFoundException ex) {
            System.out.printf("Stream %s does not exist. Creating it now.\n", myStreamName);

            // Create a stream. The number of shards determines the provisioned throughput.
            CreateStreamRequest createStreamRequest = new CreateStreamRequest();
            createStreamRequest.setStreamName(myStreamName);
            createStreamRequest.setShardCount(myStreamSize);
            kinesis.createStream(createStreamRequest);
            // The stream is now being created. Wait for it to become active.
            waitForStreamToBecomeAvailable(myStreamName);
        }

        // List all of my streams.
        ListStreamsRequest listStreamsRequest = new ListStreamsRequest();
        listStreamsRequest.setLimit(10);
        ListStreamsResult listStreamsResult = kinesis.listStreams(listStreamsRequest);
        List<String> streamNames = listStreamsResult.getStreamNames();
        while (listStreamsResult.isHasMoreStreams()) {
            if (streamNames.size() > 0) {
                listStreamsRequest.setExclusiveStartStreamName(streamNames.get(streamNames.size() - 1));
            }

            listStreamsResult = kinesis.listStreams(listStreamsRequest);
            streamNames.addAll(listStreamsResult.getStreamNames());
        }
        // Print all of my streams.
        System.out.println("List of my streams: ");
        for (int i = 0; i < streamNames.size(); i++) {
            System.out.println("\t- " + streamNames.get(i));
        }

        //04/11/2018 Jorge Castro
        System.out.println("Getting Connection with Database ");
        ArrayList<String> items = new ArrayList<>();
        
        // 1.Get a Connection to database
           Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/kinesis?useSSL=false&createDatabaseIfNotExist=true", "root", "root");
        // 2. Create a statement
           Statement myStmt = myConn.createStatement();
        // 3. Execute a SQL query
           ResultSet myRs = myStmt.executeQuery("select * from alve_csv");
        // 4.Process the result set.
           System.out.println("Getting Data ");		
           while (myRs.next())
			{
				
        	   items.add(
        			   
        			   myRs.getString("rid") + ", " + 
        	           myRs.getString("uid") + ", " + 
        			   myRs.getString("aid") + ", " + 
        	           myRs.getString("oid") + ", " + 
        			   myRs.getString("Updated") + ", " +
        			   myRs.getString("Symbol") + ", " +
        			   myRs.getString("Price") + ", " +
        			   myRs.getString("Quantity") + ", " +
        			   myRs.getString("Side") + ", " +
        			   myRs.getString("OpenType") + ", " +
        			   myRs.getString("CloseType") + ", " +
        			   myRs.getString("Type") + ", " +
        			   myRs.getString("Status") + ", " +
        			   myRs.getString("TimeInForce") + ", " +
        			   myRs.getString("ExpirationDate") + ", " +
        			   myRs.getString("OpenDate") + ", " +
        			   myRs.getString("FillDate") + ", " +
        			   myRs.getString("CloseDate") + ", " +
        			   myRs.getString("CloseDateSec") + ", " +
        			   myRs.getString("CloseDateNew") + ", " +
        			   myRs.getString("TradeCommission") + ", " +
        			   myRs.getString("AgentCommission") + ", " +
        			   myRs.getString("Swap") 
        			   //myRs.getString("Comment")
        			   
        			   );   			   
        	    
			}
        
        //Put Records (Jorge 04/11/2018)
        System.out.printf("Putting records in stream : %s until this application is stopped...\n", myStreamName);
        System.out.println("Press CTRL-C to stop.");
        // Write records to the stream until this program is aborted.
        
        //Multiple record
        AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
        clientBuilder.setRegion("us-east-1");
        AmazonKinesis kinesisClient = clientBuilder.build();
        
        PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
        putRecordsRequest.setStreamName(myStreamName);
        List <PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
        
        int j = 0;
        int count = items.size();
        
        
        while(j != items.size() ) 
        {
        	
        	
        		for (int i = 0; i < 100; i++) 
        		
        		{
        			PutRecordsRequestEntry putRecordsRequestEntry  = new PutRecordsRequestEntry();
        			putRecordsRequestEntry.setData(ByteBuffer.wrap(String.format(items.get(j)).getBytes()));
        			putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", j));
        			putRecordsRequestEntryList.add(putRecordsRequestEntry);
        			j++;
        			count--;
        		
        		}
        		
        		putRecordsRequest.setRecords(putRecordsRequestEntryList);
        		PutRecordsResult putRecordsResult  = kinesisClient.putRecords(putRecordsRequest);
        		System.out.println("Put Result" + " " + j  + putRecordsResult );
        		putRecordsRequestEntryList.clear();
        
        	 }
        	
       }
    
    private static void waitForStreamToBecomeAvailable(String myStreamName) throws InterruptedException {
        System.out.printf("Waiting for %s to become ACTIVE...\n", myStreamName);

        long startTime = System.currentTimeMillis();
        long endTime = startTime + TimeUnit.MINUTES.toMillis(10);
        while (System.currentTimeMillis() < endTime) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(20));

            try {
                DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
                describeStreamRequest.setStreamName(myStreamName);
                // ask for no more than 10 shards at a time -- this is an optional parameter
                describeStreamRequest.setLimit(10);
                DescribeStreamResult describeStreamResponse = kinesis.describeStream(describeStreamRequest);

                String streamStatus = describeStreamResponse.getStreamDescription().getStreamStatus();
                System.out.printf("\t- current state: %s\n", streamStatus);
                if ("ACTIVE".equals(streamStatus)) {
                    return;
                }
            } catch (ResourceNotFoundException ex) {
                // ResourceNotFound means the stream doesn't exist yet,
                // so ignore this error and just keep polling.
            } catch (AmazonServiceException ase) {
                throw ase;
            }
        }

        throw new RuntimeException(String.format("Stream %s never became active", myStreamName));
    }
}
