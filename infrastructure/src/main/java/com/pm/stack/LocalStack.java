package com.pm.stack;



import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LocalStack extends Stack {
    private final Vpc vpc; //virtual private cloud
    private final Cluster ecsCluster; //Elastic Container Service

    //AWS infrastructure code
    public LocalStack(final App scope, final String id, final StackProps props) {
        //Calling stack - boilerplate code to create a new stack
        super(scope, id, props);

        //Creating a VPC
        this.vpc = createVpc();

        //Creating a DB for AuthService microservice
        DatabaseInstance authServiceDB = createDatabaseInstance("AuthServiceDB", "auth-service-db");

        //Creating a DB for PatientService microservice
        DatabaseInstance patientServiceDB = createDatabaseInstance("PatientServiceDB", "patient-service-db");

        //Creating a Route 53 Health Check for the DB of AuthService Microservice
        CfnHealthCheck authDBHealthCheck = createDBHealthCheck(authServiceDB, "AuthServiceDBHealthCheck");

        //Creating a Route 53 Health Check for the DB of PatientService Microservice
        CfnHealthCheck patientDBHealthCheck = createDBHealthCheck(patientServiceDB, "PatientServiceDBHealthCheck");

        //Creating a MSK
        CfnCluster mskCluster = createMSKCluster();

        //Creating an ECS
        this.ecsCluster = createECSCluster();

        //Creating an AuthService ECS Fargate Task
        FargateService authService = createFargateService(
                "AuthService",
                "auth-service",
                List.of(4004),
                authServiceDB,
                Map.of("JWT_SECRET", "e74fb4a2df7167ac74527e04a09b02011d7c042e6a60b1a77b3125c3daffade1262f89d5659aa7444a74cf921299713df399c539a37ccd8782071d31ee70dd8d8ceb1b68478548a2c8a1038cf7db6fc191ce5a4d1a13fe8b90844d8c8e1452e40edb13762bfae337bf106afc9fb5c56c090c87b46afa3ebfd8bb0a024f07e4ee8a3e2444d1018d1441580d238429936a6fabfb7548b2c8ba5053b29549e2ab4a7a9ed57b14e28b4d5e8a14ebcdea080e170c8d9be69fbd709a24883aa50dff9a37f6268d0195a65ac1fea59170adc4932a9478cb3a826e973a02c2a4bd103afd988224dde62eb6a9c7403cea6ccc8cd319f0f5f9ff78f159e70f9a4bd87042b3")
        );

        //Making sure the authDBHealthCheck is created before creating the ECS Task
        authService.getNode().addDependency(authDBHealthCheck);

        //Making sure the authServiceDB is created before creating the ECS Task
        authService.getNode().addDependency(authServiceDB);

        //Creating an PatientService ECS Fargate Task
        FargateService analyticsService = createFargateService(
                "AnalyticsService",
                "analytics-service",
                List.of(4002),
                null,
                null
        );

        //Making sure the MSK Cluster (KAFKA) is created before creating the ECS Task
        analyticsService.getNode().addDependency(mskCluster);

        //Creating an PatientService ECS Fargate Task
        FargateService billingService = createFargateService(
                "BillingService",
                "billing-service",
                List.of(4001,9001),
                null,
                null

        );

        //Creating an PatientService ECS Fargate Task
        FargateService patientService = createFargateService(
                "PatientService",
                "patient-service",
                List.of(4000),
                patientServiceDB,
                Map.of(
                        "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                        "BILLING_SERVICE_GRPC_PORT", "9001"
                )
        );

        //Making sure the patientDBHealthCheck is created before creating the ECS Task
        patientService.getNode().addDependency(patientDBHealthCheck);

        //Making sure the patientServiceDB is created before creating the ECS Task
        patientService.getNode().addDependency(patientServiceDB);

        //Making sure the billingService is created before the ECS Task
        patientService.getNode().addDependency(billingService);

        //Making sure the MSK Cluster (KAFKA) is created before creating the ECS Task
        patientService.getNode().addDependency(mskCluster);

        //Creating an APIGatewayService ECS Fargate Task with also a Application Load Balancer
        createAPIGatewayService();
    }

    //creating VPC instance
    private Vpc createVpc() {
        return Vpc.Builder
                // Create a VPC in this CDK construct
                .create(this,"PatientManagementVPC")
                // Name of VPC
                .vpcName("PatientManagementVPC")
                // Spread subnets across up to 2 Availability Zones
                .maxAzs(2)
                .build();
    }

    //creating an RDS DB instance that uses POSTGRES Version: 17.2
    private DatabaseInstance createDatabaseInstance(String id, String dbName) {
        return DatabaseInstance.Builder
                // Create an RDS DB in this CDK construct
                .create(this, id)
                // Selecting the language and version
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_17_2)
                        .build()
                ))
                // Connecting DB to VPC
                .vpc(vpc)
                // Setting compute levels
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                // Setting storage space
                .allocatedStorage(20)
                // Creating a user
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                // Setting DB name
                .databaseName(dbName)
                //Deletes data on DB if deleting stack
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    // Creating a Route 53 Health Check instance
    private CfnHealthCheck createDBHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
                .create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        // Getting port from DB
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        //Getting IP Address from DB
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        // Setting how often to check (30 Secs)
                        .requestInterval(30)
                        // Setting a max amount of times it can fail before reporting
                        .failureThreshold(3)
                        .build())
                .build();
    }

    // Creating an MSK instance - meant for Apache Kafka
    private CfnCluster createMSKCluster(){
        return CfnCluster.Builder
                .create(this, "MSKCluster")
                // Setting name
                .clusterName("kafka-cluster")
                // Setting version
                .kafkaVersion("2.8.0")
                // Setting num of broker notes
                .numberOfBrokerNodes(4)
                // Setting Broker info
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        //Type of broker instance
                        .instanceType("kafka.m5.xlarge")
                        // Providing subnet ids
                        .clientSubnets(vpc.getPrivateSubnets().stream()
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList())
                        )
                        // How to distribute brokers based on region
                        .brokerAzDistribution("DEFAULT")
                        .build()
                )
                .build();
    }

    //Creating an ECS instance
    private Cluster createECSCluster() {
        return Cluster.Builder
                .create(this, "PatientManagementCluster")
                .vpc(vpc)
                // Setting what is basically part of a URI EX: auth-service.patient-management.local
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build()
                )

                .build();
    }

    // Creating a Fargate Instance - meant to create ECS Services
    private FargateService createFargateService(
            String id,
            String imageName,
            List<Integer> ports,
            DatabaseInstance db,
            Map<String, String> additionalEnvVars
    ) {
        //ECS Task is the one that runs the container therefore we need to create an ECS Task.
        //To create an ECS Task we need an ECS Task Definition (basically a blueprint)

        //Creating an FargateTaskDefinition
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, id + "Task")
                //Setting the CPU value
                .cpu(256)
                //Setting the Memory
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerOptions =
                ContainerDefinitionOptions.builder()
                        //Setting the image info (where we are getting the image from)
                        .image(ContainerImage.fromRegistry(imageName))
                        //Setting up port info
                        .portMappings(ports.stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        //Building a Logger
                        .logging(LogDriver.awsLogs(
                                AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                                .logGroupName("/ecs/" + imageName)
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .retention(RetentionDays.ONE_DAY)
                                                .build())
                                        .streamPrefix(imageName)
                                .build())
                        );

        //Creating environment variables
        Map<String, String> envVars = new HashMap<>();

        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS",
                "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        // adding all the other environment variables
        if(additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars);
        }
        //setting environment variables that are related to DB
        if(db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName
            ));

            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        //adding the environment variables
        containerOptions.environment(envVars);

        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    //Creating a ApplicationLoadBalancerFargateService - basically the same as a FargateService difference is that its
    // also creating an application load balancer
    private void createAPIGatewayService(){
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, "APIGatewayTaskDefinition")
                //Setting the CPU value
                .cpu(256)
                //Setting the Memory
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions containerOptions =
                ContainerDefinitionOptions.builder()
                        //Setting the image info (where we are getting the image from)
                        .image(ContainerImage.fromRegistry("api-gateway"))
                        //Setting environment variables
                        .environment(Map.of(
                                "SPRING_PROFILE_ACTIVE", "prod",
                                "AUTH_SERVICE_URL", "http://host.docker.internal:4004"
                                )
                        )
                        //Setting up port info
                        .portMappings(Stream.of(4003)
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        //Building a Logger
                        .logging(LogDriver.awsLogs(
                                AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this, "APIGatewayLogGroup")
                                                .logGroupName("/ecs/api-gateway")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .retention(RetentionDays.ONE_DAY)
                                                .build())
                                        .streamPrefix("api-gateway")
                                        .build())
                        )
                .build();

        taskDefinition.addContainer("APIGatewayContainer", containerOptions );

        ApplicationLoadBalancedFargateService apiGateway = ApplicationLoadBalancedFargateService.Builder
                .create(this, "APIGatewayService")
                .cluster(ecsCluster)
                .serviceName("api-gateway")
                .taskDefinition(taskDefinition)
                .desiredCount(1)
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();


    }

    public static void main (final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps stackProps = StackProps.builder()
                //converting into a cloud template
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", stackProps);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}