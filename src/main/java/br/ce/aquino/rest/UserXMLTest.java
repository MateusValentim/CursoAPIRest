package br.ce.aquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;
import org.junit.Test;

public class UserXMLTest {
	
	@Test
	public void devoTrabalharComXML() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.statusCode(200)
			
			.rootPath("user") //n� na raiz iniciando no - user -
			.body("name", is("Ana Julia"))
			.body("@id", is("3")) // para se referenciar a um atributo deve se usar o @
			
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			.detachRootPath("filhos") //agora tenho de declarar filhos novamente
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos") 
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho", "Zezinho"));
	}
	
	@Test
	public void devoFazerPesquisasAvancadasComXML () {
		given()
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(200)
			.body("users.user.size", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size", is(2))
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia")) //explicitar que o retorno � uma string
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}
}