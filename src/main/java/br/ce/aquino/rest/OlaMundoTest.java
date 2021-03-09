package br.ce.aquino.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertTrue("O status code deveria ser 201", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());	 // espera 201 e está retornando 200 da aplicação

		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}
	
	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		
		//Forma 1
		Response response = request(Method.GET, "http://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
		//Forma 2
		get("http://restapi.wcaquino.me/ola").then().statusCode(200);
		
		//Forma 3	
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
			.then()
			//.assertThat()
			.statusCode(200);
	}
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		//trabalhando com igualdade
		
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		Assert.assertThat(128d, Matchers.greaterThan(120d)); // 128 > 120
		Assert.assertThat(128d, Matchers.lessThan(130d)); //128 < 130
		
		//trabalhando com listas
		List <Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, hasSize(5)); //verificando se a coleção é de 5 elementos
		assertThat(impares, contains(1,3,5,7,9));
		assertThat(impares, containsInAnyOrder(3,7,1,9,5)); //em qualquer ordem
		assertThat(impares, hasItem(7)); // contem qualquer um dos elementos da lista
		assertThat(impares, hasItems(7,3)); // contem alguns elementos da lista
		
		// trabalhando com varias acertivas dentro de uma mesma logica
		assertThat("Maria", is(not("João")));
		assertThat("Maria", not("João")); //igual o de cima, sem os 'is'
		assertThat("Joaquina", anyOf(is("Maria"), is ("Joaquina"))); // pode ser 'Maria' ou 'Joaquina'
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui"))); //todas as condições que eu colocar devem ser satisfeitas
	}
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
			.then()
				.statusCode(200) // validação dos mais restrivos, aos menos restritivos
				.body(is("Ola Mundo!"))
				.body(containsString("Mundo"))
				.body(is(not(nullValue())));
	}
	
}
