package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.mapper.ImagemMapper;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import com.javacar.lojadecarro.repository.VeiculoRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public abstract class AbstractVeiculoServiceTest extends BaseServiceTest {
    @Mock
    protected VeiculoRepository veiculoRepository;

    @Mock
    protected VeiculoMapper veiculoMapper;

    @Mock
    protected ImagemMapper imagemMapper;

    @Mock
    protected CarroceriaService carroceriaService;

    @Mock
    protected CoresService coresService;

    @Mock
    protected ModeloService modeloService;

    @Mock
    protected UsuarioService usuarioService;

    @Mock
    protected CombustivelService combustivelService;

    @Mock
    protected OpcionalService opcionalService;

    @Mock
    protected ImagensService imagensService;

    @InjectMocks
    protected VeiculoService veiculoService;
}
