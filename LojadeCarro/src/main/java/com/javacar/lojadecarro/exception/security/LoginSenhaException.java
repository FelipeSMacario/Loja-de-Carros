package com.javacar.lojadecarro.exception.security;

public class LoginSenhaException extends SecurityException {
	public LoginSenhaException() {
		super("Usuário ou senha inválidos.");
	}
}
