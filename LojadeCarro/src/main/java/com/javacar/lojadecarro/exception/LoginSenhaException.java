package com.javacar.lojadecarro.exception;

public class LoginSenhaException extends SecurityException {
	public LoginSenhaException() {
		super("Usuário ou senha inválidos.");
	}
}
