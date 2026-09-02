package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.validation.EntityValidation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {
    @Spy
    private EntityValidation entityValidation;
}
