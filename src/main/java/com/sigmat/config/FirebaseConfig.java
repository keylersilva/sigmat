package com.sigmat.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(FirebaseConfig.class);

    private static Firestore firestore;

    @PostConstruct
    public void initialize() {

        try {

            logger.info("Iniciando Firebase...");

            InputStream serviceAccount =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "firebase-service-account.json"
                            );

            if (serviceAccount == null) {

                logger.error(
                        "NO se encontró firebase-service-account.json"
                );

                return;
            }

            logger.info("Archivo JSON encontrado");

            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(
                                    GoogleCredentials.fromStream(
                                            serviceAccount
                                    )
                            )
                            .build();

            if (FirebaseApp.getApps().isEmpty()) {

                FirebaseApp.initializeApp(options);

                logger.info(
                        "FirebaseApp inicializado"
                );
            }

            firestore = FirestoreClient.getFirestore();

            logger.info(
                    "Firestore conectado correctamente"
            );

        } catch (Exception e) {

            logger.error(
                    "ERROR REAL FIREBASE:",
                    e
            );
        }
    }

    public static Firestore getFirestore() {

        return firestore;
    }
}