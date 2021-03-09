package br.ce.aquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {

	@Test
	public void deveVerificarPrimeiroNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/1")
			.then()
			.statusCode(200)
			.body ("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18)); //maior que 18
	}
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		  Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		  
		  //path
		  org.junit.Assert.assertEquals(new Integer(1), response.path("id")); // == "id": 1
		  org.junit.Assert.assertEquals(new Integer(1), response.path("%s","id")); //enviando o id via parametro (%s é uma string, e na sequencia passa a string que é um id)

		  //jsonpath
		  JsonPath jpath = new JsonPath(response.asString());
		  org.junit.Assert.assertEquals(1, jpath.getInt("id")); //valor atual é 1 e a propriedade que eu quero é o ID
		  
		  //from
		  int id = JsonPath.from(response.asString()).getInt("id");
		  Assert.assertEquals(1, id); // esperando que o 1 seja o que vem no ID	  
	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/2")
			.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"));
	}
	
	@Test
	public void deveVerificarLista() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/3")
			.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2)) //contém dois items dentro do array.
			.body("filhos[0].name", is("Zezinho")) // dentro do array na primeira posição, contem o nome 'Zezinho'
			.body("filhos[1].name", is("Luizinho")) // dentro do array na segunda posição, contem o nome 'Luizinho'
			.body("filhos.name", hasItem("Zezinho"))// dentro do array na primeira posição, contem o nome 'Zezinho'
			.body("filhos.name", hasItems("Zezinho", "Luizinho", "Qualquer")) //vai dar erro, pois 'Qualquer' não existe.
			;
	}
	
	@Test
	public void deveRetornarErroUsuarioInexistente() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
			;
	}
	
	@Test
	public void deveVerificarListaNaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()	
			.statusCode(200)
			.body("$", hasSize(3)) // Utilizado como convenção, significa que a procura se inicia na raiz do json
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
			;
	}
	
	@Test
	public void devoFazerVerificacoesAvancadas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()	
			.statusCode(200)
			.body("$", hasSize(3)) 
			.body("age.findAll{it <= 25}.size()", is(2))//idade menor ou igual a 25, existem 2 idades que serão apresentadas 20 e 25
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1)) //idade menor que 25 e maior que 20, só será apresentada a idade = 20
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina")) //idade menor ou igual a 25 e maior que 20 ,será apresentado apenas 25 e Maria Joaquina
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia")) // a busca se inicia do fim da raiz p/ o começo
			.body("find{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina")) //válida nomes com mais de 10 caracteres
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
			.body("age.collect{it * 2}", hasItems(60,50,40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}", allOf(greaterThan(3000d), lessThan(5000d)))
			
			;
	}
		@Test
		public void devoUnirJsonPathComJAVA () {
			ArrayList<String> names =
			given()
			.when()
				.get("https://restapi.wcaquino.me/users")
			.then()	
				.statusCode(200)
				.extract().path("name.findAll{it.startsWith('Maria')}");
			
			Assert.assertEquals(1, names.size());
			Assert.assertTrue(names.get(0).equalsIgnoreCase("mAria jOaquina"));
			Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
		}
	}
	
	

