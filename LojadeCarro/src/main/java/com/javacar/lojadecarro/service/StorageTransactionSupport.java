package com.javacar.lojadecarro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageTransactionSupport {
    private final StorageService storageService;

    public void deleteOnRollback(String objectKey) {
        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            log.warn(
                    "Não existe sincronização transacional ativa para o objeto: {}",
                    objectKey
            );

            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            deleteQuietly(objectKey);
                        }
                    }
                }
        );
    }

    private void deleteQuietly(String objectKey) {
        try {
            storageService.delete(objectKey);
        } catch (IOException | RuntimeException exception) {
            log.error(
                    "Não foi possível remover do storage o objeto: {}",
                    objectKey,
                    exception
            );
        }
    }

    public void deleteAfterCommit(String objectKey) {
        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            log.warn(
                    "Não existe sincronização transacional ativa para o objeto: {}",
                    objectKey
            );

            deleteQuietly(objectKey);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        deleteQuietly(objectKey);
                    }
                }
        );
    }
}
