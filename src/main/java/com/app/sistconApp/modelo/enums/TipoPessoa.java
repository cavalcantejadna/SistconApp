package com.app.sistconApp.modelo.enums;

public enum TipoPessoa {

	// @formatter:off
	F("Pessoa Física"),
	J("Pessoa Jurídica");
	// @formatter:on

	private final String nome;

	private TipoPessoa(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
