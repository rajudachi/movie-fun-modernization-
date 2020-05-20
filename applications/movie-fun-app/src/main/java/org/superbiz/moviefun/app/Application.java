package org.superbiz.moviefun.app;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.superbiz.moviefun.albums.blobstore.BlobStore;
import org.superbiz.moviefun.albums.blobstore.S3Store;
import org.superbiz.moviefun.MovieServlet;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
//@SpringBootApplication
@ComponentScan({"org.superbiz.moviefun","org.superbiz.moviefun", "org.superbiz.moviefun.albums"})
@Configuration
@EnableJpaRepositories(basePackages = {"org.superbiz.moviefun.movies","org.superbiz.moviefun.albums","org.superbiz.moviefun"})
@EnableTransactionManagement
public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);


    public static void main(String... args) {
        logger.info("===============Starting App==================");
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public ServletRegistrationBean actionServletRegistration(MovieServlet movieServlet) {
        return new ServletRegistrationBean(movieServlet, "/moviefun/*");
    }

    @Bean
    ServiceCredentials serviceCredentials(@Value("${vcap.services}") String vcapServices) {
        return new ServiceCredentials(vcapServices);
    }

    @Bean
    public BlobStore blobStore(
        ServiceCredentials serviceCredentials,
        @Value("${vcap.services.photo-storage.credentials.endpoint:#{null}}") String endpoint
    ) {
        String photoStorageAccessKeyId = serviceCredentials.getCredential("photo-storage", "user-provided", "access_key_id");
        String photoStorageSecretKey = serviceCredentials.getCredential("photo-storage", "user-provided", "secret_access_key");
        String photoStorageBucket = serviceCredentials.getCredential("photo-storage", "user-provided", "bucket");

        AWSCredentials credentials = new BasicAWSCredentials(photoStorageAccessKeyId, photoStorageSecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        if (endpoint != null) {
            s3Client.setEndpoint(endpoint);
        }

        return new S3Store(s3Client, photoStorageBucket);
    }


}
