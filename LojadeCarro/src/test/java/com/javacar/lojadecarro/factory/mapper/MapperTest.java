package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.mapper.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MarcaMapperImpl.class,
        ModeloMapperImpl.class,
        CorMapperImpl.class,
        CombustivelMapperImpl.class,
        CarroceriaMapperImpl.class,
        ImagemMapperImpl.class,
        OpcionalMapperImpl.class,
        RoleMapperImpl.class,
        UsuarioMapperImpl.class,
        VeiculoMapperImpl.class,
        VendasMapperImpl.class})
public abstract class MapperTest {
}
